package com.arun.portfolio.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for creating or updating a technical skill.
 */
public class SkillRequest {

    @NotBlank(message = "Skill name is required")
    private String name;

    @NotBlank(message = "Category is required (e.g. Programming, Backend, Database, Frontend, Tools, AI/ML)")
    private String category;

    @NotBlank(message = "Proficiency is required (e.g. Advanced, Proficient, Intermediate, Beginner)")
    private String proficiency;

    public SkillRequest() {
    }

    public SkillRequest(String name, String category, String proficiency) {
        this.name = name;
        this.category = category;
        this.proficiency = proficiency;
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
