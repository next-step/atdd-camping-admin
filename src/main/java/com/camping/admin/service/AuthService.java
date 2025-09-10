package com.camping.admin.service;

import com.camping.admin.dto.LoginRequest;
import com.camping.admin.dto.LoginResponse;
import com.camping.admin.exception.AuthenticationException;
import com.camping.admin.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;

    @Value("${admin.username}")
    private String adminUsername;
  
    @Value("${admin.password}")
    private String adminPassword;

    public LoginResponse authenticate(LoginRequest request) {
        if (isValidCredentials(request.getUsername(), request.getPassword())) {
            String token = jwtService.generateToken(request.getUsername());
            return new LoginResponse(token);
        }
        throw new AuthenticationException("Invalid credentials");
    }


    private boolean isValidCredentials(String username, String password) {
        return adminUsername.equals(username) && adminPassword.equals(password);
    }
}
