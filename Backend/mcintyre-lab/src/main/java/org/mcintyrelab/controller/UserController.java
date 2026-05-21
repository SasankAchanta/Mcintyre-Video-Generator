package org.mcintyrelab.controller;

import org.mcintyrelab.dto.user.UserDto;
import org.mcintyrelab.dto.user.request.AllUsersRequest;
import org.mcintyrelab.dto.user.request.PasswordUpdateRequest;
import org.mcintyrelab.dto.user.request.UsernameUpdateRequest;
import org.mcintyrelab.dto.user.response.AllUsersResponse;
import org.mcintyrelab.dto.user.response.PasswordUpdateResponse;
import org.mcintyrelab.dto.user.response.UsernameUpdateResponse;
import org.mcintyrelab.service.impl.UserServiceImpl;
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
public class UserController {
    private final UserServiceImpl userServiceImpl;

    public UserController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @PreAuthorize("hasAnyRole('RESEARCHER', 'ADMIN', 'TECH')")
    @PostMapping("/user/update/password")
    public ResponseEntity<PasswordUpdateResponse> updatePassword(@RequestBody PasswordUpdateRequest passwordUpdateRequest, @AuthenticationPrincipal UserDetails userDetails) {
        // JWT Token has been checked and it's valid
        String username = userDetails.getUsername();
        userServiceImpl.updatePassword(username, passwordUpdateRequest);
        return ResponseEntity.ok(new PasswordUpdateResponse("Password updated successfully"));
    }

    @PreAuthorize("hasRole('TECH')")
    @PostMapping("/tech/update/username")
    public ResponseEntity<UsernameUpdateResponse> updateUsername(@RequestBody UsernameUpdateRequest usernameUpdateRequest, @AuthenticationPrincipal UserDetails actorDetails) {
        // JWT Token has been checked and it's valid
        String actorUsername = actorDetails.getUsername();
        userServiceImpl.updateUsername(actorUsername, usernameUpdateRequest);
        return ResponseEntity.ok(new UsernameUpdateResponse("Username updated successfully"));
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
        Page<UserDto> userPage = userServiceImpl.getAllUsers(allUsersRequest, pageable);
        AllUsersResponse allUsersResponse = new AllUsersResponse(userPage);
        return ResponseEntity.ok(allUsersResponse);
    }




}
