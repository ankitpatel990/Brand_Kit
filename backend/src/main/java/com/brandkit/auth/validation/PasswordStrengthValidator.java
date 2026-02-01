package com.brandkit.auth.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * Password Strength Validator
 * 
 * FRD-001 FR-5: Password Requirements Implementation
 * Validates password meets all security requirements
 */
public class PasswordStrengthValidator implements ConstraintValidator<PasswordStrength, String> {

    private static final int MIN_LENGTH = 8;
    private static final Pattern UPPERCASE = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE = Pattern.compile("[a-z]");
    private static final Pattern DIGIT = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{}|;:,.<>?]");

    @Override
    public void initialize(PasswordStrength constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.isEmpty()) {
            return false;
        }

        boolean valid = true;
        StringBuilder message = new StringBuilder("Password must contain: ");

        if (password.length() < MIN_LENGTH) {
            message.append("at least 8 characters, ");
            valid = false;
        }

        if (!UPPERCASE.matcher(password).find()) {
            message.append("an uppercase letter, ");
            valid = false;
        }

        if (!LOWERCASE.matcher(password).find()) {
            message.append("a lowercase letter, ");
            valid = false;
        }

        if (!DIGIT.matcher(password).find()) {
            message.append("a number, ");
            valid = false;
        }

        if (!SPECIAL.matcher(password).find()) {
            message.append("a special character (!@#$%^&*()_+-=[]{}|;:,.<>?), ");
            valid = false;
        }

        if (!valid) {
            // Customize error message
            context.disableDefaultConstraintViolation();
            String finalMessage = message.toString().replaceAll(", $", "");
            context.buildConstraintViolationWithTemplate(finalMessage)
                    .addConstraintViolation();
        }

        return valid;
    }
}
