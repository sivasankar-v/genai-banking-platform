package com.fincore.service.impl;

import com.fincore.dto.AccountResponse;
import com.fincore.dto.OpenAccountRequest;
import com.fincore.entity.Account;
import com.fincore.entity.Customer;
import com.fincore.entity.Transaction;
import com.fincore.enums.AccountStatus;
import com.fincore.enums.CustomerStatus;
import com.fincore.enums.TransactionStatus;
import com.fincore.enums.TransactionType;
import com.fincore.exception.BusinessException;
import com.fincore.exception.ResourceNotFoundException;
import com.fincore.mapper.AccountMapper;
import com.fincore.repository.AccountRepository;
import com.fincore.repository.CustomerRepository;
import com.fincore.repository.TransactionRepository;
import com.fincore.security.access.AccessGuard;
import com.fincore.service.AccountService;
import com.fincore.util.ReferenceGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;
    private final AccountMapper accountMapper;
    private final AccessGuard accessGuard;

    @Override
    public AccountResponse openAccount(OpenAccountRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId()).orElseThrow(
                () -> new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId())
        );
        accessGuard.checkCustomer(customer);
        if (customer.getStatus() != CustomerStatus.ACTIVE) {
            throw new BusinessException("Cannot open an account for a customer who is not active");
        }

        Account account = new Account();
        account.setAccountNumber(generateAccountNumber());
        account.setAccountType(request.getAccountType());
        account.setStatus(AccountStatus.ACTIVE);
        account.setBalance(request.getInitialDeposit());
        account.setCurrency(request.getCurrency().toUpperCase());
        account.setCustomer(customer);
        Account savedAccount = accountRepository.save(account);

        // Record the opening deposit so the balance matches the transaction history.
        if (request.getInitialDeposit().compareTo(BigDecimal.ZERO) > 0) {
            transactionRepository.save(Transaction.builder()
                    .transactionId(generateTransactionId())
                    .transactionType(TransactionType.DEPOSIT)
                    .transactionStatus(TransactionStatus.SUCCESS)
                    .amount(request.getInitialDeposit())
                    .description("Initial deposit")
                    .destinationAccount(savedAccount)
                    .build());
        }
        return accountMapper.toResponse(savedAccount);
    }

    private String generateAccountNumber() {
        String accountNumber;
        do {
            accountNumber = ReferenceGenerator.next("ACC");
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }

    private String generateTransactionId() {
        String transactionId;
        do {
            transactionId = ReferenceGenerator.next("TXN");
        } while (transactionRepository.existsByTransactionId(transactionId));
        return transactionId;
    }

    private Account findAccount(UUID id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse getAccountById(UUID id) {
        Account account = findAccount(id);
        accessGuard.checkAccount(account);
        return accountMapper.toResponse(account);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse getAccountByNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with number: " + accountNumber));
        accessGuard.checkAccount(account);
        return accountMapper.toResponse(account);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponse> getAccountsByCustomerId(UUID customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
        accessGuard.checkCustomer(customer);
        return accountRepository.findByCustomerId(customerId).stream().map(accountMapper::toResponse).toList();
    }

    @Override
    public AccountResponse freezeAccount(UUID id) {
        Account account = findAccount(id);
        if (account.getStatus() == AccountStatus.FROZEN) {
            throw new BusinessException("Account is already frozen");
        }
        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new BusinessException("Cannot freeze a closed account");
        }
        account.setStatus(AccountStatus.FROZEN);
        return accountMapper.toResponse(accountRepository.save(account));
    }

    @Override
    public AccountResponse closeAccount(UUID id) {
        Account account = findAccount(id);
        accessGuard.checkAccount(account);
        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new BusinessException("Account is already closed");
        }
        if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessException("Account cannot be closed while balance is greater than zero");
        }
        account.setStatus(AccountStatus.CLOSED);
        return accountMapper.toResponse(accountRepository.save(account));
    }
}
