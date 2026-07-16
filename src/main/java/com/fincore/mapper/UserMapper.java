package com.fincore.mapper;

import com.fincore.dto.RegisterRequest;
import com.fincore.entity.Role;
import com.fincore.entity.User;
import com.fincore.enums.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final PasswordEncoder passwordEncoder;
    public User toUser(RegisterRequest request, Role role){
        return User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail().toLowerCase().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .mobileNumber(request.getMobileNumber())
                .status(UserStatus.ACTIVE)
                .role(role)
                .build();
    }
}
