package com.arun.portfolio.dto;

import com.arun.portfolio.entity.ContactMessage;

import java.time.LocalDateTime;

/**
 * DTO for displaying contact message records in the Admin portal.
 */
public class ContactMessageResponse {

    private Long id;
    private String name;
    private String email;
    private String message;
    private LocalDateTime createdAt;

    public ContactMessageResponse() {
    }

    public ContactMessageResponse(Long id, String name, String email, String message, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.message = message;
        this.createdAt = createdAt;
    }

    public static ContactMessageResponse fromEntity(ContactMessage entity) {
        if (entity == null) return null;
        return new ContactMessageResponse(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getMessage(),
                entity.getCreatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
