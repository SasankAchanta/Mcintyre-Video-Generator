package org.mcintyrelab.service;

import org.mcintyrelab.dto.auth.request.LoginRequest;
import org.mcintyrelab.dto.auth.request.RegisterRequest;
import org.mcintyrelab.dto.auth.request.VerifyEmailRequest;
import org.mcintyrelab.dto.auth.response.LoginResponse;
import org.mcintyrelab.dto.auth.response.RegisterResponse;

public interface AuthService {
     LoginResponse login(LoginRequest request);
     RegisterResponse register(RegisterRequest registerRequest);
     Integer generateVerificationCode();
     Boolean checkVerificationCode(VerifyEmailRequest verifyEmailRequest);
     void resendVerificationCode(String email);
}
