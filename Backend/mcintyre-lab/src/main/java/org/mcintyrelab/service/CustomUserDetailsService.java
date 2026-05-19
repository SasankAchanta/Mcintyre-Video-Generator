package org.mcintyrelab.service;

import org.mcintyrelab.model.User;
import org.mcintyrelab.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    // Spring Security calls this automatically when it needs to find a user
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 1. look up YOUR user entity from the database
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " +username));

        // 3. convert YOUR user entity into a Spring Security UserDetails object
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),   // username
                user.getPassword(),   // password Spring Security will verify
                Collections.emptyList()  // roles/authorities (empty for now)
        );
    }
}
