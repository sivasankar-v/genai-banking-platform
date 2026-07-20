package com.fincore.controller;

import com.fincore.dto.CreateCustomerRequest;
import com.fincore.dto.CustomerResponse;
import com.fincore.dto.UpdateCustomerRequest;
import com.fincore.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse createCustomer(@Valid @RequestBody CreateCustomerRequest request){
        return customerService.createCustomer(request);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable UUID id){
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @GetMapping
    public ResponseEntity<Page<CustomerResponse>> getAllCustomers(
            @PageableDefault(
               page = 0,
               size = 10,
               sort = "createdAt",
               direction = Sort.Direction.DESC
       )Pageable pageable
            ){
        return ResponseEntity.ok(customerService.getAllCustomers(pageable));

    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable UUID id, @Valid @RequestBody UpdateCustomerRequest request){
        return ResponseEntity.ok(
                customerService.updateCustomer(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID id){
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<CustomerResponse>> searchCustomers(@RequestParam String keyword,
                                                                  @PageableDefault(
                                                                          size = 10,
                                                                          sort = "createdAt",
                                                                          direction = Sort.Direction.DESC
                                                                  ) Pageable pageable){
        return ResponseEntity.ok(
                customerService.searchCustomers(keyword, pageable)
        );
    }
}
