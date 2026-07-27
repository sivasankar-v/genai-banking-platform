package com.fincore.mapper;

import com.fincore.dto.LoanResponse;
import com.fincore.entity.Loan;
import org.springframework.stereotype.Component;

@Component
public class LoanMapper {
    public LoanResponse toResponse(Loan loan) {

        return LoanResponse.builder()
                .id(loan.getId())
                .loanNumber(loan.getLoanNumber())
                .customerId(loan.getCustomer().getId())
                .loanType(loan.getLoanType())
                .principal(loan.getPrincipalAmount())
                .interest(loan.getInterestRate())
                .tenureMonths(loan.getTenureMonths())
                .emi(loan.getEmi())
                .loanStatus(loan.getStatus())
                .approvedAt(loan.getApprovedDate())
                .createdAt(loan.getCreatedAt())
                .build();
    }
}
