package com.fincore.service;

import com.fincore.dto.AddBeneficiaryRequest;
import com.fincore.dto.BeneficiaryResponse;
import com.fincore.dto.UpdateBeneficiaryRequest;


import java.util.List;
import java.util.UUID;

public interface BeneficiaryService {
    BeneficiaryResponse addBeneficiary(AddBeneficiaryRequest request);
    BeneficiaryResponse updateBeneficiary(UUID id, UpdateBeneficiaryRequest request);
    void deleteBeneficiary(UUID id);
    List<BeneficiaryResponse> getBeneficiariesByCustomerId(UUID id);
}
