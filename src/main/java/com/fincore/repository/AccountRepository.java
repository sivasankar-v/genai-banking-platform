package com.fincore.repository;

import com.fincore.entity.Account;
import com.fincore.enums.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;


import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {

    Optional<Account> findByAccountNumber(String accountNumber);

    // Locks the row until the transaction ends, so concurrent balance updates queue up
    // instead of overwriting each other.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.accountNumber = :accountNumber")
    Optional<Account> findByAccountNumberForUpdate(@Param("accountNumber") String accountNumber);
    List<Account> findByCustomerId(UUID customerId);
    boolean existsByAccountNumber(String accountNumber);
    List<Account> findByStatus(AccountStatus status);
}
