package com.movie.dto;

public class AuthResponse {
    private Long userId;
    private String username;
    private boolean admin;
    
    public AuthResponse() {
    }
    
    public AuthResponse(Long userId, String username, boolean admin) {
        this.userId = userId;
        this.username = username;
        this.admin = admin;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public boolean isAdmin() {
        return admin;
    }
    
    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
} 