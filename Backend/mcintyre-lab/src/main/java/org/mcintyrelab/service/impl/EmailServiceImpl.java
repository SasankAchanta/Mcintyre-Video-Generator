package org.mcintyrelab.service.impl;

import jakarta.mail.internet.MimeMessage;
import org.mcintyrelab.service.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

@Service
@EnableAsync
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    @Override
    @Async
    public void sendVerificationEmail(String to, Integer verificationCode) {
        try {
            // 1. Create a MimeMessage instead of SimpleMailMessage
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            // 2. Use MimeMessageHelper to configure the email properties (true flag indicates multipart/HTML)
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setTo(to);
            helper.setFrom("no-reply@universal-build.com"); // Ensure this matches your Brevo verified sender address!
            helper.setSubject("Verify Your McIntyre Lab Account");

            // 3. Craft your beautiful HTML layout using inline styles
            String htmlContent = """
            <div style="font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; max-width: 500px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px;">
                <h2 style="color: #155724; border-bottom: 2px solid #d4edda; padding-bottom: 10px;">McIntyre Lab</h2>
                <p style="font-size: 16px; color: #333333;">Thank you for registering! Please use the verification code below to activate your account:</p>
                
                <div style="text-align: center; margin: 30px 0;">
                    <span style="display: inline-block; font-size: 32px; font-weight: bold; letter-spacing: 6px; color: #0f5132; background-color: #d1e7dd; padding: 12px 30px; border-radius: 6px; border: 1px solid #badbcc;">
                        %d
                    </span>
                </div>
                
                <p style="font-size: 14px; color: #666666; line-height: 1.5;">
                    This code is valid for <strong>15 minutes</strong>. If you did not request this registration, you can safely ignore this email.
                </p>
                <hr style="border: 0; border-top: 1px solid #e0e0e0; margin: 20px 0;">
                <p style="font-size: 12px; color: #999999; text-align: center;">Automated notification system — Please do not reply directly.</p>
            </div>
            """.formatted(verificationCode);

            // 4. Set the text and pass 'true' to signal it is HTML
            helper.setText(htmlContent, true);

            // 5. Send it away!
            mailSender.send(mimeMessage);

        } catch (jakarta.mail.MessagingException e) {
            // Log the exception securely
            throw new RuntimeException("Failed to send beautiful verification email", e);
        }
    }
}
