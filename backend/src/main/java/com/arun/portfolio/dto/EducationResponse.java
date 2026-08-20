package com.arun.portfolio.dto;

import com.arun.portfolio.entity.Education;

/**
 * Data Transfer Object for Education response.
 */
public class EducationResponse {

    private Long id;
    private String institution;
    private String degree;
    private String field;
    private String startDate;
    private String endDate;
    private String description;

    public EducationResponse() {
    }

    public EducationResponse(Long id, String institution, String degree, String field, String startDate, String endDate, String description) {
        this.id = id;
        this.institution = institution;
        this.degree = degree;
        this.field = field;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
    }

    public static EducationResponse fromEntity(Education education) {
        if (education == null) return null;
        return new EducationResponse(
                education.getId(),
                education.getInstitution(),
                education.getDegree(),
                education.getField(),
                education.getStartDate(),
                education.getEndDate(),
                education.getDescription()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
