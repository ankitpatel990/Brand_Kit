package com.brandkit.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Google reCAPTCHA Verification Service
 * 
 * FRD-001 FR-12: Security Features
 * - Google reCAPTCHA v3 on registration and after failed logins
 */
@Service
public class CaptchaService {
    private static final Logger log = LoggerFactory.getLogger(CaptchaService.class);

    private static final String RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    @Autowired
    private RestTemplate restTemplate;

    @Value("${app.recaptcha.secret-key:}")
    private String secretKey;

    @Value("${app.recaptcha.threshold:0.5}")
    private double threshold;

    /**
     * Verify reCAPTCHA token
     * FRD-001 FR-12: CAPTCHA verification after failed attempts
     * 
     * @param token The reCAPTCHA token from frontend
     * @param remoteIp The client IP address
     * @return true if verification passed, false otherwise
     */
    public boolean verifyCaptcha(String token, String remoteIp) {
        if (secretKey == null || secretKey.isEmpty()) {
            log.warn("reCAPTCHA secret key not configured, skipping verification");
            return true; // Skip in development
        }

        if (token == null || token.isEmpty()) {
            log.debug("Empty CAPTCHA token");
            return false;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("secret", secretKey);
            params.add("response", token);
            params.add("remoteip", remoteIp);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(
                    RECAPTCHA_VERIFY_URL, request, Map.class);

            if (response == null) {
                log.error("Null response from reCAPTCHA API");
                return false;
            }

            boolean success = Boolean.TRUE.equals(response.get("success"));
            double score = response.containsKey("score") 
                    ? ((Number) response.get("score")).doubleValue() 
                    : 0.0;

            log.debug("reCAPTCHA verification: success={}, score={}", success, score);

            // For reCAPTCHA v3, check both success and score
            return success && score >= threshold;

        } catch (Exception e) {
            log.error("reCAPTCHA verification failed: {}", e.getMessage());
            return false;
        }
    }

    public RestTemplate getRestTemplate() {
        return this.restTemplate;
    }
    public String getSecretKey() {
        return this.secretKey;
    }
    public double getThreshold() {
        return this.threshold;
    }
}
