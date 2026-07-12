package com.fincore.service.impl;

import com.fincore.constants.AppConstants;
import com.fincore.dto.AuthResponse;
import com.fincore.dto.LoginRequest;
import com.fincore.dto.RegisterRequest;
import com.fincore.entity.Role;
import com.fincore.entity.User;
import com.fincore.enums.RoleType;
import com.fincore.exception.ResourceAlreadyExistsException;
import com.fincore.exception.ResourceNotFoundException;
import com.fincore.mapper.UserMapper;
import com.fincore.repository.RoleRepository;
import com.fincore.repository.UserRepository;
import com.fincore.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if(userRepository.existsByEmail(request.getEmail())){
            throw new ResourceAlreadyExistsException(
                    AppConstants.EMAIL_ALREADY_EXISTS);
        }
        if(userRepository.existsByMobileNumber(request.getMobileNumber())){
            throw new ResourceAlreadyExistsException(
                    AppConstants.MOBILE_ALREADY_EXISTS
            );
        }
        Role customerRole = roleRepository.findByRoleName(RoleType.ROLE_CUSTOMER).orElseThrow(
                () -> new ResourceNotFoundException(
                        AppConstants.ROLE_NOT_FOUND
                ));
        User user = userMapper.toUser(request, customerRole);
        userRepository.save(user);
        return AuthResponse.builder().message(AppConstants.USER_REGISTERED_SUCCESSFULLY)
                .tokenType("Bearer")
                .accessToken(null)
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        throw new UnsupportedOperationException("Login not implemented yet");
    }
}
