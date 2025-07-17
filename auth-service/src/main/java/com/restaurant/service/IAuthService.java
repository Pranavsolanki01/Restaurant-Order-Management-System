package com.restaurant.service;

import com.restaurant.dto.JwtResponse;
import com.restaurant.dto.LoginRequest;
import com.restaurant.dto.RegisterRequest;

public interface IAuthService {
    JwtResponse register(RegisterRequest request);
    JwtResponse login(LoginRequest request);
}
