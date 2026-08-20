package com.arun.portfolio.service;

import com.arun.portfolio.dto.EducationRequest;
import com.arun.portfolio.dto.EducationResponse;
import com.arun.portfolio.entity.Education;
import com.arun.portfolio.exception.ResourceNotFoundException;
import com.arun.portfolio.repository.EducationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Layer handling business logic and CRUD operations for Education records.
 */
@Service
@Transactional
public class EducationService {

    private final EducationRepository educationRepository;

    public EducationService(EducationRepository educationRepository) {
        this.educationRepository = educationRepository;
    }

    /**
     * Retrieve all education records as DTOs.
     */
    @Transactional(readOnly = true)
    public List<EducationResponse> getAllEducation() {
        return educationRepository.findAll()
                .stream()
                .map(EducationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Create a new education record (Admin only).
     */
    public EducationResponse createEducation(EducationRequest request) {
        Education education = new Education(
                request.getInstitution().trim(),
                request.getDegree().trim(),
                request.getField() != null ? request.getField().trim() : "",
                request.getStartDate() != null ? request.getStartDate().trim() : "",
                request.getEndDate() != null ? request.getEndDate().trim() : "",
                request.getDescription() != null ? request.getDescription().trim() : ""
        );

        Education saved = educationRepository.save(education);
        return EducationResponse.fromEntity(saved);
    }

    /**
     * Update an existing education record (Admin only).
     */
    public EducationResponse updateEducation(Long id, EducationRequest request) {
        Education education = educationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Education record not found with id: " + id));

        education.setInstitution(request.getInstitution().trim());
        education.setDegree(request.getDegree().trim());
        education.setField(request.getField() != null ? request.getField().trim() : "");
        education.setStartDate(request.getStartDate() != null ? request.getStartDate().trim() : "");
        education.setEndDate(request.getEndDate() != null ? request.getEndDate().trim() : "");
        education.setDescription(request.getDescription() != null ? request.getDescription().trim() : "");

        Education updated = educationRepository.save(education);
        return EducationResponse.fromEntity(updated);
    }

    /**
     * Delete an education record by ID (Admin only).
     */
    public void deleteEducation(Long id) {
        if (!educationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Education record not found with id: " + id);
        }
        educationRepository.deleteById(id);
    }
}
