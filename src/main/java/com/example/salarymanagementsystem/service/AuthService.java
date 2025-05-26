package com.example.salarymanagementsystem.service;

import com.example.salarymanagementsystem.dto.LoginRequest;
import com.example.salarymanagementsystem.dto.JwtAuthResponse;

public interface AuthService {
    JwtAuthResponse login(LoginRequest loginRequest);
}
