package org.mcintyrelab.service;

public interface EmailService {
    void sendVerificationEmail(String to, Integer verificationCode);
}
