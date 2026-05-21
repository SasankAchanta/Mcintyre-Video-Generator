package org.mcintyrelab.service;

import org.mcintyrelab.dto.auth.request.LoginRequest;
import org.mcintyrelab.dto.auth.request.RegisterRequest;
import org.mcintyrelab.dto.auth.response.LoginResponse;
import org.mcintyrelab.dto.auth.response.RegisterResponse;

public interface AuthService {
    public LoginResponse login(LoginRequest request);
    public RegisterResponse register(RegisterRequest registerRequest);
}
