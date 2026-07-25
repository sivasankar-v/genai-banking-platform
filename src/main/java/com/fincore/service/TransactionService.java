package com.fincore.service;

import com.fincore.dto.DepositRequest;
import com.fincore.dto.TransactionResponse;
import com.fincore.dto.TransferRequest;
import com.fincore.dto.WithdrawRequest;


import java.util.List;
import java.util.UUID;

public interface TransactionService {
    TransactionResponse deposit(DepositRequest request);
    TransactionResponse withdraw(WithdrawRequest request);
    TransactionResponse transfer(TransferRequest request);
    List<TransactionResponse> getTransactionsByAccount(UUID accountId);
}
