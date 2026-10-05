package org.mcintyrelab.service;

import org.mcintyrelab.dto.project.ProjectDto;
import org.mcintyrelab.dto.project.request.AllProjectsRequest;
import org.mcintyrelab.dto.project.request.CreateProjectRequest;
import org.mcintyrelab.dto.project.request.UpdateProjectRequest;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface ProjectService {
    void createProject(String username, CreateProjectRequest createProjectRequest);

    Page<ProjectDto> getAllProjects(AllProjectsRequest allProjectsRequest, Pageable pageable);

    Page<ProjectDto> getYourProjects(AllProjectsRequest allProjectsRequest, Pageable pageable, String username);

    ProjectDto getProjectById(UUID projectId, String username, boolean isManagement);

    ProjectDto updateProject(UUID projectId, UpdateProjectRequest updateProjectRequest, String username, boolean isManagement);

    void deleteProject(UUID projectId, String username, boolean isManagement);
}