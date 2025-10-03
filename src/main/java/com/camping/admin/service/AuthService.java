package com.camping.admin.service;

import com.camping.admin.dto.LoginRequest;
import com.camping.admin.dto.LoginResponse;
import com.camping.admin.security.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final JwtService jwtService;
    private final String adminUsername;
    private final String adminPassword;

    public AuthService(
            JwtService jwtService,
            @Value("${admin.username}") String adminUsername,
            @Value("${admin.password}") String adminPassword
    ) {
        this.jwtService = jwtService;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    public LoginResponse login(LoginRequest request, HttpServletResponse response) {
        validateCredentials(request.getUsername(), request.getPassword());
        String token = jwtService.generateToken(request.getUsername());
        addAuthCookie(response, token);
        return new LoginResponse(token);
    }

    private void validateCredentials(String username, String password) {
        if (!adminUsername.equals(username) || !adminPassword.equals(password)) {
            throw new IllegalArgumentException("Invalid credentials");
        }
    }

    private void addAuthCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("AUTH_TOKEN", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
