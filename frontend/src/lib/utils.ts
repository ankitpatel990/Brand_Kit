import { type ClassValue, clsx } from 'clsx';
import { twMerge } from 'tailwind-merge';

/**
 * Utility function to merge Tailwind CSS classes
 */
export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

/**
 * Password strength validation
 * FRD-001 FR-5: Password Requirements
 */
export function validatePasswordStrength(password: string): {
  isValid: boolean;
  errors: string[];
  strength: 'weak' | 'medium' | 'strong';
} {
  const errors: string[] = [];
  
  if (password.length < 8) {
    errors.push('At least 8 characters');
  }
  if (!/[A-Z]/.test(password)) {
    errors.push('One uppercase letter');
  }
  if (!/[a-z]/.test(password)) {
    errors.push('One lowercase letter');
  }
  if (!/[0-9]/.test(password)) {
    errors.push('One number');
  }
  if (!/[!@#$%^&*()_+\-=\[\]{}|;:,.<>?]/.test(password)) {
    errors.push('One special character');
  }

  const isValid = errors.length === 0;
  let strength: 'weak' | 'medium' | 'strong' = 'weak';
  
  if (password.length >= 8) {
    const criteriaCount = 5 - errors.length;
    if (criteriaCount >= 4) strength = 'strong';
    else if (criteriaCount >= 2) strength = 'medium';
  }

  return { isValid, errors, strength };
}

/**
 * Phone number formatting for Indian format
 */
export function formatPhoneNumber(value: string): string {
  // Remove all non-digits
  const digits = value.replace(/\D/g, '');
  
  // Limit to 10 digits (after country code)
  const limited = digits.slice(0, 12);
  
  // Format as +91-XXXXXXXXXX
  if (limited.length === 0) return '';
  if (limited.startsWith('91') && limited.length > 2) {
    return `+91-${limited.slice(2)}`;
  }
  if (limited.length <= 10) {
    return `+91-${limited}`;
  }
  return `+91-${limited.slice(2)}`;
}

/**
 * Email validation
 */
export function isValidEmail(email: string): boolean {
  const emailRegex = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
  return emailRegex.test(email);
}

/**
 * Get initials from name
 */
export function getInitials(name: string): string {
  return name
    .split(' ')
    .map((word) => word[0])
    .join('')
    .toUpperCase()
    .slice(0, 2);
}
