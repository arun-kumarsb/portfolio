package com.arun.portfolio.controller;

import com.arun.portfolio.dto.EducationRequest;
import com.arun.portfolio.dto.EducationResponse;
import com.arun.portfolio.service.EducationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller exposing Education history endpoints.
 */
@RestController
@RequestMapping("/api/education")
public class EducationController {

    private final EducationService educationService;

    public EducationController(EducationService educationService) {
        this.educationService = educationService;
    }

    /**
     * GET /api/education - Retrieve all education qualifications (Public)
     */
    @GetMapping
    public ResponseEntity<List<EducationResponse>> getAllEducation() {
        List<EducationResponse> educationList = educationService.getAllEducation();
        return ResponseEntity.ok(educationList);
    }

    /**
     * POST /api/education - Create a new education record (Admin only)
     */
    @PostMapping
    public ResponseEntity<EducationResponse> createEducation(@Valid @RequestBody EducationRequest request) {
        EducationResponse created = educationService.createEducation(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * PUT /api/education/{id} - Update an existing education record (Admin only)
     */
    @PutMapping("/{id}")
    public ResponseEntity<EducationResponse> updateEducation(@PathVariable Long id, @Valid @RequestBody EducationRequest request) {
        EducationResponse updated = educationService.updateEducation(id, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /api/education/{id} - Delete an education record (Admin only)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteEducation(@PathVariable Long id) {
        educationService.deleteEducation(id);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("message", "Education record deleted successfully with id: " + id);
        return ResponseEntity.ok(response);
    }
}
