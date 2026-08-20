package com.arun.portfolio;

import com.arun.portfolio.dto.AuthRequest;
import com.arun.portfolio.dto.ProjectRequest;
import com.arun.portfolio.security.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdminSecurityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Test
    void testAdminLogin_ValidCredentials_ShouldReturnJwtToken() throws Exception {
        AuthRequest request = new AuthRequest("admin", "adminpassword123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.role").value("ROLE_ADMIN"));
    }

    @Test
    void testAdminLogin_InvalidPassword_ShouldReturnUnauthorized() throws Exception {
        AuthRequest request = new AuthRequest("admin", "wrong_password");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreateProject_WithoutAuth_ShouldReturnUnauthorized() throws Exception {
        ProjectRequest request = new ProjectRequest(
                "Secured Project", "Test Description", "Java, Spring Boot", "", "", "", true
        );

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreateProject_WithValidJwtToken_ShouldReturnCreated() throws Exception {
        String token = jwtUtils.generateToken("admin", "ADMIN");

        ProjectRequest request = new ProjectRequest(
                "Admin Created Project", "Created via Spring Security", "Java, Spring Boot, JWT", "", "", "", true
        );

        mockMvc.perform(post("/api/projects")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Admin Created Project"));
    }
}
