package org.mcintyrelab.service;


import org.mcintyrelab.dto.user.UserDto;
import org.mcintyrelab.dto.user.request.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
     void updatePassword(String username, PasswordUpdateRequest passwordUpdateRequest);
     void updateUsername(String techUsername, UsernameUpdateRequest usernameUpdateRequest);
     void updateRole(String actorUsername, RoleUpdateRequest roleUpdateRequest);
     Page<UserDto> getAllUsers(AllUsersRequest allUsersRequest, Pageable pageable);
    void updateName(String actorUsername, NameUpdateRequest nameUpdateRequest);
}
