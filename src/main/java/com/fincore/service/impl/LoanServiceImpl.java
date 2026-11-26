package com.fincore.service.impl;

import com.fincore.dto.ApplyLoanRequest;
import com.fincore.dto.LoanResponse;
import com.fincore.entity.Customer;
import com.fincore.entity.Loan;
import com.fincore.enums.LoanStatus;
import com.fincore.enums.LoanType;
import com.fincore.exception.BusinessException;
import com.fincore.exception.ResourceNotFoundException;
import com.fincore.mapper.LoanMapper;
import com.fincore.repository.CustomerRepository;
import com.fincore.repository.LoanRepository;
import com.fincore.security.access.AccessGuard;
import com.fincore.service.LoanService;
import com.fincore.util.ReferenceGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class LoanServiceImpl implements LoanService {

    private static final MathContext MC = MathContext.DECIMAL64;

    private final LoanRepository loanRepository;
    private final CustomerRepository customerRepository;
    private final LoanMapper loanMapper;
    private final AccessGuard accessGuard;

    @Override
    public LoanResponse applyLoan(ApplyLoanRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId()).orElseThrow(
                () -> new ResourceNotFoundException("Customer Not Found")
        );
        accessGuard.checkCustomer(customer);

        // The bank sets the rate by loan type; the EMI must use that same rate.
        double rate = getInterestRate(request.getLoanType());
        Loan loan = Loan.builder()
                .loanNumber(generateLoanNumber())
                .customer(customer)
                .loanType(request.getLoanType())
                .principalAmount(request.getPrincipal())
                .interestRate(rate)
                .tenureMonths(request.getTenureMonths())
                .emi(calculateEmi(request.getPrincipal(), rate, request.getTenureMonths()))
                .status(LoanStatus.APPLIED)
                .build();
        return loanMapper.toResponse(loanRepository.save(loan));
    }

    @Override
    @Transactional(readOnly = true)
    public LoanResponse getLoanById(UUID loanId) {
        Loan loan = findLoan(loanId);
        accessGuard.checkCustomer(loan.getCustomer());
        return loanMapper.toResponse(loan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanResponse> getLoansByCustomer(UUID customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer Not Found"));
        accessGuard.checkCustomer(customer);
        return loanRepository.findByCustomer_Id(customerId)
                .stream()
                .map(loanMapper::toResponse)
                .toList();
    }

    @Override
    public LoanResponse approveLoan(UUID loanId) {
        Loan loan = findPendingLoan(loanId);
        loan.setStatus(LoanStatus.APPROVED);
        loan.setApprovedDate(LocalDateTime.now());
        return loanMapper.toResponse(loanRepository.save(loan));
    }

    @Override
    public LoanResponse rejectLoan(UUID loanId) {
        Loan loan = findPendingLoan(loanId);
        loan.setStatus(LoanStatus.REJECTED);
        return loanMapper.toResponse(loanRepository.save(loan));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanResponse> getAllLoans() {
        return loanRepository.findAll()
                .stream()
                .map(loanMapper::toResponse)
                .toList();
    }

    private Loan findLoan(UUID loanId) {
        return loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));
    }

    private Loan findPendingLoan(UUID loanId) {
        Loan loan = findLoan(loanId);
        if (loan.getStatus() != LoanStatus.APPLIED) {
            throw new BusinessException("Only loans in APPLIED status can be approved or rejected; this loan is " + loan.getStatus());
        }
        return loan;
    }

    private String generateLoanNumber() {
        String loanNumber;
        do {
            loanNumber = ReferenceGenerator.next("LOAN");
        } while (loanRepository.existsByLoanNumber(loanNumber));
        return loanNumber;
    }

    static double getInterestRate(LoanType loanType) {
        return switch (loanType) {
            case HOME -> 8.5;
            case PERSONAL -> 12.0;
            case VEHICLE -> 9.5;
            case EDUCATION -> 7.5;
        };
    }

    /**
     * Standard reducing-balance EMI: P * r * (1 + r)^n / ((1 + r)^n - 1),
     * where r is the monthly rate and n the number of months.
     */
    static BigDecimal calculateEmi(BigDecimal principal, double annualRatePercent, int tenureMonths) {
        if (annualRatePercent == 0) {
            return principal.divide(BigDecimal.valueOf(tenureMonths), 2, RoundingMode.HALF_UP);
        }
        BigDecimal monthlyRate = BigDecimal.valueOf(annualRatePercent).divide(BigDecimal.valueOf(1200), MC);
        BigDecimal growth = BigDecimal.ONE.add(monthlyRate).pow(tenureMonths, MC);
        return principal.multiply(monthlyRate, MC)
                .multiply(growth, MC)
                .divide(growth.subtract(BigDecimal.ONE), MC)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
