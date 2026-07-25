package com.fincore.controller;

import com.fincore.dto.DepositRequest;
import com.fincore.dto.TransactionResponse;
import com.fincore.dto.TransferRequest;
import com.fincore.dto.WithdrawRequest;
import com.fincore.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(@Valid @RequestBody DepositRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.deposit(request));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(@Valid @RequestBody WithdrawRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.withdraw(request));
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(@Valid @RequestBody TransferRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.transfer(request));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByAccount(@PathVariable("accountId") UUID accountId){
        return ResponseEntity.ok(transactionService.getTransactionsByAccount(accountId));
    }
}
