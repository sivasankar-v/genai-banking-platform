package com.fincore.service;

import com.fincore.dto.AccountResponse;
import com.fincore.dto.OpenAccountRequest;

import java.util.List;
import java.util.UUID;

public interface AccountService {
    AccountResponse openAccount(OpenAccountRequest  request);
    AccountResponse getAccountById(UUID id);
    AccountResponse getAccountByNumber(String accountNumber);
    List<AccountResponse> getAccountsByCustomerId(UUID customerId);
    AccountResponse freezeAccount(UUID id);
    AccountResponse closeAccount(UUID id);
}
