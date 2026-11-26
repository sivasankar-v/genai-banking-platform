package com.fincore.controller;

import com.fincore.dto.ApplyLoanRequest;
import com.fincore.dto.LoanResponse;
import com.fincore.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
public class LoanController {
    private final LoanService loanService;

    @PostMapping("/apply")
    public ResponseEntity<LoanResponse> applyLoan(@Valid @RequestBody ApplyLoanRequest applyLoanRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(loanService.applyLoan(applyLoanRequest));
    }


    @GetMapping("/{loanId}")
    public ResponseEntity<LoanResponse> getLoanById(@PathVariable("loanId") UUID loanId) {
        return ResponseEntity.ok(loanService.getLoanById(loanId));
    }

    @GetMapping("/customer/{customerid}")
    public ResponseEntity<List<LoanResponse>> getLoanByCustomerId(@PathVariable("customerid") UUID customerId) {
        return ResponseEntity.ok(loanService.getLoansByCustomer(customerId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{loanId}/approve")
    public ResponseEntity<LoanResponse> approveLoan(@PathVariable("loanId") UUID loanId) {
        return  ResponseEntity.ok(loanService.approveLoan(loanId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{loanid}/reject")
    public ResponseEntity<LoanResponse> rejectLoan(@PathVariable("loanid") UUID loanId) {
        return  ResponseEntity.ok(loanService.rejectLoan(loanId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<LoanResponse>> getAllLoans() {
        return ResponseEntity.ok(loanService.getAllLoans());
    }
}
