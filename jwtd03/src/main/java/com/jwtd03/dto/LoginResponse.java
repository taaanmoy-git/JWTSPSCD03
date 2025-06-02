package com.jwtd03.dto;

public class LoginResponse {
	
    private String username;
    private String role;
    private String jwtToken;


    public LoginResponse(String username, String roles, String jwtToken) {
        this.username = username;
        this.role = roles;
        this.jwtToken = jwtToken;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}