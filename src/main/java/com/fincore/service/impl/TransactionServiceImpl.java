package com.fincore.service.impl;

import com.fincore.dto.DepositRequest;
import com.fincore.dto.TransactionResponse;
import com.fincore.dto.TransferRequest;
import com.fincore.dto.WithdrawRequest;
import com.fincore.entity.Account;
import com.fincore.entity.Transaction;
import com.fincore.enums.AccountStatus;
import com.fincore.enums.TransactionStatus;
import com.fincore.enums.TransactionType;
import com.fincore.exception.BusinessException;
import com.fincore.exception.ResourceNotFoundException;
import com.fincore.mapper.TransactionMapper;
import com.fincore.repository.AccountRepository;
import com.fincore.repository.TransactionRepository;
import com.fincore.security.access.AccessGuard;
import com.fincore.service.TransactionService;
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
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionMapper transactionMapper;
    private final AccessGuard accessGuard;

    private String generateTransactionId() {
        String transactionId;
        do {
            transactionId = ReferenceGenerator.next("TXN");
        } while (transactionRepository.existsByTransactionId(transactionId));
        return transactionId;
    }

    private Account lockAccount(String accountNumber, String label) {
        return accountRepository.findByAccountNumberForUpdate(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException(label + " account not found"));
    }

    private static void requireActive(Account account, String label) {
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new BusinessException(label + " account is not active");
        }
    }

    private static void requireFunds(Account account, BigDecimal amount) {
        if (account.getBalance().compareTo(amount) < 0) {
            throw new BusinessException("Insufficient funds");
        }
    }

    @Override
    public TransactionResponse deposit(DepositRequest request) {
        Account account = lockAccount(request.getAccountNumber(), "Destination");
        accessGuard.checkAccount(account);
        requireActive(account, "Destination");

        account.setBalance(account.getBalance().add(request.getAmount()));
        accountRepository.save(account);

        return record(TransactionType.DEPOSIT, request.getAmount(), request.getDescription(), null, account);
    }

    @Override
    public TransactionResponse withdraw(WithdrawRequest request) {
        Account account = lockAccount(request.getAccountNumber(), "Source");
        accessGuard.checkAccount(account);
        requireActive(account, "Source");
        requireFunds(account, request.getAmount());

        account.setBalance(account.getBalance().subtract(request.getAmount()));
        accountRepository.save(account);

        return record(TransactionType.WITHDRAW, request.getAmount(), request.getDescription(), account, null);
    }

    @Override
    public TransactionResponse transfer(TransferRequest request) {
        if (request.getFromAccount().equals(request.getToAccount())) {
            throw new BusinessException("Source and destination accounts cannot be the same");
        }

        // Always lock the two rows in the same order, so two opposite transfers
        // between the same accounts cannot deadlock each other.
        boolean sourceFirst = request.getFromAccount().compareTo(request.getToAccount()) < 0;
        Account first = lockAccount(sourceFirst ? request.getFromAccount() : request.getToAccount(),
                sourceFirst ? "Source" : "Destination");
        Account second = lockAccount(sourceFirst ? request.getToAccount() : request.getFromAccount(),
                sourceFirst ? "Destination" : "Source");
        Account source = sourceFirst ? first : second;
        Account destination = sourceFirst ? second : first;

        accessGuard.checkAccount(source);
        requireActive(source, "Source");
        requireActive(destination, "Destination");
        if (!source.getCurrency().equalsIgnoreCase(destination.getCurrency())) {
            throw new BusinessException("Transfers between different currencies are not supported");
        }
        requireFunds(source, request.getAmount());

        source.setBalance(source.getBalance().subtract(request.getAmount()));
        destination.setBalance(destination.getBalance().add(request.getAmount()));
        accountRepository.save(source);
        accountRepository.save(destination);

        return record(TransactionType.TRANSFER, request.getAmount(), request.getDescription(), source, destination);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByAccount(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));
        accessGuard.checkAccount(account);
        List<Transaction> transactions = transactionRepository
                .findBySourceAccountIdOrDestinationAccountIdOrderByCreatedAtDesc(accountId, accountId);
        return transactions.stream().map(transactionMapper::toResponse).toList();
    }

    private TransactionResponse record(TransactionType type, BigDecimal amount, String description,
                                       Account source, Account destination) {
        Transaction transaction = Transaction.builder()
                .transactionId(generateTransactionId())
                .transactionType(type)
                .transactionStatus(TransactionStatus.SUCCESS)
                .amount(amount)
                .description(description)
                .sourceAccount(source)
                .destinationAccount(destination)
                .build();
        return transactionMapper.toResponse(transactionRepository.save(transaction));
    }
}
