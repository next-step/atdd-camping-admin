package com.camping.admin.controller;

import com.camping.admin.dto.LoginRequest;
import com.camping.admin.dto.LoginResponse;
import com.camping.admin.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Validated @RequestBody LoginRequest request, HttpServletResponse response) {
        LoginResponse loginResponse = authService.authenticate(request);
        Cookie cookie = createAuthCookie(loginResponse.accessToken());
        response.addCookie(cookie);
        return ResponseEntity.ok(loginResponse);
    }

    private Cookie createAuthCookie(String token) {
        Cookie cookie = new Cookie("AUTH_TOKEN", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        return cookie;
    }
}
