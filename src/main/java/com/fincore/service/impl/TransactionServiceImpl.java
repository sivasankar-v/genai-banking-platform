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
import com.fincore.service.TransactionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionMapper transactionMapper;

    private String generateTransactionId(){
        String transactionId;
        do{
            transactionId = "TXN" + System.currentTimeMillis();
        }while(transactionRepository.existsByTransactionId(transactionId));
        return transactionId;
    }
    @Override
    public TransactionResponse deposit(DepositRequest request) {
        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account number not found"));

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new BusinessException("Account is not active");
        }

        account.setBalance(account.getBalance().add(request.getAmount()));

        Transaction transaction = Transaction.builder()
                .transactionId(generateTransactionId())
                .transactionType(TransactionType.DEPOSIT)
                .transactionStatus(TransactionStatus.SUCCESS)
                .amount(request.getAmount())
                .description(request.getDescription())
                .destinationAccount(account)
                .build();
        accountRepository.save(account);
        return transactionMapper.toResponse(transactionRepository.save(transaction));
    }

    @Override
    public TransactionResponse withdraw(WithdrawRequest request) {
        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account number not found"));
        if(account.getStatus() != AccountStatus.ACTIVE)
            throw new BusinessException("Account is not active");
        if(account.getBalance().compareTo(request.getAmount()) < 0)
            throw new BusinessException("Insufficient funds");
        account.setBalance(account.getBalance().subtract(request.getAmount()));
        Transaction transaction = Transaction.builder()
                .transactionId(generateTransactionId())
                .transactionType(TransactionType.WITHDRAW)
                .transactionStatus(TransactionStatus.SUCCESS)
                .amount(request.getAmount())
                .description(request.getDescription())
                .sourceAccount(account)
                .build();
        accountRepository.save(account);
        return transactionMapper.toResponse(transactionRepository.save(transaction));
    }

    @Override
    public TransactionResponse transfer(TransferRequest request) {
        if(request.getFromAccount().equals(request.getToAccount())){
            throw new BusinessException("Source and destination accounts cannot be the same");
        }

        Account sourceAccount = accountRepository.findByAccountNumber(request.getFromAccount())
                .orElseThrow(() ->  new ResourceNotFoundException("Source account not found"));

        Account destinationAccount = accountRepository.findByAccountNumber(request.getToAccount())
                .orElseThrow(() -> new ResourceNotFoundException("Destination account not found"));

        if(sourceAccount.getBalance().compareTo(request.getAmount()) < 0)
            throw new BusinessException("Insufficient balance");

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(request.getAmount()));
        destinationAccount.setBalance(destinationAccount.getBalance().add(request.getAmount()));

        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);
        Transaction transaction = Transaction.builder()
                .transactionId(generateTransactionId())
                .transactionStatus(TransactionStatus.SUCCESS)
                .transactionType(TransactionType.TRANSFER)
                .amount(request.getAmount())
                .description(request.getDescription())
                .sourceAccount(sourceAccount)
                .destinationAccount(destinationAccount)
                .build();
        return transactionMapper.toResponse(transactionRepository.save(transaction));
    }

    @Override
    public List<TransactionResponse> getTransactionsByAccount(UUID accountId) {
        List<Transaction> transactions = transactionRepository.findBySourceAccountIdOrDestinationAccountIdOrderByCreatedAtDesc(accountId, accountId);
        return transactions.stream().map(transactionMapper::toResponse).toList();
    }
}
