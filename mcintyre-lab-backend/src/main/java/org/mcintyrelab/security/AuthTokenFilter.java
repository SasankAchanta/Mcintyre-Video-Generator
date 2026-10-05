package org.mcintyrelab.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.mcintyrelab.service.impl.CustomUserDetailsServiceImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class AuthTokenFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private static final String BEARER_ = "Bearer ";
    private final CustomUserDetailsServiceImpl customUserDetailsServiceImpl;

    public AuthTokenFilter(JwtUtil jwtUtil, CustomUserDetailsServiceImpl customUserDetailsServiceImpl) {
        this.jwtUtil = jwtUtil;
        this.customUserDetailsServiceImpl = customUserDetailsServiceImpl;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtil.validateToken(jwt)) {
                final String username = jwtUtil.getUsernameFromToken(jwt);
                final UserDetails userDetails = customUserDetailsServiceImpl.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication {}", e.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(BEARER_.length());
        }
        return null;
    }
}
/*
When a lab member hits your /update/password endpoint, Spring Security intercepts the request before it ever reaches your controller. Here is the exact sequence of events:

The Request Arrives: A user sends an HTTP request with a JWT token in the header.

The JWT Filter Intercepts It: Your custom security filter reads the token, verifies that it hasn't expired, and extracts the username.

Your Service is Called: The filter calls your CustomUserDetailsService (the class we looked at earlier). Your database is queried, and you return that built-in Spring User object (which implements UserDetails).

Stored in Context: Spring Security takes that UserDetails object and puts it into the SecurityContextHolder. It is now the official "proof of identity" for that specific HTTP request.
 */
