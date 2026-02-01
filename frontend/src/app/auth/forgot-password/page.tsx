'use client';

import React, { useState } from 'react';
import Link from 'next/link';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { motion } from 'framer-motion';
import toast from 'react-hot-toast';
import { ArrowLeft, Mail } from 'lucide-react';

import { AuthLayout } from '@/components/auth/AuthLayout';
import { Input } from '@/components/ui/Input';
import { Button } from '@/components/ui/Button';
import { forgotPasswordSchema, ForgotPasswordFormData } from '@/lib/validation';
import { authApi } from '@/lib/api';

/**
 * Forgot Password Page
 * FRD-001 FR-7: Password Reset Workflow
 */

export default function ForgotPasswordPage() {
  const [isLoading, setIsLoading] = useState(false);
  const [isSuccess, setIsSuccess] = useState(false);
  const [email, setEmail] = useState('');

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<ForgotPasswordFormData>({
    resolver: zodResolver(forgotPasswordSchema),
  });

  const onSubmit = async (data: ForgotPasswordFormData) => {
    setIsLoading(true);

    try {
      await authApi.forgotPassword(data.email);
      setEmail(data.email);
      setIsSuccess(true);
    } catch (error) {
      // Always show success for security (no email enumeration)
      setEmail(data.email);
      setIsSuccess(true);
    } finally {
      setIsLoading(false);
    }
  };

  if (isSuccess) {
    return (
      <AuthLayout
        title="Check your email"
        subtitle="We've sent you a password reset link"
      >
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="text-center"
        >
          <div className="w-16 h-16 bg-brand-100 rounded-full flex items-center justify-center mx-auto mb-6">
            <Mail className="w-8 h-8 text-brand-600" />
          </div>
          
          <p className="text-slate-600 mb-6">
            If an account exists for <strong>{email}</strong>, you will receive
            a password reset link shortly.
          </p>

          <p className="text-sm text-slate-500 mb-6">
            The link will expire in 1 hour.
          </p>

          <Button
            variant="secondary"
            className="w-full"
            onClick={() => setIsSuccess(false)}
          >
            Try a different email
          </Button>

          <Link
            href="/auth/login"
            className="inline-flex items-center justify-center gap-2 text-sm text-slate-500 hover:text-slate-700 mt-4"
          >
            <ArrowLeft size={16} />
            Back to sign in
          </Link>
        </motion.div>
      </AuthLayout>
    );
  }

  return (
    <AuthLayout
      title="Forgot password?"
      subtitle="No worries, we'll send you reset instructions"
    >
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
          <Button
            type="submit"
            className="w-full"
            isLoading={isLoading}
          >
            Send Reset Link
          </Button>
        </motion.div>
      </form>

      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ delay: 0.2 }}
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
