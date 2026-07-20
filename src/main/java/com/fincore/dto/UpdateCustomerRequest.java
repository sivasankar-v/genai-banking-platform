package com.fincore.dto;

import com.fincore.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCustomerRequest {
    private String firstName;
    private String lastName;
    @Past
    private LocalDate dateOfBirth;
    private Gender gender;
    @Pattern(regexp = "^[6-9]\\d{9}")
    private String mobile;
    private String addressLine1;
    private String getAddressLine2;
    private String city;
    private String state;
    private String country;
    private String postalCode;
}
