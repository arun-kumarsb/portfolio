package com.arun.portfolio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating or updating education qualifications.
 */
public class EducationRequest {

    @NotBlank(message = "Institution name is required")
    private String institution;

    @NotBlank(message = "Degree title is required")
    private String degree;

    private String field;
    private String startDate;
    private String endDate;

    @Size(max = 1500, message = "Description must not exceed 1500 characters")
    private String description;

    public EducationRequest() {
    }

    public EducationRequest(String institution, String degree, String field, String startDate, String endDate, String description) {
        this.institution = institution;
        this.degree = degree;
        this.field = field;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
