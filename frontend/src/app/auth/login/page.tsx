'use client';

import React, { useState } from 'react';
import Link from 'next/link';
import { useRouter, useSearchParams } from 'next/navigation';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { motion } from 'framer-motion';
import toast from 'react-hot-toast';
import { AxiosError } from 'axios';

import { AuthLayout } from '@/components/auth/AuthLayout';
import { SocialAuthButtons } from '@/components/auth/SocialAuthButtons';
import { Input } from '@/components/ui/Input';
import { Button } from '@/components/ui/Button';
import { Checkbox } from '@/components/ui/Checkbox';
import { loginSchema, LoginFormData } from '@/lib/validation';
import { authApi, ErrorResponse, setAccessToken } from '@/lib/api';

/**
 * Login Page
 * FRD-001 FR-6: Login Functionality
 */

export default function LoginPage() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const [isLoading, setIsLoading] = useState(false);
  const [requiresCaptcha, setRequiresCaptcha] = useState(false);

  // Show error from URL params (e.g., OAuth failure)
  const errorParam = searchParams.get('error');
  React.useEffect(() => {
    if (errorParam === 'oauth_failed') {
      toast.error('OAuth authorization failed. Please try again.');
    } else if (errorParam === 'session_expired') {
      toast.error('Your session has expired. Please log in again.');
    }
  }, [errorParam]);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      rememberMe: false,
    },
  });

  const onSubmit = async (data: LoginFormData) => {
    setIsLoading(true);

    try {
      const response = await authApi.login({
        email: data.email,
        password: data.password,
        rememberMe: data.rememberMe,
        // TODO: Add captcha token when requiresCaptcha is true
      });

      toast.success('Welcome back!');

      // Redirect based on role
      const role = response.data.role;
      if (role === 'ADMIN') {
        router.push('/admin/dashboard');
      } else if (role === 'PARTNER') {
        router.push('/partner/dashboard');
      } else {
        router.push('/dashboard');
      }
    } catch (error) {
      const axiosError = error as AxiosError<ErrorResponse>;
      const errorData = axiosError.response?.data;

      if (errorData?.errorCode === 'AUTH_017') {
        setRequiresCaptcha(true);
        toast.error('Please complete the CAPTCHA verification');
      } else if (errorData?.errorCode === 'AUTH_006') {
        toast.error('Please verify your email before logging in');
      } else if (errorData?.errorCode === 'AUTH_008') {
        toast.error(errorData.message || 'Account locked. Please try again later.');
      } else {
        toast.error(errorData?.message || 'Invalid email or password');
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <AuthLayout
      title="Welcome back"
      subtitle="Sign in to your BrandKit account"
    >
      {/* Social Auth */}
      <SocialAuthButtons mode="login" />

      {/* Login Form */}
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.1 }}
        >
          <Input
            id="email"
            type="email"
            label="Email Address"
            placeholder="you@company.com"
            autoComplete="email"
            error={errors.email?.message}
            {...register('email')}
          />
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.15 }}
        >
          <Input
            id="password"
            type="password"
            label="Password"
            placeholder="Enter your password"
            autoComplete="current-password"
            error={errors.password?.message}
            {...register('password')}
          />
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.2 }}
          className="flex items-center justify-between"
        >
          <Checkbox
            id="rememberMe"
            label="Remember me for 30 days"
            {...register('rememberMe')}
          />
          <Link href="/auth/forgot-password" className="link text-sm">
            Forgot password?
          </Link>
        </motion.div>

        {/* TODO: Add reCAPTCHA when requiresCaptcha is true */}

        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.25 }}
        >
          <Button
            type="submit"
            className="w-full"
            isLoading={isLoading}
          >
            Sign In
          </Button>
        </motion.div>
      </form>

      {/* Sign Up Link */}
      <motion.p
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ delay: 0.3 }}
        className="text-center text-sm text-slate-500 mt-6"
      >
        Don&apos;t have an account?{' '}
        <Link href="/auth/register" className="link">
          Create account
        </Link>
      </motion.p>
    </AuthLayout>
  );
}
