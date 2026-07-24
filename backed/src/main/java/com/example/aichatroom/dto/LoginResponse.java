package com.example.aichatroom.dto;

public class LoginResponse {
    private Long id;
    private String username;
    private String avatar;
    private Integer role;
    private String token;

    public LoginResponse() {}
    public LoginResponse(Long id, String username, String avatar, Integer role, String token) {
        this.id = id;
        this.username = username;
        this.avatar = avatar;
        this.role = role;
        this.token = token;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public Integer getRole() { return role; }
    public void setRole(Integer role) { this.role = role; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}