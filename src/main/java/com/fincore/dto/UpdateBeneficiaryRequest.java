package com.fincore.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBeneficiaryRequest {
    @NotBlank(message = "Beneficiary name is request")
    private String beneficiaryName;
    private String ifscCode;
    private String bankName;
    private String nickName;
}
