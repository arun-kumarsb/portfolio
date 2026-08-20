package com.arun.portfolio.service;

import com.arun.portfolio.dto.AuthRequest;
import com.arun.portfolio.dto.AuthResponse;
import com.arun.portfolio.security.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service managing Admin authentication and JWT token generation.
 */
@Service
public class AuthService {

    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username:admin}")
    private String adminUsername;

    @Value("${app.admin.password:adminpassword123}")
    private String adminPassword;

    public AuthService(JwtUtils jwtUtils, PasswordEncoder passwordEncoder) {
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Authenticate admin credentials and return JWT token.
     */
    public AuthResponse authenticate(AuthRequest request) {
        String inputUsername = request.getUsername().trim();
        String inputPassword = request.getPassword().trim();

        // Validate username and password
        boolean usernameMatches = adminUsername.equalsIgnoreCase(inputUsername);
        boolean passwordMatches = adminPassword.equals(inputPassword) || passwordEncoder.matches(inputPassword, adminPassword);

        if (!usernameMatches || !passwordMatches) {
            throw new BadCredentialsException("Invalid admin username or password.");
        }

        String token = jwtUtils.generateToken(inputUsername, "ADMIN");
        return new AuthResponse(token, inputUsername, "ROLE_ADMIN", jwtUtils.getJwtExpirationMs());
    }

    public String getAdminUsername() {
        return adminUsername;
    }
}
