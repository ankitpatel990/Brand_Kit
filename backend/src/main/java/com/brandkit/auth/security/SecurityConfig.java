package com.brandkit.auth.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Security Configuration
 * 
 * FRD-001 FR-8: Role-Based Access Control
 * FRD-001 FR-9: JWT-based Session Management
 * FRD-001 FR-12: Security Features
 * 
 * Route Protection:
 * - Client routes: /api/products, /api/orders (own orders only)
 * - Partner routes: /api/partner/* (partner dashboard)
 * - Admin routes: /api/admin/* (admin panel)
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Security Filter Chain
     * FRD-001 FR-8: Role-Based Access Control implementation
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for stateless JWT auth
            .csrf(AbstractHttpConfigurer::disable)
            
            // Enable CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Stateless session management
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Authorization rules
            .authorizeHttpRequests(auth -> auth
                // Public endpoints - no authentication required
                .requestMatchers(
                    "/api/auth/register",
                    "/api/auth/login",
                    "/api/auth/verify-email",
                    "/api/auth/resend-verification",
                    "/api/auth/forgot-password",
                    "/api/auth/reset-password",
                    "/api/auth/refresh",
                    "/api/auth/google",
                    "/api/auth/google/callback",
                    "/api/auth/linkedin",
                    "/api/auth/linkedin/callback",
                    "/oauth2/**",
                    "/actuator/health"
                ).permitAll()
                
                // FRD-002: Public product catalog - no authentication required
                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/products/*/calculate-price").permitAll()
                
                // FRD-001 FR-8: Admin routes - ADMIN role only
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                // FRD-001 FR-8: Partner routes - PARTNER or ADMIN roles
                .requestMatchers("/api/partner/**").hasAnyRole("PARTNER", "ADMIN")
                
                // FRD-001 FR-8: Orders - authenticated users (service layer enforces ownership)
                .requestMatchers("/api/orders/**").authenticated()
                
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            
            // OAuth2 login configuration
            .oauth2Login(oauth2 -> oauth2
                .authorizationEndpoint(endpoint -> endpoint
                    .baseUri("/oauth2/authorization"))
            )
            
            // Add JWT filter before UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS Configuration
     * FRD-001 NFR-6: Security for cross-origin requests
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
            "http://localhost:3000",
            "https://brandkit.in"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", 
            "Content-Type", 
            "X-Requested-With",
            "Accept",
            "Origin"
        ));
        configuration.setExposedHeaders(List.of("X-Token-Expired"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
