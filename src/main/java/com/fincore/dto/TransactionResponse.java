package com.fincore.dto;

import com.fincore.enums.TransactionStatus;
import com.fincore.enums.TransactionType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionResponse {
    private UUID id;

    private String transactionId;

    private TransactionType transactionType;

    private TransactionStatus transactionStatus;
    private BigDecimal amount;
    private String description;
    private String sourceAccount;
    private String destinationAccount;
    private LocalDateTime createdAt;
}
