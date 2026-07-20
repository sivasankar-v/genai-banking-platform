package com.fincore.dto;

import com.fincore.enums.CustomerStatus;
import com.fincore.enums.Gender;
import com.fincore.enums.KycStatus;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerResponse {
    private UUID id;
    private String customerNumber;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String mobile;
    private String city;
    private String state;
    private String country;
    private KycStatus kycStatus;
    private CustomerStatus status;
}
