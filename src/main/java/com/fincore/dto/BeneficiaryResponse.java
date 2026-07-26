package com.fincore.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeneficiaryResponse {
    private UUID id;
    private UUID customerId;
    private String beneficiaryName;
    private String accountNumber;
    private String ifscCode;
    private String bankName;
    private String nickname;
    private LocalDateTime createdAt;
}
