package com.example.aichatroom.interceptor;

import com.example.aichatroom.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JWTUtils jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 从请求头获取 token
        String token = request.getHeader("Authorization");
        
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                // 验证 token
                if (jwtUtil.validateToken(token)) {
                    // 获取用户信息并存入请求上下文
                    Long userId = jwtUtil.extractUserId(token);
                    String username = jwtUtil.extractUsername(token);
                    request.setAttribute("userId", userId);
                    request.setAttribute("username", username);
                    return true;
                }
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
        }
        
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }
}