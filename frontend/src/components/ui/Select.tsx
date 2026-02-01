'use client';

import React, { forwardRef } from 'react';
import { ChevronDown } from 'lucide-react';
import { cn } from '@/lib/utils';

/**
 * Select Component
 * FRD-001: Dropdown select for user type selection
 */

interface SelectOption {
  value: string;
  label: string;
}

interface SelectProps extends Omit<React.SelectHTMLAttributes<HTMLSelectElement>, 'children'> {
  label?: string;
  error?: string;
  options: SelectOption[];
  placeholder?: string;
}

export const Select = forwardRef<HTMLSelectElement, SelectProps>(
  ({ className, label, error, options, placeholder = 'Select an option', ...props }, ref) => {
    return (
      <div className="w-full">
        {label && (
          <label className="label" htmlFor={props.id}>
            {label}
            {props.required && <span className="text-red-500 ml-1">*</span>}
          </label>
        )}
        <div className="relative">
          <select
            className={cn(
              'input appearance-none pr-10',
              error && 'input-error',
              !props.value && 'text-slate-400',
              className
            )}
            ref={ref}
            aria-invalid={error ? 'true' : 'false'}
            {...props}
          >
            <option value="" disabled>
              {placeholder}
            </option>
            {options.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
          <ChevronDown
            className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none"
            size={20}
          />
        </div>
        {error && (
          <p className="error-message" role="alert">
            {error}
          </p>
        )}
      </div>
    );
  }
);

Select.displayName = 'Select';
