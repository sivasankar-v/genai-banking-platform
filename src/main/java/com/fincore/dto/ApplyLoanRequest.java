package com.fincore.dto;

import com.fincore.enums.LoanType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplyLoanRequest {
    @NotNull
    private UUID customerId;
    @NotNull
    private LoanType loanType;
    @NotNull
    @DecimalMin("1000.00")
    private BigDecimal principal;
    @NotNull
    @DecimalMin("1.0")
    private BigDecimal interest;
    @NotNull
    @Min(1)
    private Integer tenureMonths;
}
