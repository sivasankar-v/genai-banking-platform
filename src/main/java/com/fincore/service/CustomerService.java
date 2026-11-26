package com.fincore.service;

import com.fincore.dto.CreateCustomerRequest;
import com.fincore.dto.CustomerResponse;
import com.fincore.dto.UpdateCustomerRequest;
import com.fincore.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CustomerService {
    CustomerResponse createCustomer(CreateCustomerRequest request);
    CustomerResponse getCustomerById(UUID id);
    CustomerResponse getMyCustomer();
    Page<CustomerResponse> getAllCustomers(Pageable pageable);
    CustomerResponse updateCustomer(UUID id, UpdateCustomerRequest request);
    void deleteCustomer(UUID id);
    Page<CustomerResponse> searchCustomers(String keyword, Pageable pageable);

}
