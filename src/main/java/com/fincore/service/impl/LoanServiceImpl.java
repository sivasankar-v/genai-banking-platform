package com.fincore.service.impl;

import com.fincore.dto.ApplyLoanRequest;
import com.fincore.dto.LoanResponse;
import com.fincore.entity.Customer;
import com.fincore.entity.Loan;
import com.fincore.enums.LoanStatus;
import com.fincore.enums.LoanType;
import com.fincore.exception.ResourceNotFoundException;
import com.fincore.mapper.LoanMapper;
import com.fincore.repository.CustomerRepository;
import com.fincore.repository.LoanRepository;
import com.fincore.service.LoanService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
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
                .interestRate(getInterestRate(request.getLoanType()))
                .tenureMonths(request.getTenureMonths())
                .emi(calculateEMI(request.getPrincipal(),request.getInterest(), request.getTenureMonths()))
                .status(LoanStatus.APPLIED)
                .build();
        return loanMapper.toResponse(loanRepository.save(loan));
    }

    @Override
    public LoanResponse getLoanById(UUID loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));
        return loanMapper.toResponse(loan);
    }

    @Override
    public List<LoanResponse> getLoansByCustomer(UUID customerId) {

        return loanRepository.findByCustomer_Id(customerId)
                .stream()
                .map(loanMapper::toResponse)
                .toList();
    }

    @Override
    public LoanResponse approveLoan(UUID loanId) {
        Loan loan = loanRepository
                .findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));
        loan.setStatus(LoanStatus.APPROVED);
        loan.setApprovedDate(LocalDateTime.now());
        return loanMapper.toResponse(loanRepository.save(loan));
    }

    @Override
    public LoanResponse rejectLoan(UUID loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));
        loan.setStatus(LoanStatus.REJECTED);
        return loanMapper.toResponse(loanRepository.save(loan));
    }

    private String generateLoanNumber(){
        String loanNumber;
        do{
            loanNumber = "LOAN" + System.currentTimeMillis();
        }while(loanRepository.existsByLoanNumber(loanNumber));
        return loanNumber;
    }

    private Double getInterestRate(LoanType loanType){
        return switch (loanType){
            case HOME -> 8.5;
            case PERSONAL -> 12.0;
            case VEHICLE -> 9.5;
            case EDUCATION -> 7.5;
        };
    }

    private BigDecimal calculateEMI(BigDecimal principal, BigDecimal interest, Integer tenureMonths){
        double monthlyRate = interest.doubleValue()/(12 * 100);
        double emi = (principal.doubleValue() * monthlyRate * Math.pow(1 + monthlyRate, tenureMonths) - 1);
        return BigDecimal.valueOf(emi).setScale(2, RoundingMode.HALF_UP);
    }

    public List<LoanResponse> getAllLoans(){
        return loanRepository.findAll()
                .stream()
                .map(loanMapper::toResponse)
                .toList();
    }
}
