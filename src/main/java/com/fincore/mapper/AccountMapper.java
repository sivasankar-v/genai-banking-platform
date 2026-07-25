package com.fincore.mapper;

import com.fincore.dto.AccountResponse;
import com.fincore.entity.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {
    public AccountResponse toResponse(Account account){
        if(account == null){
            return null;
        }

        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType())
                .status(account.getStatus())
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .customerId(account.getCustomer().getId())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }
}
