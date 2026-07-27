package com.fincore.service;

import com.fincore.dto.ApplyLoanRequest;
import com.fincore.dto.LoanResponse;

import java.util.List;
import java.util.UUID;

public interface LoanService {
    LoanResponse applyLoan(ApplyLoanRequest request);
    LoanResponse getLoanById(UUID loanId);
    List<LoanResponse> getLoansByCustomer(UUID customerId);
    LoanResponse approveLoan(UUID loanId);
    LoanResponse rejectLoan(UUID loanId);
}
