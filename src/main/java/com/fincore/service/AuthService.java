package com.fincore.service;

import com.fincore.dto.AuthResponse;
import com.fincore.dto.LoginRequest;
import com.fincore.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
