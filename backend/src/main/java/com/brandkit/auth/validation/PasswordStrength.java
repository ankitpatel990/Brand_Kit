package com.brandkit.auth.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Password Strength Validation Annotation
 * 
 * FRD-001 FR-5: Password Requirements
 * - Minimum 8 characters
 * - At least one uppercase letter (A-Z)
 * - At least one lowercase letter (a-z)
 * - At least one digit (0-9)
 * - At least one special character (!@#$%^&*()_+-=[]{}|;:,.<>?)
 */
@Documented
@Constraint(validatedBy = PasswordStrengthValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordStrength {
    String message() default "Password must be at least 8 characters with uppercase, lowercase, number, and special character";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
