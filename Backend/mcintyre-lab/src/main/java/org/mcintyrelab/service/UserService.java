package org.mcintyrelab.service;


import org.mcintyrelab.dto.user.UserDto;
import org.mcintyrelab.dto.user.request.AllUsersRequest;
import org.mcintyrelab.dto.user.request.PasswordUpdateRequest;
import org.mcintyrelab.dto.user.request.UsernameUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
     void updatePassword(String username, PasswordUpdateRequest passwordUpdateRequest);
     void updateUsername(String techUsername, UsernameUpdateRequest usernameUpdateRequest);
     Page<UserDto> getAllUsers(AllUsersRequest allUsersRequest, Pageable pageable);

}
