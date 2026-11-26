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
import com.fincore.entity.User;
import com.fincore.repository.UserRepository;
import com.fincore.security.access.AccessGuard;
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
    private final UserRepository userRepository;
    private final AccessGuard accessGuard;

    @Override
    public CustomerResponse createCustomer(CreateCustomerRequest request) {

        if(customerRepository.existsByMobile(request.getMobile())){
            throw new ResourceAlreadyExistsException(AppConstants.MOBILE_ALREADY_EXISTS);
        }
        Customer customer = customerMapper.toEntity(request);
        customer.setCustomerNumber(generateCustomerNumber());

        // A customer creating a profile owns it. Admin-created profiles stay unlinked.
        if (!accessGuard.isAdmin()) {
            User user = userRepository.findByEmail(accessGuard.currentEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            if (customerRepository.existsByUserId(user.getId())) {
                throw new ResourceAlreadyExistsException("You already have a customer profile");
            }
            customer.setUser(user);
        }
        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.toResponse(savedCustomer);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public CustomerResponse getCustomerById(UUID id) {
        Customer customer = customerRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Customer not found with ID: " + id)
        );
        accessGuard.checkCustomer(customer);
        return customerMapper.toResponse(customer);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public CustomerResponse getMyCustomer() {
        Customer customer = customerRepository.findByUserEmail(accessGuard.currentEmail())
                .orElseThrow(() -> new ResourceNotFoundException("No customer profile is linked to your login yet"));
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
        accessGuard.checkCustomer(customer);
        //check duplicate mobile
        if(!customer.getMobile().equals(request.getMobile()) && customerRepository.existsByMobile(request.getMobile())){
            throw new ResourceAlreadyExistsException(AppConstants.MOBILE_ALREADY_EXISTS);
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
        // count()+1 hands out the same number to two simultaneous requests; retry until free.
        long next = customerRepository.count() + 1;
        String number;
        do {
            number = String.format("CUST%06d", next++);
        } while (customerRepository.existsByCustomerNumber(number));
        return number;
    }
}
