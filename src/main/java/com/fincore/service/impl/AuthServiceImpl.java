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
import com.fincore.security.jwt.JwtService;
import com.fincore.security.service.CustomUserDetailsService;
import com.fincore.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

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
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
                )
        );
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtService.generateToken(userDetails);
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new ResourceNotFoundException("User not found"));
        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .message(AppConstants.LOGIN_SUCCESSFUL)
                .email(user.getEmail())
                .role(user.getRole().getRoleName().name())
                .build();
    }
}
