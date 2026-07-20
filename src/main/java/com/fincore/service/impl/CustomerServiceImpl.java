package com.fincore.service.impl;

import com.fincore.constants.AppConstants;
import com.fincore.dto.CreateCustomerRequest;
import com.fincore.dto.CustomerResponse;
import com.fincore.dto.UpdateCustomerRequest;
import com.fincore.entity.Customer;
import com.fincore.enums.CustomerStatus;
import com.fincore.exception.ResourceAlreadyExistsException;
import com.fincore.exception.ResourceNotFoundException;
import com.fincore.mapper.CustomerMapper;
import com.fincore.repository.CustomerRepository;
import com.fincore.service.CustomerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    @Override
    public CustomerResponse createCustomer(CreateCustomerRequest request) {

        if(customerRepository.existsByMobile(request.getMobile())){
            throw new ResourceAlreadyExistsException(AppConstants.MOBILE_ALREADY_EXISTS);
        }
        Customer customer = customerMapper.toEntity(request);
        customer.setCustomerNumber(generateCustomerNumber());
        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.toResponse(savedCustomer);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public CustomerResponse getCustomerById(UUID id) {
        Customer customer = customerRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Customer not found with ID: " + id)
        );
        return customerMapper.toResponse(customer);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public Page<CustomerResponse> getAllCustomers(Pageable pageable) {
        Page<Customer> customerPage = customerRepository.findAll(pageable);
        return customerPage.map(customerMapper::toResponse);
    }

    @Override
    public CustomerResponse updateCustomer(UUID id, UpdateCustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer not found with id: " + id));
        //check duplicate mobile
        if(!customer.getMobile().equals(request.getMobile()) && customerRepository.existsByMobile(request.getMobile())){
            throw new ResourceAlreadyExistsException("Mobil number already exists");
        }
        customerMapper.updateCustomer(customer, request);
        Customer updatedCustomer = customerRepository.save(customer);
        return customerMapper.toResponse(updatedCustomer);
    }

    @Override
    public void deleteCustomer(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Customer not found with id : " + id));

        customer.setStatus(CustomerStatus.INACTIVE);
        customerRepository.save(customer);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public Page<CustomerResponse> searchCustomers(String keyword, Pageable pageable) {
        if(keyword == null || keyword.trim().isEmpty()){
            throw new IllegalArgumentException("Search keyword cannot be empty");
        }
        Page<Customer> customers = customerRepository.searchCustomers(keyword, pageable);
        return customers.map(customerMapper::toResponse);
    }


    private String generateCustomerNumber(){
        long count = customerRepository.count() + 1;
        return String.format("CUST%06d", count);
    }
}
