'use client';

import React, { useState, Suspense } from 'react';
import Link from 'next/link';
import { useRouter, useSearchParams } from 'next/navigation';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { motion } from 'framer-motion';
import toast from 'react-hot-toast';
import { AxiosError } from 'axios';
import { ArrowLeft, CheckCircle } from 'lucide-react';

import { AuthLayout } from '@/components/auth/AuthLayout';
import { Input } from '@/components/ui/Input';
import { Button } from '@/components/ui/Button';
import { PasswordStrength } from '@/components/ui/PasswordStrength';
import { resetPasswordSchema, ResetPasswordFormData } from '@/lib/validation';
import { authApi, ErrorResponse } from '@/lib/api';

/**
 * Reset Password Page
 * FRD-001 FR-7: Password Reset
 */

function ResetPasswordContent() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const token = searchParams.get('token');
  
  const [isLoading, setIsLoading] = useState(false);
  const [isSuccess, setIsSuccess] = useState(false);

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors },
  } = useForm<ResetPasswordFormData>({
    resolver: zodResolver(resetPasswordSchema),
  });

  const password = watch('password', '');

  // If no token, show error
  if (!token) {
    return (
      <AuthLayout
        title="Invalid Link"
        subtitle="This password reset link is invalid"
      >
        <div className="text-center">
          <p className="text-slate-600 mb-6">
            The password reset link is missing or invalid. Please request a new one.
          </p>
          <Link href="/auth/forgot-password">
            <Button className="w-full">Request New Link</Button>
          </Link>
        </div>
      </AuthLayout>
    );
  }

  const onSubmit = async (data: ResetPasswordFormData) => {
    setIsLoading(true);

    try {
      await authApi.resetPassword({
        token,
        password: data.password,
        confirmPassword: data.confirmPassword,
      });

      setIsSuccess(true);
      toast.success('Password reset successful!');
      
      // Redirect to dashboard after short delay
      setTimeout(() => {
        router.push('/dashboard');
      }, 2000);
    } catch (error) {
      const axiosError = error as AxiosError<ErrorResponse>;
      const errorData = axiosError.response?.data;

      if (errorData?.errorCode === 'AUTH_010') {
        toast.error('This link has expired. Please request a new one.');
      } else if (errorData?.errorCode === 'AUTH_014') {
        toast.error('This link has already been used or is invalid.');
      } else {
        toast.error(errorData?.message || 'Failed to reset password. Please try again.');
      }
    } finally {
      setIsLoading(false);
    }
  };

  if (isSuccess) {
    return (
      <AuthLayout
        title="Password Reset!"
        subtitle="Your password has been successfully reset"
      >
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="text-center"
        >
          <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-6">
            <CheckCircle className="w-8 h-8 text-green-600" />
          </div>
          
          <p className="text-slate-600 mb-6">
            You're now logged in. Redirecting to your dashboard...
          </p>

          <Link href="/dashboard">
            <Button className="w-full">Go to Dashboard</Button>
          </Link>
        </motion.div>
      </AuthLayout>
    );
  }

  return (
    <AuthLayout
      title="Set new password"
      subtitle="Create a strong, secure password"
    >
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.1 }}
        >
          <Input
            id="password"
            type="password"
            label="New Password"
            placeholder="Create a strong password"
            autoComplete="new-password"
            error={errors.password?.message}
            {...register('password')}
          />
          <PasswordStrength password={password} />
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.15 }}
        >
          <Input
            id="confirmPassword"
            type="password"
            label="Confirm Password"
            placeholder="Confirm your password"
            autoComplete="new-password"
            error={errors.confirmPassword?.message}
            {...register('confirmPassword')}
          />
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.2 }}
        >
          <Button
            type="submit"
            className="w-full"
            isLoading={isLoading}
          >
            Reset Password
          </Button>
        </motion.div>
      </form>

      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ delay: 0.25 }}
        className="text-center mt-6"
      >
        <Link
          href="/auth/login"
          className="inline-flex items-center justify-center gap-2 text-sm text-slate-500 hover:text-slate-700"
        >
          <ArrowLeft size={16} />
          Back to sign in
        </Link>
      </motion.div>
    </AuthLayout>
  );
}

export default function ResetPasswordPage() {
  return (
    <Suspense fallback={
      <AuthLayout title="Loading..." subtitle="">
        <div className="flex justify-center py-8">
          <div className="animate-spin w-8 h-8 border-4 border-brand-600 border-t-transparent rounded-full" />
        </div>
      </AuthLayout>
    }>
      <ResetPasswordContent />
    </Suspense>
  );
}
