package com.arun.portfolio.dto;

import java.time.LocalDateTime;

/**
 * Data Transfer Object returned upon successfully submitting a contact message.
 */
public class ContactResponse {

    private Long id;
    private String status;
    private String message;
    private LocalDateTime timestamp;

    public ContactResponse() {
    }

    public ContactResponse(Long id, String status, String message, LocalDateTime timestamp) {
        this.id = id;
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
