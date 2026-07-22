package com.example.aichatroom.service;

import com.example.aichatroom.common.Result;
import com.example.aichatroom.dto.LoginRequest;
import com.example.aichatroom.dto.LoginResponse;

public interface LoginService {
    LoginResponse login(LoginRequest request);
    
    LoginResponse register(com.example.aichatroom.dto.RegisterRequest request);
}