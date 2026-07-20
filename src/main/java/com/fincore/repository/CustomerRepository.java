package com.fincore.repository;

import com.fincore.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByCustomerNumber(String customerNumber);
    Optional<Customer> findByMobile(String mobile);
    boolean existsByMobile(String mobile);
    boolean existsByCustomerNumber(String customerNumber);

    @Query("""
SELECT c from Customer c where lower(c.firstName) like lower(concat('%', :keyword, '%') )
or lower(c.lastName) like lower(concat('%', :keyword, '%')) 
or c.mobile like concat('%', :keyword, '%')
or c.customerNumber like concat('%', :keyword, '%') 
""")
    Page<Customer> searchCustomers(@Param("keyword") String keyword, Pageable pageable);
}
