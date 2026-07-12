package com.fincore.mapper;

import com.fincore.dto.RegisterRequest;
import com.fincore.entity.Role;
import com.fincore.entity.User;
import com.fincore.enums.UserStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toUser(RegisterRequest request, Role role, PasswordEncoder passwordEncoder){
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
