package com.brandkit.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.brandkit.auth.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * Email Service
 * 
 * FRD-001 FR-2: Email Verification
 * FRD-001 FR-7: Password Reset Workflow
 * 
 * Sends transactional emails for:
 * - Email verification
 * - Password reset
 */
@Service
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.email.from-name}")
    private String fromName;

    @Value("${app.email.verification-subject}")
    private String verificationSubject;

    @Value("${app.email.password-reset-subject}")
    private String passwordResetSubject;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${app.frontend.verification-path}")
    private String verificationPath;

    @Value("${app.frontend.password-reset-path}")
    private String passwordResetPath;

    /**
     * Send email verification link
     * FRD-001 FR-2: Email Verification
     */
    @Async
    public void sendVerificationEmail(User user, String token) {
        String verificationLink = frontendUrl + verificationPath + "?token=" + token;
        
        String htmlContent = buildVerificationEmailHtml(user.getFullName(), verificationLink);
        
        try {
            sendHtmlEmail(user.getEmail(), verificationSubject, htmlContent);
            log.info("Verification email sent to {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send verification email to {}: {}", user.getEmail(), e.getMessage());
            // Queue for retry in production
        }
    }

    /**
     * Send password reset link
     * FRD-001 FR-7: Password Reset Workflow
     * Note: Always send generic message for security (no email enumeration)
     */
    @Async
    public void sendPasswordResetEmail(User user, String token) {
        String resetLink = frontendUrl + passwordResetPath + "?token=" + token;
        
        String htmlContent = buildPasswordResetEmailHtml(user.getFullName(), resetLink);
        
        try {
            sendHtmlEmail(user.getEmail(), passwordResetSubject, htmlContent);
            log.info("Password reset email sent to {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", user.getEmail(), e.getMessage());
            // Queue for retry in production
        }
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        
        mailSender.send(message);
    }

    private String buildVerificationEmailHtml(String name, String link) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: #2563eb; color: white; padding: 20px; text-align: center; }
                    .content { padding: 30px; background: #f9fafb; }
                    .button { display: inline-block; background: #2563eb; color: white; padding: 12px 30px; 
                              text-decoration: none; border-radius: 6px; margin: 20px 0; }
                    .footer { text-align: center; padding: 20px; color: #6b7280; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>BrandKit</h1>
                    </div>
                    <div class="content">
                        <h2>Verify Your Email Address</h2>
                        <p>Hi %s,</p>
                        <p>Thank you for registering with BrandKit! Please verify your email address by clicking the button below:</p>
                        <p><a href="%s" class="button">Verify Email</a></p>
                        <p>This link will expire in 24 hours.</p>
                        <p>If you didn't create an account with BrandKit, you can safely ignore this email.</p>
                        <p>Thanks,<br>The BrandKit Team</p>
                    </div>
                    <div class="footer">
                        <p>&copy; 2026 BrandKit. All rights reserved.</p>
                        <p>If the button doesn't work, copy and paste this link: %s</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(name, link, link);
    }

    private String buildPasswordResetEmailHtml(String name, String link) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: #2563eb; color: white; padding: 20px; text-align: center; }
                    .content { padding: 30px; background: #f9fafb; }
                    .button { display: inline-block; background: #2563eb; color: white; padding: 12px 30px; 
                              text-decoration: none; border-radius: 6px; margin: 20px 0; }
                    .footer { text-align: center; padding: 20px; color: #6b7280; font-size: 12px; }
                    .warning { color: #dc2626; font-size: 13px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>BrandKit</h1>
                    </div>
                    <div class="content">
                        <h2>Reset Your Password</h2>
                        <p>Hi %s,</p>
                        <p>You requested to reset your password. Click the button below to set a new password:</p>
                        <p><a href="%s" class="button">Reset Password</a></p>
                        <p class="warning">This link will expire in 1 hour.</p>
                        <p>If you didn't request this password reset, please ignore this email. Your password will remain unchanged.</p>
                        <p>Thanks,<br>The BrandKit Team</p>
                    </div>
                    <div class="footer">
                        <p>&copy; 2026 BrandKit. All rights reserved.</p>
                        <p>If the button doesn't work, copy and paste this link: %s</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(name, link, link);
    }

    public JavaMailSender getMailSender() {
        return this.mailSender;
    }
    public String getFromEmail() {
        return this.fromEmail;
    }
    public String getFromName() {
        return this.fromName;
    }
    public String getVerificationSubject() {
        return this.verificationSubject;
    }
    public String getPasswordResetSubject() {
        return this.passwordResetSubject;
    }
    public String getFrontendUrl() {
        return this.frontendUrl;
    }
    public String getVerificationPath() {
        return this.verificationPath;
    }
    public String getPasswordResetPath() {
        return this.passwordResetPath;
    }
}
