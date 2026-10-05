package org.mcintyrelab.service.impl;

import org.mcintyrelab.dto.project.ProjectDto;
import org.mcintyrelab.dto.project.request.AllProjectsRequest;
import org.mcintyrelab.dto.project.request.CreateProjectRequest;
import org.mcintyrelab.dto.project.request.UpdateProjectRequest;
import org.mcintyrelab.dto.user.UserDto;
import org.mcintyrelab.dto.video.VideoDto;
import org.mcintyrelab.exception.badrequest.ResourceNotFoundException;
import org.mcintyrelab.exception.badrequest.UserNotFoundException;
import org.mcintyrelab.model.Project;
import org.mcintyrelab.model.User;
import org.mcintyrelab.repository.ProjectRepository;
import org.mcintyrelab.repository.UserRepository;
import org.mcintyrelab.service.ProjectService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.UUID;

@Service
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void createProject(String username, CreateProjectRequest createProjectRequest) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        Project newProject = Project.builder()
                .user(user)
                .modelType(createProjectRequest.modelType())
                .projectName(createProjectRequest.projectName())
                .description(createProjectRequest.description())
                .build();
        projectRepository.save(newProject);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectDto> getAllProjects(AllProjectsRequest allProjectsRequest, Pageable pageable) {
        LocalDateTime cutoffDate = null;

        // If a month was selected, find its absolute starting point
        if (allProjectsRequest.cutoffMonth() != null) {
            cutoffDate = allProjectsRequest.cutoffMonth().atDay(1).atStartOfDay();
        }

        // Execute the optimized database query with filters
        Page<Project> projects = projectRepository.findWithFilters(
                allProjectsRequest.modelType(),
                cutoffDate,
                pageable
        );

        return projects.map(project -> new ProjectDto(
                project.getProjectId(),
                // FIXED: Matching your exact 6-field UserDto record constructor
                new UserDto(
                        project.getUser().getFirstName(),
                        project.getUser().getLastName(),
                        project.getUser().getProfilePicture(),
                        project.getUser().getEmail(),
                        project.getUser().getRole(),
                        YearMonth.from(project.getUser().getCreatedAt())
                ),
                project.getModelType(),
                project.getProjectName(),
                project.getDescription(),
                project.getVideos().stream().map(video -> new VideoDto(
                        video.getVideoId(),
                        video.getVideoUrl(),
                        video.getVideoType(),
                        video.getCreatedAt()
                )).toList(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        ));
    }

    @Override
    public Page<ProjectDto> getYourProjects(AllProjectsRequest allProjectsRequest, Pageable pageable,  String username) {
        LocalDateTime cutoffDate = null;

        // If a month was selected, find its absolute starting point
        if (allProjectsRequest.cutoffMonth() != null) {
            cutoffDate = allProjectsRequest.cutoffMonth().atDay(1).atStartOfDay();
        }

        // Execute the optimized database query with filters
        Page<Project> projects = projectRepository.findWithFiltersAndUser(
                username,
                allProjectsRequest.modelType(),
                cutoffDate,
                pageable
        );

        return projects.map(project -> new ProjectDto(
                project.getProjectId(),
                // FIXED: Matching your exact 6-field UserDto record constructor
                new UserDto(
                        project.getUser().getFirstName(),
                        project.getUser().getLastName(),
                        project.getUser().getProfilePicture(),
                        project.getUser().getEmail(),
                        project.getUser().getRole(),
                        YearMonth.from(project.getUser().getCreatedAt())
                ),
                project.getModelType(),
                project.getProjectName(),
                project.getDescription(),
                project.getVideos().stream().map(video -> new VideoDto(
                        video.getVideoId(),
                        video.getVideoUrl(),
                        video.getVideoType(),
                        video.getCreatedAt()
                )).toList(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        ));
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectDto getProjectById(UUID projectId, String username, boolean isManagement) {
        // 1. Fetch the project
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        // 2. Security guard: If they aren't management, they must be the owner
        if (!isManagement && !project.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("You do not have permission to view this project.");
        }

        // 3. Return the mapped ProjectDto assembly line
        return new ProjectDto(
                project.getProjectId(),
                new UserDto(
                        project.getUser().getFirstName(),
                        project.getUser().getLastName(),
                        project.getUser().getProfilePicture(),
                        project.getUser().getEmail(),
                        project.getUser().getRole(),
                        YearMonth.from(project.getUser().getCreatedAt())
                ),
                project.getModelType(),
                project.getProjectName(),
                project.getDescription(),
                project.getVideos().stream().map(video -> new VideoDto(
                        video.getVideoId(),
                        video.getVideoUrl(),
                        video.getVideoType(),
                        video.getCreatedAt()
                )).toList(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }

    @Transactional
    public ProjectDto updateProject(UUID projectId, UpdateProjectRequest updateProjectRequest, String username, boolean isManagement) {
        // 1. Fetch the existing project
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        // 2. Security guard: Must be management OR the owner to modify it
        if (!isManagement && !project.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("You do not have permission to update this project.");
        }

        // 3. Make the changes from the request payload
        project.setProjectName(updateProjectRequest.projectName());
        project.setDescription(updateProjectRequest.description());

        // 4. Save and flush changes so updatedAt triggers execute
        Project updatedProject = projectRepository.saveAndFlush(project);

        // 5. Return the fresh DTO back to the controller!
        return new ProjectDto(
                updatedProject.getProjectId(),
                new UserDto(
                        project.getUser().getFirstName(),
                        project.getUser().getLastName(),
                        project.getUser().getProfilePicture(),
                        project.getUser().getEmail(),
                        project.getUser().getRole(),
                        YearMonth.from(project.getUser().getCreatedAt())
                ),
                updatedProject.getModelType(),
                updatedProject.getProjectName(),
                updatedProject.getDescription(),
                updatedProject.getVideos().stream().map(video -> new VideoDto(
                        video.getVideoId(),
                        video.getVideoUrl(),
                        video.getVideoType(),
                        video.getCreatedAt()
                )).toList(),
                updatedProject.getCreatedAt(),
                updatedProject.getUpdatedAt() // <-- Frontend gets the exact new timestamp!
        );
    }

    @Override
    public void deleteProject(UUID projectId, String username, boolean isManagement) {
        // 1. Fetch the project or throw a 404
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        // 2. Security guard: Must be management OR the project owner to delete it
        if (!isManagement && !project.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("You do not have permission to delete this project.");
        }

        // 3. Remove the project from the database
        projectRepository.delete(project);
    }
}
