package com.brandkit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

/**
 * Application Configuration
 * 
 * General configuration beans for the BrandKit application
 */
@Configuration
@EnableAsync
public class AppConfig {

    /**
     * RestTemplate for external API calls (e.g., reCAPTCHA verification)
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * Password Encoder
     * FRD-001 FR-5, NFR-7: bcrypt with salt rounds 10
     * Defined here to avoid circular dependency with SecurityConfig
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
