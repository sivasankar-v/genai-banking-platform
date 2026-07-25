package com.fincore.repository;

import com.fincore.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findBySourceAccountIdOrDestinationAccountIdOrderByCreatedAtDesc(UUID sourceAccountId, UUID destinationAccountId);
    boolean existsByTransactionId(String transactionId);

}
