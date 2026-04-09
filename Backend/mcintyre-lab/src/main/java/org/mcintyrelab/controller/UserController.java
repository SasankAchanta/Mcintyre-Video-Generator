package org.mcintyrelab.controller;

import org.mcintyrelab.dto.CreateUser;
import org.mcintyrelab.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mcintyre-lab/v1/user")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity <String> createUser(@RequestBody CreateUser createUser) {
        userService.createUser(createUser);
        return ResponseEntity.ok("User created successfully");
    }
}
