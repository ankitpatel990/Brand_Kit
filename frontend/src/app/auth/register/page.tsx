'use client';

import React, { useState } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { motion } from 'framer-motion';
import toast from 'react-hot-toast';
import { AxiosError } from 'axios';

import { AuthLayout } from '@/components/auth/AuthLayout';
import { SocialAuthButtons } from '@/components/auth/SocialAuthButtons';
import { Input } from '@/components/ui/Input';
import { Button } from '@/components/ui/Button';
import { Select } from '@/components/ui/Select';
import { Checkbox } from '@/components/ui/Checkbox';
import { PasswordStrength } from '@/components/ui/PasswordStrength';
import { registerSchema, RegisterFormData } from '@/lib/validation';
import { authApi, ErrorResponse } from '@/lib/api';
import { formatPhoneNumber } from '@/lib/utils';

/**
 * Registration Page
 * FRD-001 FR-1: Email-Based Registration
 */

const userTypeOptions = [
  { value: 'CLIENT', label: 'Client - Order promotional items' },
  { value: 'PARTNER', label: 'Partner - Fulfill orders' },
];

export default function RegisterPage() {
  const router = useRouter();
  const [isLoading, setIsLoading] = useState(false);

  const {
    register,
    handleSubmit,
    watch,
    setValue,
    formState: { errors },
  } = useForm<RegisterFormData>({
    resolver: zodResolver(registerSchema),
    defaultValues: {
      termsAccepted: false,
    },
  });

  const password = watch('password', '');

  // Format phone number on change
  const handlePhoneChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const formatted = formatPhoneNumber(e.target.value);
    setValue('phone', formatted);
  };

  const onSubmit = async (data: RegisterFormData) => {
    setIsLoading(true);

    try {
      await authApi.register({
        fullName: data.fullName,
        email: data.email,
        password: data.password,
        confirmPassword: data.confirmPassword,
        companyName: data.companyName,
        phone: data.phone || undefined,
        userType: data.userType,
        termsAccepted: data.termsAccepted,
      });

      toast.success('Registration successful! You can now log in.');
      router.push('/auth/login');
    } catch (error) {
      const axiosError = error as AxiosError<ErrorResponse>;
      const errorData = axiosError.response?.data;

      if (errorData?.errorCode === 'AUTH_001') {
        toast.error('This email is already registered. Please log in or reset your password.');
      } else if (errorData?.fieldErrors) {
        // Show first field error
        const firstError = Object.values(errorData.fieldErrors)[0];
        toast.error(firstError);
      } else {
        toast.error(errorData?.message || 'Registration failed. Please try again.');
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <AuthLayout
      title="Create your account"
      subtitle="Start designing custom promotional items"
    >
      {/* Social Auth */}
      <SocialAuthButtons mode="register" />

      {/* Registration Form */}
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.1 }}
        >
          <Input
            id="fullName"
            type="text"
            label="Full Name"
            placeholder="John Doe"
            autoComplete="name"
            required
            error={errors.fullName?.message}
            {...register('fullName')}
          />
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.15 }}
        >
          <Input
            id="email"
            type="email"
            label="Email Address"
            placeholder="you@company.com"
            autoComplete="email"
            required
            error={errors.email?.message}
            {...register('email')}
          />
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.2 }}
        >
          <Input
            id="companyName"
            type="text"
            label="Company Name"
            placeholder="Acme Corporation"
            autoComplete="organization"
            required
            error={errors.companyName?.message}
            {...register('companyName')}
          />
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.25 }}
        >
          <Input
            id="phone"
            type="tel"
            label="Phone Number"
            placeholder="+91-9876543210"
            autoComplete="tel"
            helperText="Optional. Format: +91-XXXXXXXXXX"
            error={errors.phone?.message}
            {...register('phone')}
            onChange={handlePhoneChange}
          />
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.3 }}
        >
          <Select
            id="userType"
            label="I want to..."
            options={userTypeOptions}
            required
            error={errors.userType?.message}
            {...register('userType')}
          />
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.35 }}
        >
          <Input
            id="password"
            type="password"
            label="Password"
            placeholder="Create a strong password"
            autoComplete="new-password"
            required
            error={errors.password?.message}
            {...register('password')}
          />
          <PasswordStrength password={password} />
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.4 }}
        >
          <Input
            id="confirmPassword"
            type="password"
            label="Confirm Password"
            placeholder="Confirm your password"
            autoComplete="new-password"
            required
            error={errors.confirmPassword?.message}
            {...register('confirmPassword')}
          />
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.45 }}
        >
          <Checkbox
            id="termsAccepted"
            label={
              <>
                I agree to the{' '}
                <Link href="/terms" className="link" target="_blank">
                  Terms of Service
                </Link>{' '}
                and{' '}
                <Link href="/privacy" className="link" target="_blank">
                  Privacy Policy
                </Link>
              </>
            }
            error={errors.termsAccepted?.message}
            {...register('termsAccepted')}
          />
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.5 }}
        >
          <Button
            type="submit"
            className="w-full"
            isLoading={isLoading}
          >
            Create Account
          </Button>
        </motion.div>
      </form>

      {/* Sign In Link */}
      <motion.p
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ delay: 0.55 }}
        className="text-center text-sm text-slate-500 mt-6"
      >
        Already have an account?{' '}
        <Link href="/auth/login" className="link">
          Sign in
        </Link>
      </motion.p>
    </AuthLayout>
  );
}
