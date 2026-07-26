package com.fincore.repository;

import com.fincore.entity.Beneficiary;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BeneficiaryRepository extends JpaRepository<Beneficiary, UUID> {
    List<Beneficiary> findByCustomerId(UUID customerId);
    boolean existsByCustomerIdAndAccountNumber(UUID customerId, String accountNumber);

}
