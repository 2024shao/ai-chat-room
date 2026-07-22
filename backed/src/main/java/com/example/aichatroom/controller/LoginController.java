package com.example.aichatroom.controller;

import com.example.aichatroom.common.Result;
import com.example.aichatroom.dto.LoginRequest;
import com.example.aichatroom.dto.LoginResponse;
import com.example.aichatroom.dto.RegisterRequest;
import com.example.aichatroom.service.LoginService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    @Resource
    private LoginService loginService;

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = loginService.login(request);
            return Result.success("登录成功", response);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    @PostMapping("/register")
    public Result<LoginResponse> register(@RequestBody RegisterRequest request) {
        try {
            LoginResponse response = loginService.register(request);
            return Result.success("注册成功", response);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    @GetMapping("/test")
    public Result<String> test() {
        return Result.success("测试接口");
    }
}