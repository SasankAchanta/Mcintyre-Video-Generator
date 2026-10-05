package org.mcintyrelab.controller;

import jakarta.validation.Valid;
import org.mcintyrelab.dto.project.ProjectDto;
import org.mcintyrelab.dto.project.request.AllProjectsRequest;
import org.mcintyrelab.dto.project.request.CreateProjectRequest;
import org.mcintyrelab.dto.project.request.UpdateProjectRequest;
import org.mcintyrelab.dto.project.response.AllProjectsResponse;
import org.mcintyrelab.dto.project.response.CreateProjectResponse;
import org.mcintyrelab.service.ProjectService;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import java.util.UUID;

@RestController
@RequestMapping("/mcintyre-lab/v1/project")
@CrossOrigin
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PreAuthorize("hasAnyRole('RESEARCHER', 'ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<CreateProjectResponse> createProject(@Valid @RequestBody CreateProjectRequest createProjectRequest, @AuthenticationPrincipal UserDetails userDetails) {
        // JWT Token has been checked and it's valid
        String username = userDetails.getUsername();
        projectService.createProject(username, createProjectRequest);
        return new ResponseEntity<>(new CreateProjectResponse("Project created successfully!"), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('RESEARCHER','TECH', 'ADMIN')")
    @GetMapping("/my-projects")
    public ResponseEntity<AllProjectsResponse> getYourProjects(
            @Valid @ModelAttribute AllProjectsRequest allProjectsRequest,
            @PageableDefault(
                    size = 5,
                    sort = "projectName",
                    direction = org.springframework.data.domain.Sort.Direction.ASC
            ) Pageable pageable, @AuthenticationPrincipal UserDetails userDetails
    ) {
        // JWT Token has been checked and it's valid
        String username = userDetails.getUsername();

        // 1. Fetch filtered page of DTOs from your service
        Page<ProjectDto> projectPage = projectService.getYourProjects(allProjectsRequest, pageable, username);

        // 3. Return the response payload
        return ResponseEntity.ok(new AllProjectsResponse(projectPage));
    }

    @PreAuthorize("hasAnyRole('TECH', 'ADMIN')") // Restricting visibility to management roles
    @GetMapping("/all")
    public ResponseEntity<AllProjectsResponse> getAllProjects(
            @Valid @ModelAttribute AllProjectsRequest allProjectsRequest,
            @PageableDefault(
                    size = 5,
                    sort = "projectName",
                    direction = org.springframework.data.domain.Sort.Direction.ASC
            ) Pageable pageable
    ) {
        // 1. Fetch filtered page of DTOs from your service
        Page<ProjectDto> projectPage = projectService.getAllProjects(allProjectsRequest, pageable);

        // 3. Return the response payload
        return ResponseEntity.ok(new AllProjectsResponse(projectPage));
    }

    @PreAuthorize("hasAnyRole('RESEARCHER', 'TECH', 'ADMIN')")
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDto> getProjectById(
            @PathVariable UUID projectId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // FIXED: Splitting the checks using standard stream syntax or logical OR
        boolean isManagement = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_TECH"));

        // Pass everything down to the service layer to handle the check safely
        ProjectDto project = projectService.getProjectById(projectId, userDetails.getUsername(), isManagement);
        return ResponseEntity.ok(project);
    }

    @PreAuthorize("hasAnyRole('RESEARCHER', 'TECH', 'ADMIN')")
    @PatchMapping("/{projectId}")
    public ResponseEntity<ProjectDto> updateProject(
            @PathVariable UUID projectId,
            @Valid @RequestBody UpdateProjectRequest updateProjectRequest,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // 1. Calculate if the user has a management role
        boolean isManagement = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_TECH"));

        // 2. Pass the data down to the service layer for validation and execution
        ProjectDto updatedProject = projectService.updateProject(
                projectId,
                updateProjectRequest,
                userDetails.getUsername(),
                isManagement
        );

        // 3. Return a 200 OK along with the freshly updated DTO payload
        return ResponseEntity.ok(updatedProject);
    }


    @PreAuthorize("hasAnyRole('RESEARCHER', 'TECH', 'ADMIN')")
    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(
            @PathVariable UUID projectId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // Determine management status from authentication context
        boolean isManagement = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_TECH"));

        // Delegate deletion to the service layer
        projectService.deleteProject(projectId, userDetails.getUsername(), isManagement);

        // Return a 204 No Content response
        return ResponseEntity.noContent().build();
    }

}
