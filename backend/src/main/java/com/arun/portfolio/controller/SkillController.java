package com.arun.portfolio.controller;

import com.arun.portfolio.dto.SkillRequest;
import com.arun.portfolio.dto.SkillResponse;
import com.arun.portfolio.service.SkillService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller exposing Technical Skills endpoints.
 */
@RestController
@RequestMapping("/api/skills")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    /**
     * GET /api/skills - Retrieve all skills, with optional ?category= filter (Public)
     */
    @GetMapping
    public ResponseEntity<List<SkillResponse>> getSkills(
            @RequestParam(required = false) String category) {
        if (category != null && !category.trim().isEmpty() && !category.equalsIgnoreCase("ALL")) {
            return ResponseEntity.ok(skillService.getSkillsByCategory(category.trim()));
        }
        return ResponseEntity.ok(skillService.getAllSkills());
    }

    /**
     * POST /api/skills - Create a new skill (Admin only)
     */
    @PostMapping
    public ResponseEntity<SkillResponse> createSkill(@Valid @RequestBody SkillRequest request) {
        SkillResponse created = skillService.createSkill(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * PUT /api/skills/{id} - Update an existing skill (Admin only)
     */
    @PutMapping("/{id}")
    public ResponseEntity<SkillResponse> updateSkill(@PathVariable Long id, @Valid @RequestBody SkillRequest request) {
        SkillResponse updated = skillService.updateSkill(id, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /api/skills/{id} - Delete a skill (Admin only)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteSkill(@PathVariable Long id) {
        skillService.deleteSkill(id);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("message", "Skill deleted successfully with id: " + id);
        return ResponseEntity.ok(response);
    }
}
