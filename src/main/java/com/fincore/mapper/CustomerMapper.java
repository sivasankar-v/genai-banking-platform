package com.fincore.mapper;

import com.fincore.dto.CreateCustomerRequest;
import com.fincore.dto.CustomerResponse;
import com.fincore.dto.UpdateCustomerRequest;
import com.fincore.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {
    public Customer toEntity(CreateCustomerRequest request){
        return Customer.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .mobile(request.getMobile())
                .aadhaarNumber(request.getAadhaarNumber())
                .panNumber(request.getPanNumber())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .postalCode(request.getPostalCode())
                .build();
    }

    public CustomerResponse toResponse(Customer customer){
        return CustomerResponse.builder()
                .id(customer.getId())
                .customerNumber(customer.getCustomerNumber())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .dateOfBirth(customer.getDateOfBirth())
                .gender(customer.getGender())
                .mobile(customer.getMobile())
                .city(customer.getCity())
                .state(customer.getState())
                .country(customer.getCountry())
                .kycStatus(customer.getKycStatus())
                .status(customer.getStatus())
                .build();
    }

    public void updateCustomer(Customer customer, UpdateCustomerRequest request){
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setGender(request.getGender());
        customer.setMobile(request.getMobile());
        customer.setAddressLine1(request.getAddressLine1());
        customer.setAddressLine2(request.getGetAddressLine2());
        customer.setCity(request.getCity());
        customer.setCountry(request.getCountry());
        customer.setState(request.getState());
        customer.setPostalCode(request.getPostalCode());
    }
}
