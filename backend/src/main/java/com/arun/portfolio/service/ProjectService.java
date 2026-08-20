package com.arun.portfolio.service;

import com.arun.portfolio.dto.ProjectRequest;
import com.arun.portfolio.dto.ProjectResponse;
import com.arun.portfolio.entity.Project;
import com.arun.portfolio.exception.ResourceNotFoundException;
import com.arun.portfolio.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Layer handling business logic and CRUD operations for Projects.
 */
@Service
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    /**
     * Retrieve all projects as DTOs.
     */
    @Transactional(readOnly = true)
    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(ProjectResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Retrieve single project by ID.
     */
    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
        return ProjectResponse.fromEntity(project);
    }

    /**
     * Create a new project (Admin only).
     */
    public ProjectResponse createProject(ProjectRequest request) {
        Project project = new Project(
                request.getTitle().trim(),
                request.getDescription().trim(),
                request.getTechnologies().trim(),
                request.getGithubUrl() != null ? request.getGithubUrl().trim() : "",
                request.getLiveUrl() != null ? request.getLiveUrl().trim() : "",
                request.getImageUrl() != null ? request.getImageUrl().trim() : "",
                request.getFeatured() != null ? request.getFeatured() : false
        );

        Project saved = projectRepository.save(project);
        return ProjectResponse.fromEntity(saved);
    }

    /**
     * Update an existing project (Admin only).
     */
    public ProjectResponse updateProject(Long id, ProjectRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));

        project.setTitle(request.getTitle().trim());
        project.setDescription(request.getDescription().trim());
        project.setTechnologies(request.getTechnologies().trim());
        project.setGithubUrl(request.getGithubUrl() != null ? request.getGithubUrl().trim() : "");
        project.setLiveUrl(request.getLiveUrl() != null ? request.getLiveUrl().trim() : "");
        project.setImageUrl(request.getImageUrl() != null ? request.getImageUrl().trim() : "");
        if (request.getFeatured() != null) {
            project.setFeatured(request.getFeatured());
        }

        Project updated = projectRepository.save(project);
        return ProjectResponse.fromEntity(updated);
    }

    /**
     * Delete a project by ID (Admin only).
     */
    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Project not found with id: " + id);
        }
        projectRepository.deleteById(id);
    }
}
