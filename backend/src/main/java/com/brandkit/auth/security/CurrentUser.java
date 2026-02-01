package com.brandkit.auth.security;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.*;

/**
 * Current User Annotation
 * 
 * Custom annotation to inject current authenticated user
 * FRD-001: Used in controllers to get current user context
 */
@Target({ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AuthenticationPrincipal
public @interface CurrentUser {
}
