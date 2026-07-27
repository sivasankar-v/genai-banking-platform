package com.fincore.dto;

import com.fincore.enums.LoanStatus;
import com.fincore.enums.LoanType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanResponse {
    private UUID id;
    private String loanNumber;
    private UUID customerId;
    private LoanType loanType;
    private BigDecimal principal;
    private Double interest;
    private Integer tenureMonths;
    private BigDecimal emi;
    private LoanStatus loanStatus;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
}
