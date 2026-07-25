package com.fincore.mapper;

import com.fincore.dto.TransactionResponse;
import com.fincore.entity.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {
    public TransactionResponse toResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .transactionId(transaction.getTransactionId())
                .transactionType(transaction.getTransactionType())
                .transactionStatus(transaction.getTransactionStatus())
                .description(transaction.getDescription())
                .amount(transaction.getAmount())
                .destinationAccount(transaction.getDestinationAccount() != null ? transaction.getDestinationAccount().getAccountNumber() : null)
                .sourceAccount(transaction.getSourceAccount() != null ? transaction.getSourceAccount().getAccountNumber() : null)
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
