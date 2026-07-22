package com.example.aichatroom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.aichatroom.dto.LoginRequest;
import com.example.aichatroom.dto.LoginResponse;
import com.example.aichatroom.dto.RegisterRequest;
import com.example.aichatroom.entity.User;
import com.example.aichatroom.mapper.Usermapper;
import com.example.aichatroom.service.LoginService;
import com.example.aichatroom.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private Usermapper userMapper;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));

        if (user == null) {
            throw new RuntimeException("用户名不存在");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        if (user.getStatus() != 1) {
            throw new RuntimeException("账号已被禁用");
        }

        String token = jwtUtils.generateToken(Long.valueOf(user.getId()), user.getUsername());

        return new LoginResponse(
                Long.valueOf(user.getId()),
                user.getUsername(),
                user.getAvatar(),
                user.getRole(),
                token
        );
    }

    @Override
    public LoginResponse register(RegisterRequest request) {
        if (request.getUsername() == null || request.getUsername().isEmpty()) {
            throw new RuntimeException("用户名不能为空");
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new RuntimeException("密码不能为空");
        }

        User existingUser = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));

        if (existingUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setAvatar(request.getAvatar());
        newUser.setRole(0);
        newUser.setStatus(1);
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());

        userMapper.insert(newUser);

        String token = jwtUtils.generateToken(Long.valueOf(newUser.getId()), newUser.getUsername());

        return new LoginResponse(
                Long.valueOf(newUser.getId()),
                newUser.getUsername(),
                newUser.getAvatar(),
                newUser.getRole(),
                token
        );
    }

    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    public User getUserByUsername(String username) {
        return userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));
    }

}