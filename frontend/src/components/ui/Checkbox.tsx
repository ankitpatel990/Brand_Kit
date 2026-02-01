'use client';

import React, { forwardRef } from 'react';
import { Check } from 'lucide-react';
import { cn } from '@/lib/utils';

/**
 * Checkbox Component
 * FRD-001: Terms acceptance checkbox
 */

interface CheckboxProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: React.ReactNode;
  error?: string;
}

export const Checkbox = forwardRef<HTMLInputElement, CheckboxProps>(
  ({ className, label, error, ...props }, ref) => {
    return (
      <div className="w-full">
        <label className="flex items-start gap-3 cursor-pointer group">
          <div className="relative flex-shrink-0 mt-0.5">
            <input
              type="checkbox"
              className={cn(
                'peer sr-only',
                className
              )}
              ref={ref}
              {...props}
            />
            <div
              className={cn(
                'w-5 h-5 border-2 rounded-md transition-all duration-200',
                'border-slate-300 bg-white',
                'peer-checked:bg-brand-600 peer-checked:border-brand-600',
                'peer-focus:ring-2 peer-focus:ring-brand-500 peer-focus:ring-offset-2',
                'group-hover:border-slate-400',
                error && 'border-red-300'
              )}
            />
            <Check
              className={cn(
                'absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2',
                'w-3.5 h-3.5 text-white opacity-0 transition-opacity',
                'peer-checked:opacity-100'
              )}
            />
          </div>
          {label && (
            <span className="text-sm text-slate-600 leading-tight">
              {label}
            </span>
          )}
        </label>
        {error && (
          <p className="error-message mt-1" role="alert">
            {error}
          </p>
        )}
      </div>
    );
  }
);

Checkbox.displayName = 'Checkbox';
