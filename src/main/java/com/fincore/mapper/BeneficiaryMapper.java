package com.fincore.mapper;

import com.fincore.dto.BeneficiaryResponse;
import com.fincore.entity.Beneficiary;
import org.springframework.stereotype.Component;

@Component
public class BeneficiaryMapper {
    public BeneficiaryResponse toResponse(Beneficiary beneficiary) {
        return BeneficiaryResponse.builder()
                .id(beneficiary.getId())
                .beneficiaryName(beneficiary.getBeneficiaryName())
                .customerId(beneficiary.getCustomer().getId())
                .accountNumber(beneficiary.getAccountNumber())
                .ifscCode(beneficiary.getIfscCode())
                .bankName(beneficiary.getBankName())
                .nickname(beneficiary.getNickName())
                .createdAt(beneficiary.getCreatedAt())
                .build();
    }
}
