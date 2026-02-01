'use client';

import React from 'react';

/**
 * Partner Dashboard Layout - FRD-005
 * Shared layout for all partner portal pages
 */

export default function PartnerLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <div className="min-h-screen">
      {children}
    </div>
  );
}
