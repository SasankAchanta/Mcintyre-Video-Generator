package org.mcintyrelab.controller;

import org.mcintyrelab.dto.user.UserDto;
import org.mcintyrelab.dto.user.request.*;
import org.mcintyrelab.dto.user.response.*;
import org.mcintyrelab.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mcintyre-lab/v1")
@CrossOrigin
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasAnyRole('RESEARCHER', 'ADMIN', 'TECH')")
    @PatchMapping("/user/update/password")
    public ResponseEntity<PasswordUpdateResponse> updatePassword(@RequestBody PasswordUpdateRequest passwordUpdateRequest, @AuthenticationPrincipal UserDetails userDetails) {
        // JWT Token has been checked and it's valid
        String username = userDetails.getUsername();
        userService.updatePassword(username, passwordUpdateRequest);
        return ResponseEntity.ok(new PasswordUpdateResponse("Password updated successfully"));
    }

    @PreAuthorize("hasRole('TECH')")
    @PatchMapping("/tech/update/username")
    public ResponseEntity<UsernameUpdateResponse> updateUsername(@RequestBody UsernameUpdateRequest usernameUpdateRequest, @AuthenticationPrincipal UserDetails actorDetails) {
        // JWT Token has been checked and it's valid
        String actorUsername = actorDetails.getUsername();
        userService.updateUsername(actorUsername, usernameUpdateRequest);
        return ResponseEntity.ok(new UsernameUpdateResponse("Username updated successfully"));
    }

    @PreAuthorize("hasRole('TECH')")
    @PatchMapping("/tech/update/role")
    public ResponseEntity<RoleUpdateResponse> updateRole(@RequestBody RoleUpdateRequest roleUpdateRequest, @AuthenticationPrincipal UserDetails actorDetails) {
        // JWT Token has been checked and it's valid
        String actorUsername = actorDetails.getUsername();
        userService.updateRole(actorUsername, roleUpdateRequest);
        return ResponseEntity.ok(new RoleUpdateResponse("Role updated successfully"));
    }

    @PreAuthorize("hasRole('TECH')")
    @PatchMapping("/tech/update/name")
    public ResponseEntity<NameUpdateResponse> updateName(@RequestBody NameUpdateRequest nameUpdateRequest, @AuthenticationPrincipal UserDetails actorDetails) {
        // JWT Token has been checked and it's valid
        String actorUsername = actorDetails.getUsername();
        userService.updateName(actorUsername, nameUpdateRequest);
        return ResponseEntity.ok(new NameUpdateResponse("Name updated successfully"));
    }

    @PreAuthorize("hasAnyRole('RESEARCHER', 'ADMIN', 'TECH')")
    @GetMapping("/user/all")
    public ResponseEntity<AllUsersResponse> getAllUsers(@ModelAttribute AllUsersRequest allUsersRequest,
                                                        // This annotation forces Spring to sort alphabetically if the URL doesn't specify sorting
                                                        @PageableDefault(
                                                                size = 5,
                                                                sort = {"lastName", "firstName"},
                                                                direction = org.springframework.data.domain.Sort.Direction.ASC
                                                        ) Pageable pageable) {
        Page<UserDto> userPage = userService.getAllUsers(allUsersRequest, pageable);
        AllUsersResponse allUsersResponse = new AllUsersResponse(userPage);
        return ResponseEntity.ok(allUsersResponse);
    }
}
