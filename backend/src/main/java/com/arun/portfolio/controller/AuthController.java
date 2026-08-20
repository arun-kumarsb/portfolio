package com.arun.portfolio.controller;

import com.arun.portfolio.dto.AuthRequest;
import com.arun.portfolio.dto.AuthResponse;
import com.arun.portfolio.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller exposing Admin Authentication endpoints.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * POST /api/auth/login - Authenticate admin and return signed JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = authService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/auth/me - Verify current authenticated admin identity
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("authenticated", true);
        response.put("username", authentication != null ? authentication.getName() : authService.getAdminUsername());
        response.put("role", "ROLE_ADMIN");
        return ResponseEntity.ok(response);
    }
}
