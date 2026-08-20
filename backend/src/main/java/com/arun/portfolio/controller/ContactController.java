package com.arun.portfolio.controller;

import com.arun.portfolio.dto.ContactMessageResponse;
import com.arun.portfolio.dto.ContactRequest;
import com.arun.portfolio.dto.ContactResponse;
import com.arun.portfolio.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller exposing Contact submission and administration endpoints.
 */
@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    /**
     * POST /api/contact - Submit a contact message with Jakarta validation (Public)
     */
    @PostMapping
    public ResponseEntity<ContactResponse> submitContactMessage(@Valid @RequestBody ContactRequest request) {
        ContactResponse response = contactService.saveMessage(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * GET /api/contact - Retrieve all contact messages in inbox (Admin only)
     */
    @GetMapping
    public ResponseEntity<List<ContactMessageResponse>> getAllMessages() {
        List<ContactMessageResponse> messages = contactService.getAllMessages();
        return ResponseEntity.ok(messages);
    }

    /**
     * DELETE /api/contact/{id} - Delete a contact message (Admin only)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteMessage(@PathVariable Long id) {
        contactService.deleteMessage(id);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("message", "Contact message deleted successfully with id: " + id);
        return ResponseEntity.ok(response);
    }
}
