package com.arun.portfolio.dto;

import com.arun.portfolio.entity.Skill;

/**
 * Data Transfer Object for Skill response.
 */
public class SkillResponse {

    private Long id;
    private String name;
    private String category;
    private String proficiency;

    public SkillResponse() {
    }

    public SkillResponse(Long id, String name, String category, String proficiency) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.proficiency = proficiency;
    }

    public static SkillResponse fromEntity(Skill skill) {
        if (skill == null) return null;
        return new SkillResponse(
                skill.getId(),
                skill.getName(),
                skill.getCategory(),
                skill.getProficiency()
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProficiency() {
        return proficiency;
    }

    public void setProficiency(String proficiency) {
        this.proficiency = proficiency;
    }
}
