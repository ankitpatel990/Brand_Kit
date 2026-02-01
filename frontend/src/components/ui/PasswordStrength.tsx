'use client';

import React from 'react';
import { Check, X } from 'lucide-react';
import { cn } from '@/lib/utils';

/**
 * Password Strength Indicator
 * FRD-001 FR-5: Real-time validation with visual feedback
 */

interface PasswordStrengthProps {
  password: string;
}

interface Requirement {
  label: string;
  test: (password: string) => boolean;
}

const requirements: Requirement[] = [
  { label: 'At least 8 characters', test: (p) => p.length >= 8 },
  { label: 'One uppercase letter', test: (p) => /[A-Z]/.test(p) },
  { label: 'One lowercase letter', test: (p) => /[a-z]/.test(p) },
  { label: 'One number', test: (p) => /[0-9]/.test(p) },
  { label: 'One special character', test: (p) => /[!@#$%^&*()_+\-=\[\]{}|;:,.<>?]/.test(p) },
];

export function PasswordStrength({ password }: PasswordStrengthProps) {
  const passedCount = requirements.filter((req) => req.test(password)).length;
  
  const strengthLevel = passedCount === 0 ? 0 : passedCount <= 2 ? 1 : passedCount <= 4 ? 2 : 3;
  const strengthLabels = ['', 'Weak', 'Medium', 'Strong'];
  const strengthColors = ['bg-slate-200', 'bg-red-500', 'bg-yellow-500', 'bg-green-500'];

  if (!password) return null;

  return (
    <div className="mt-3 space-y-3">
      {/* Strength bar */}
      <div className="space-y-1">
        <div className="flex gap-1">
          {[1, 2, 3].map((level) => (
            <div
              key={level}
              className={cn(
                'h-1.5 flex-1 rounded-full transition-all duration-300',
                level <= strengthLevel ? strengthColors[strengthLevel] : 'bg-slate-200'
              )}
            />
          ))}
        </div>
        {strengthLevel > 0 && (
          <p
            className={cn(
              'text-xs font-medium',
              strengthLevel === 1 && 'text-red-500',
              strengthLevel === 2 && 'text-yellow-600',
              strengthLevel === 3 && 'text-green-600'
            )}
          >
            {strengthLabels[strengthLevel]} password
          </p>
        )}
      </div>

      {/* Requirements checklist */}
      <ul className="space-y-1">
        {requirements.map((req) => {
          const passed = req.test(password);
          return (
            <li
              key={req.label}
              className={cn(
                'flex items-center gap-2 text-xs transition-colors',
                passed ? 'text-green-600' : 'text-slate-400'
              )}
            >
              {passed ? (
                <Check className="w-3.5 h-3.5" />
              ) : (
                <X className="w-3.5 h-3.5" />
              )}
              {req.label}
            </li>
          );
        })}
      </ul>
    </div>
  );
}
