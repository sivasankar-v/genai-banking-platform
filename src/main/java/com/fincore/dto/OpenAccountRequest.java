package com.fincore.dto;

import com.fincore.enums.AccountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpenAccountRequest {
    @NotNull(message = "Customer ID is required")
    private UUID customerId;

    @NotNull(message = "Account type is required")
    private AccountType accountType;

    @NotBlank(message = "Currency is required")
    private String currency;

    @NotNull(message = "Initial deposit is required")
    @DecimalMin(value = "0.00", message = "Initial deposit cannot be negative")
    private BigDecimal initialDeposit;
}
