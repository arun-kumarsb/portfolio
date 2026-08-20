package com.arun.portfolio.service;

import com.arun.portfolio.dto.SkillRequest;
import com.arun.portfolio.dto.SkillResponse;
import com.arun.portfolio.entity.Skill;
import com.arun.portfolio.exception.ResourceNotFoundException;
import com.arun.portfolio.repository.SkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Layer handling business logic and CRUD operations for Skills.
 */
@Service
@Transactional
public class SkillService {

    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    /**
     * Retrieve all skills as DTOs.
     */
    @Transactional(readOnly = true)
    public List<SkillResponse> getAllSkills() {
        return skillRepository.findAll()
                .stream()
                .map(SkillResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Retrieve skills by category.
     */
    @Transactional(readOnly = true)
    public List<SkillResponse> getSkillsByCategory(String category) {
        return skillRepository.findByCategoryIgnoreCase(category)
                .stream()
                .map(SkillResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Create a new skill (Admin only).
     */
    public SkillResponse createSkill(SkillRequest request) {
        Skill skill = new Skill(
                request.getName().trim(),
                request.getCategory().trim(),
                request.getProficiency().trim()
        );

        Skill saved = skillRepository.save(skill);
        return SkillResponse.fromEntity(saved);
    }

    /**
     * Update an existing skill (Admin only).
     */
    public SkillResponse updateSkill(Long id, SkillRequest request) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + id));

        skill.setName(request.getName().trim());
        skill.setCategory(request.getCategory().trim());
        skill.setProficiency(request.getProficiency().trim());

        Skill updated = skillRepository.save(skill);
        return SkillResponse.fromEntity(updated);
    }

    /**
     * Delete a skill by ID (Admin only).
     */
    public void deleteSkill(Long id) {
        if (!skillRepository.existsById(id)) {
            throw new ResourceNotFoundException("Skill not found with id: " + id);
        }
        skillRepository.deleteById(id);
    }
}
