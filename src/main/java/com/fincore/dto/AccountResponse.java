package com.fincore.dto;

import com.fincore.enums.AccountStatus;
import com.fincore.enums.AccountType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountResponse {
    private UUID id;
    private String accountNumber;
    private AccountType accountType;
    private AccountStatus status;
    private BigDecimal balance;
    private String currency;
    private UUID customerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
