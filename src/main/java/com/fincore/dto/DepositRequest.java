package com.fincore.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepositRequest {
    @NotBlank
    private String accountNumber;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    private String description;
}
