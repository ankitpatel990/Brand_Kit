import { z } from 'zod';

/**
 * Validation Schemas
 * FRD-001: Form validation matching backend requirements
 */

// Password validation regex
const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=\[\]{}|;:,.<>?]).{8,}$/;

// Phone validation regex (Indian format)
const phoneRegex = /^\+91-[0-9]{10}$/;

/**
 * Registration Schema
 * FRD-001 FR-1: Email-Based Registration validation
 */
export const registerSchema = z.object({
  fullName: z
    .string()
    .min(2, 'Full name must be at least 2 characters')
    .max(100, 'Full name must be at most 100 characters')
    .regex(/^[a-zA-Z\s]+$/, 'Full name can only contain letters and spaces'),
  email: z
    .string()
    .email('Please enter a valid email address')
    .max(255),
  password: z
    .string()
    .min(8, 'Password must be at least 8 characters')
    .regex(
      passwordRegex,
      'Password must contain uppercase, lowercase, number, and special character'
    ),
  confirmPassword: z.string(),
  companyName: z
    .string()
    .min(2, 'Company name must be at least 2 characters')
    .max(200, 'Company name must be at most 200 characters'),
  phone: z
    .string()
    .regex(phoneRegex, 'Phone must be in format: +91-XXXXXXXXXX')
    .optional()
    .or(z.literal('')),
  userType: z.enum(['CLIENT', 'PARTNER'], {
    required_error: 'Please select a user type',
  }),
  termsAccepted: z.literal(true, {
    errorMap: () => ({ message: 'You must accept the Terms & Conditions' }),
  }),
}).refine((data) => data.password === data.confirmPassword, {
  message: 'Passwords do not match',
  path: ['confirmPassword'],
});

export type RegisterFormData = z.infer<typeof registerSchema>;

/**
 * Login Schema
 * FRD-001 FR-6: Login validation
 */
export const loginSchema = z.object({
  email: z.string().email('Please enter a valid email address'),
  password: z.string().min(1, 'Password is required'),
  rememberMe: z.boolean().optional(),
});

export type LoginFormData = z.infer<typeof loginSchema>;

/**
 * Forgot Password Schema
 * FRD-001 FR-7: Forgot Password validation
 */
export const forgotPasswordSchema = z.object({
  email: z.string().email('Please enter a valid email address'),
});

export type ForgotPasswordFormData = z.infer<typeof forgotPasswordSchema>;

/**
 * Reset Password Schema
 * FRD-001 FR-7: Reset Password validation
 */
export const resetPasswordSchema = z.object({
  password: z
    .string()
    .min(8, 'Password must be at least 8 characters')
    .regex(
      passwordRegex,
      'Password must contain uppercase, lowercase, number, and special character'
    ),
  confirmPassword: z.string(),
}).refine((data) => data.password === data.confirmPassword, {
  message: 'Passwords do not match',
  path: ['confirmPassword'],
});

export type ResetPasswordFormData = z.infer<typeof resetPasswordSchema>;

/**
 * Change Password Schema
 * FRD-001 FR-10: Change Password validation
 */
export const changePasswordSchema = z.object({
  currentPassword: z.string().min(1, 'Current password is required'),
  newPassword: z
    .string()
    .min(8, 'Password must be at least 8 characters')
    .regex(
      passwordRegex,
      'Password must contain uppercase, lowercase, number, and special character'
    ),
  confirmPassword: z.string(),
}).refine((data) => data.newPassword === data.confirmPassword, {
  message: 'Passwords do not match',
  path: ['confirmPassword'],
}).refine((data) => data.currentPassword !== data.newPassword, {
  message: 'New password must be different from current password',
  path: ['newPassword'],
});

export type ChangePasswordFormData = z.infer<typeof changePasswordSchema>;

/**
 * Profile Update Schema
 * FRD-001 FR-10: Profile Update validation
 */
export const profileUpdateSchema = z.object({
  fullName: z
    .string()
    .min(2, 'Full name must be at least 2 characters')
    .max(100, 'Full name must be at most 100 characters')
    .regex(/^[a-zA-Z\s]+$/, 'Full name can only contain letters and spaces')
    .optional(),
  companyName: z
    .string()
    .min(2, 'Company name must be at least 2 characters')
    .max(200, 'Company name must be at most 200 characters')
    .optional(),
  phone: z
    .string()
    .regex(phoneRegex, 'Phone must be in format: +91-XXXXXXXXXX')
    .optional()
    .or(z.literal('')),
});

export type ProfileUpdateFormData = z.infer<typeof profileUpdateSchema>;

/**
 * OAuth Additional Info Schema
 * FRD-001 BR-10: Mandatory Fields for Social Auth
 */
export const oauthAdditionalInfoSchema = z.object({
  companyName: z
    .string()
    .min(2, 'Company name must be at least 2 characters')
    .max(200, 'Company name must be at most 200 characters'),
  phone: z
    .string()
    .regex(phoneRegex, 'Phone must be in format: +91-XXXXXXXXXX')
    .optional()
    .or(z.literal('')),
  userType: z.enum(['CLIENT', 'PARTNER'], {
    required_error: 'Please select a user type',
  }),
  termsAccepted: z.literal(true, {
    errorMap: () => ({ message: 'You must accept the Terms & Conditions' }),
  }),
});

export type OAuthAdditionalInfoFormData = z.infer<typeof oauthAdditionalInfoSchema>;
