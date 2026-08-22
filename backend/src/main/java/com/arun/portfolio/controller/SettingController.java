package com.arun.portfolio.controller;

import com.arun.portfolio.dto.SettingRequest;
import com.arun.portfolio.entity.Setting;
import com.arun.portfolio.repository.SettingRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for application settings and dynamic configurations (e.g. resume URL).
 */
@RestController
@RequestMapping("/api/settings")
public class SettingController {

    private final SettingRepository settingRepository;

    public SettingController(SettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    /**
     * Public endpoint to fetch the current configured resume URL.
     */
    @GetMapping("/resume")
    public ResponseEntity<Map<String, String>> getResumeUrl() {
        String url = settingRepository.findBySettingKey("resume_url")
                .map(Setting::getSettingValue)
                .orElse("");
        Map<String, String> response = new HashMap<>();
        response.put("resumeUrl", url);
        return ResponseEntity.ok(response);
    }

    /**
     * Admin-only endpoint to update the online resume URL.
     */
    @PostMapping("/resume")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> setResumeUrl(@RequestBody(required = false) SettingRequest request) {
        String newUrl = (request != null && request.getValue() != null) ? request.getValue().trim() : "";
        Setting setting = settingRepository.findBySettingKey("resume_url")
                .orElseGet(() -> new Setting("resume_url", newUrl));
        setting.setSettingValue(newUrl);
        settingRepository.save(setting);

        Map<String, String> response = new HashMap<>();
        response.put("resumeUrl", newUrl);
        response.put("message", "Resume URL updated successfully.");
        return ResponseEntity.ok(response);
    }
}
