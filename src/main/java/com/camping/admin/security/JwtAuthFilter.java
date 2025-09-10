package com.camping.admin.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        if (isExcluded(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = extractTokenFromRequest(request);

        if (token == null) {
            handleUnauthorizedAccess(request, response, "Missing or invalid token");
            return;
        }
        if (!jwtService.isTokenValid(token)) {
            handleUnauthorizedAccess(request, response, "Invalid or expired token");
            return;
        }

        processAuthenticatedRequest(request, token);
        filterChain.doFilter(request, response);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String token = extractTokenFromHeader(request);
        if (token == null) {
            token = extractTokenFromCookies(request);
        }
        return token;
    }

    private String extractTokenFromHeader(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private String extractTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("AUTH_TOKEN")) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private void handleUnauthorizedAccess(HttpServletRequest request, HttpServletResponse response, String message) throws IOException {
        if (wantsHtml(request)) {
            response.sendRedirect("/login");
        } else {
            unauthorized(response, message);
        }
    }

    private void processAuthenticatedRequest(HttpServletRequest request, String token) {
        request.setAttribute("currentUsername", jwtService.getUsername(token));
    }

    private boolean isExcluded(String path) {
        return pathMatcher.match("/auth/login", path) ||
                pathMatcher.match("/login", path) ||
                pathMatcher.match("/css/**", path) ||
                pathMatcher.match("/js/**", path) ||
                pathMatcher.match("/images/**", path) ||
                pathMatcher.match("/webjars/**", path) ||
                pathMatcher.match("/favicon.ico", path) ||
                pathMatcher.match("/h2-console/**", path) ||
                path.equals("/");
    }

    private boolean wantsHtml(HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        return accept != null && accept.contains("text/html");
    }

    private void unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String body = "{\"error\":\"" + message + "\"}";
        response.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));
    }
}
