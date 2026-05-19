package org.mcintyrelab.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    // Spring Security calls this automatically when an unauthenticated user
    // tries to access a protected endpoint
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        // sends back a 401 HTTP status code with a message
        // 401 means "you need to be logged in to access this"
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "401 Unauthorized");
    }
}
