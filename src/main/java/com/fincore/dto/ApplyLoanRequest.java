package com.fincore.dto;

import com.fincore.enums.LoanType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
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
    @Min(1)
    @Max(360)
    private Integer tenureMonths;
}
