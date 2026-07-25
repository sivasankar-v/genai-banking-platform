package com.fincore.service.impl;

import com.fincore.dto.AccountResponse;
import com.fincore.dto.OpenAccountRequest;
import com.fincore.entity.Account;
import com.fincore.entity.Customer;
import com.fincore.enums.AccountStatus;
import com.fincore.exception.BusinessException;
import com.fincore.exception.ResourceNotFoundException;
import com.fincore.mapper.AccountMapper;
import com.fincore.repository.AccountRepository;
import com.fincore.repository.CustomerRepository;
import com.fincore.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final AccountMapper accountMapper;


    @Override
    public AccountResponse openAccount(OpenAccountRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId()).orElseThrow(
                () -> new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId())
        );

        Account account = new Account();
        account.setAccountNumber(generateAccountNumber());
        account.setAccountType(request.getAccountType());
        account.setStatus(AccountStatus.ACTIVE);
        account.setBalance(request.getInitialDeposit());
        account.setCurrency(request.getCurrency().toUpperCase());
        account.setCustomer(customer);
        Account savedAccount = accountRepository.save(account);
        return accountMapper.toResponse(savedAccount);
    }

    private String generateAccountNumber() {
        String accountNumber;
        do{
            accountNumber = "ACC" + System.currentTimeMillis();
        }while(accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }

    @Override
    public AccountResponse getAccountById(UUID id) {
        Account account = accountRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Account not found with id: " + id));
        return accountMapper.toResponse(account);
    }

    @Override
    public AccountResponse getAccountByNumber(String accountNumber) {
        Account account = accountRepository
                .findByAccountNumber(accountNumber)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Account not found with id: " + accountNumber)
                );
        return accountMapper.toResponse(account);
    }

    @Override
    public List<AccountResponse> getAccountsByCustomerId(UUID customerId) {
        List<Account> accounts = accountRepository.findByCustomerId(customerId);
        return accounts.stream().map(accountMapper::toResponse).toList();
    }

    @Override
    public AccountResponse freezeAccount(UUID id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
        if(account.getStatus() == AccountStatus.FROZEN){
            throw new BusinessException("Account is already frozen");
        }
        if(account.getStatus() == AccountStatus.CLOSED){
            throw new BusinessException("Cannot freeze a closed account");
        }

        account.setStatus(AccountStatus.FROZEN);
        return accountMapper.toResponse(accountRepository.save(account));
    }

    @Override
    public AccountResponse closeAccount(UUID id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(
                        ()  -> new ResourceNotFoundException("Account not found with id: " + id)
                );
        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new BusinessException("Account is already closed");
        }

        if (account.getBalance().compareTo(BigDecimal.ZERO) > 0){
            throw new BusinessException("Account cannot be closed while balance is greater than zero");
        }

        account.setStatus(AccountStatus.CLOSED);
        return accountMapper.toResponse(accountRepository.save(account));
    }
}
