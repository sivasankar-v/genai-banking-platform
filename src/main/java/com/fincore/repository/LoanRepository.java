package com.fincore.repository;

import com.fincore.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoanRepository extends JpaRepository<Loan, UUID> {
    Optional<Loan> findByLoanNumber(String loanNumber);
    List<Loan> findByCustomerId(UUID customerId);
    boolean existsByLoanNumber(String loanNumber);
}
