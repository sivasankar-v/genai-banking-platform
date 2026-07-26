package com.fincore.controller;

import com.fincore.dto.AddBeneficiaryRequest;
import com.fincore.dto.BeneficiaryResponse;
import com.fincore.dto.UpdateBeneficiaryRequest;
import com.fincore.service.BeneficiaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/beneficiaries")
@RequiredArgsConstructor
public class BeneficiaryController {
    private final BeneficiaryService beneficiaryService;

    @PostMapping
    public ResponseEntity<BeneficiaryResponse> addBeneficiary(@Valid @RequestBody AddBeneficiaryRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(beneficiaryService.addBeneficiary(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BeneficiaryResponse> updateBeneficiary(@PathVariable UUID id, @Valid @RequestBody UpdateBeneficiaryRequest request){
        return ResponseEntity.ok(beneficiaryService.updateBeneficiary(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBeneficiary(@PathVariable UUID id){
        beneficiaryService.deleteBeneficiary(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<BeneficiaryResponse>> getBeneficiaries(@PathVariable UUID customerId){
        return ResponseEntity.ok(beneficiaryService.getBeneficiariesByCustomerId(customerId));
    }
}
