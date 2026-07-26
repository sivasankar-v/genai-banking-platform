package com.fincore.service.impl;

import com.fincore.dto.AddBeneficiaryRequest;
import com.fincore.dto.BeneficiaryResponse;
import com.fincore.dto.UpdateBeneficiaryRequest;
import com.fincore.entity.Beneficiary;
import com.fincore.entity.Customer;
import com.fincore.exception.BusinessException;
import com.fincore.exception.ResourceNotFoundException;
import com.fincore.mapper.BeneficiaryMapper;
import com.fincore.repository.BeneficiaryRepository;
import com.fincore.repository.CustomerRepository;
import com.fincore.service.BeneficiaryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class BeneficiaryServiceImpl implements BeneficiaryService {
    private final BeneficiaryRepository beneficiaryRepository;
    private final CustomerRepository customerRepository;
    private final BeneficiaryMapper beneficiaryMapper;

    @Override
    public BeneficiaryResponse addBeneficiary(AddBeneficiaryRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer Not Found"));

        if(beneficiaryRepository.existsByCustomerIdAndAccountNumber(request.getCustomerId(), request.getAccountNumber())) {
            throw new BusinessException("Beneficiary Already Exists");
        }

        Beneficiary beneficiary = Beneficiary.builder()
                .customer(customer)
                .beneficiaryName(request.getBeneficiaryName())
                .accountNumber(request.getAccountNumber())
                .ifscCode(request.getIfscCode())
                .bankName(request.getBankName())
                .nickName(request.getNickName())
                .build();
        return beneficiaryMapper.toResponse(beneficiaryRepository.save(beneficiary));
    }

    @Override
    public BeneficiaryResponse updateBeneficiary(UUID id, UpdateBeneficiaryRequest request) {
        Beneficiary beneficiary = beneficiaryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Beneficiary Not Found"));
        beneficiary.setBeneficiaryName(request.getBeneficiaryName());
        beneficiary.setIfscCode(request.getIfscCode());
        beneficiary.setBankName(request.getBankName());
        beneficiary.setNickName(request.getNickName());
        return beneficiaryMapper.toResponse(beneficiaryRepository.save(beneficiary));
    }

    @Override
    public void deleteBeneficiary(UUID id) {
        Beneficiary beneficiary = beneficiaryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Beneficiary Not Found"));
        beneficiaryRepository.delete(beneficiary);
    }

    @Override
    public List<BeneficiaryResponse> getBeneficiariesByCustomerId(UUID id) {
        return beneficiaryRepository.findByCustomerId(id).stream().map(beneficiaryMapper::toResponse).toList();
    }
}
