package com.fincore.controller;

import com.fincore.dto.AccountResponse;
import com.fincore.dto.OpenAccountRequest;
import com.fincore.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponse> openAccount(@Valid @RequestBody OpenAccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.openAccount(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable UUID id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @GetMapping("/account-number/{accountNumber}")
    public ResponseEntity<AccountResponse> getAccountByAccountNumber(@PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.getAccountByNumber(accountNumber));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<AccountResponse>> getAccountsByCustomerId(@PathVariable UUID customerId) {
        return ResponseEntity.ok(
                accountService.getAccountsByCustomerId(customerId)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/freeze")
    public ResponseEntity<AccountResponse> freezeAccount(@PathVariable UUID id) {
        return ResponseEntity.ok(accountService.freezeAccount(id));
    }

    @PatchMapping("/{id}/close")
    public ResponseEntity<AccountResponse> closeAccount(@PathVariable UUID id) {
        return ResponseEntity.ok(accountService.closeAccount(id));
    }
}

