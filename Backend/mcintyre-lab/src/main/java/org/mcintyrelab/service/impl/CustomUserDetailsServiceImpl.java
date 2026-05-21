package org.mcintyrelab.service.impl;

import org.mcintyrelab.model.User;
import org.mcintyrelab.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsServiceImpl(UserRepository userRepository) {
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
                List.of(new SimpleGrantedAuthority(user.getRole().name())) //  Specific role for user
        );
    }
}
