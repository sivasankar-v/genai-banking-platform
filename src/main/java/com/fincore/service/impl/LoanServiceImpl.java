package com.fincore.service.impl;

import com.fincore.dto.ApplyLoanRequest;
import com.fincore.dto.LoanResponse;
import com.fincore.entity.Customer;
import com.fincore.entity.Loan;
import com.fincore.enums.LoanStatus;
import com.fincore.exception.ResourceNotFoundException;
import com.fincore.mapper.LoanMapper;
import com.fincore.repository.CustomerRepository;
import com.fincore.repository.LoanRepository;
import com.fincore.service.LoanService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final CustomerRepository customerRepository;
    private final LoanMapper loanMapper;
    @Override
    public LoanResponse applyLoan(ApplyLoanRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId()).orElseThrow(
                () -> new ResourceNotFoundException("Customer Not Found")
        );

        Loan loan = Loan.builder()
                .loanNumber(generateLoanNumber())
                .customer(customer)
                .loanType(request.getLoanType())
                .principalAmount(request.getPrincipal())
                .interestRate(request.getInterest())
                .tenureMonths(request.getTenureMonths())
                .emi(calculateEMI(request.getPrincipal(),request.getInterest(), request.getTenureMonths()))
                .status(LoanStatus.APPLIED)
                .build();
        return loanMapper.toResponse(loanRepository.save(loan));
    }

    @Override
    public LoanResponse getLoanById(UUID loanId) {
        return null;
    }

    @Override
    public List<LoanResponse> getLoansByCustomer(UUID customerId) {
        return List.of();
    }

    @Override
    public LoanResponse approveLoan(UUID loanId) {
        return null;
    }

    @Override
    public LoanResponse rejectLoan(UUID loanId) {
        return null;
    }
}
