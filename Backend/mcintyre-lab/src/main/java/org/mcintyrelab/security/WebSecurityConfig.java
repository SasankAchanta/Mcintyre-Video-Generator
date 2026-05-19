package org.mcintyrelab.security;

import org.mcintyrelab.service.CustomUserDetailsService;
import org.mcintyrelab.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class WebSecurityConfig {
    private final UserService userService;
    private final AuthEntryPointJwt unauthorizedHandler;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    public WebSecurityConfig(UserService userService, AuthEntryPointJwt unauthorizedHandler, JwtUtil jwtUtil, CustomUserDetailsService customUserDetailsService) {
        this.userService = userService;
        this.unauthorizedHandler = unauthorizedHandler;
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
    }

    // creates our JWT filter bean so Spring can inject it
    @Bean
    public AuthTokenFilter authTokenFilter() {
        return new AuthTokenFilter(jwtUtil, customUserDetailsService);
    }

    // Spring Security uses this to authenticate username/password during login
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // BCrypt password encoder for hashing passwords before saving to DB
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // disable CSRF since we're using JWT (CSRF is only needed for session-based auth)
                .csrf(AbstractHttpConfigurer::disable)

                // disable CORS for now (you can configure this later for your frontend)
                .cors(AbstractHttpConfigurer::disable)

                // when an unauthenticated user hits a protected endpoint, use our custom 401 handler
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(unauthorizedHandler)
                )

                // don't create sessions - JWT is stateless so we don't need them
                .sessionManagement(s -> s
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // define which endpoints are public and which require authentication
                .authorizeHttpRequests(a -> a
                        // allow anyone to hit auth endpoints (login, register)
                        .requestMatchers("/mcintyre-lab/v1/auth/**").permitAll()
                        // everything else requires a valid JWT token
                        .anyRequest().authenticated()
                )

                // add our JWT filter before Spring Security's default login filter
                .addFilterBefore(authTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}