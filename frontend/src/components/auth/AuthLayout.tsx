'use client';

import React from 'react';
import Link from 'next/link';
import { motion } from 'framer-motion';

/**
 * Auth Layout Component
 * FRD-001: Common layout for authentication pages
 */

interface AuthLayoutProps {
  children: React.ReactNode;
  title: string;
  subtitle?: string;
}

export function AuthLayout({ children, title, subtitle }: AuthLayoutProps) {
  return (
    <div className="auth-bg">
      <div className="relative z-10 w-full max-w-md mx-auto px-4 py-12">
        {/* Logo */}
        <motion.div
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5 }}
          className="text-center mb-8"
        >
          <Link href="/" className="inline-flex items-center gap-2">
            <div className="w-10 h-10 bg-gradient-to-br from-brand-500 to-brand-700 rounded-xl flex items-center justify-center shadow-lg">
              <span className="text-white font-bold text-xl font-display">B</span>
            </div>
            <span className="text-2xl font-bold font-display text-slate-900">
              Brand<span className="text-brand-600">Kit</span>
            </span>
          </Link>
        </motion.div>

        {/* Card */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.1 }}
          className="card p-8"
        >
          {/* Header */}
          <div className="text-center mb-8">
            <h1 className="text-2xl font-bold font-display text-slate-900 mb-2">
              {title}
            </h1>
            {subtitle && (
              <p className="text-slate-500">{subtitle}</p>
            )}
          </div>

          {/* Content */}
          {children}
        </motion.div>

        {/* Footer */}
        <motion.p
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ duration: 0.5, delay: 0.3 }}
          className="text-center text-sm text-slate-500 mt-6"
        >
          &copy; 2026 BrandKit. All rights reserved.
        </motion.p>
      </div>
    </div>
  );
}
