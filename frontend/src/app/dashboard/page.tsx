'use client';

import React from 'react';
import Link from 'next/link';
import { Package, ShoppingBag, FileText, Settings, LogOut } from 'lucide-react';
import { Button } from '@/components/ui/Button';

/**
 * Client Dashboard
 * FRD-001 FR-8: Role-Based Access - Client Dashboard
 */

export default function DashboardPage() {
  return (
    <div className="min-h-screen bg-slate-50">
      {/* Header */}
      <header className="bg-white border-b border-slate-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between h-16">
            <div className="flex items-center gap-2">
              <div className="w-9 h-9 bg-gradient-to-br from-brand-500 to-brand-700 rounded-xl flex items-center justify-center">
                <span className="text-white font-bold text-lg font-display">B</span>
              </div>
              <span className="text-xl font-bold font-display text-slate-900">
                Brand<span className="text-brand-600">Kit</span>
              </span>
            </div>

            <div className="flex items-center gap-4">
              <Link href="/auth/profile">
                <Button variant="ghost" size="sm" leftIcon={<Settings size={18} />}>
                  Profile
                </Button>
              </Link>
              <Link href="/api/auth/logout">
                <Button variant="ghost" size="sm" leftIcon={<LogOut size={18} />}>
                  Logout
                </Button>
              </Link>
            </div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="mb-8">
          <h1 className="text-2xl font-bold font-display text-slate-900">
            Welcome to BrandKit
          </h1>
          <p className="text-slate-600">
            Start designing custom promotional merchandise for your business.
          </p>
        </div>

        {/* Quick Actions */}
        <div className="grid md:grid-cols-3 gap-6 mb-8">
          <div className="card p-6 hover:shadow-xl transition-shadow">
            <div className="w-12 h-12 bg-brand-100 rounded-xl flex items-center justify-center mb-4">
              <Package className="w-6 h-6 text-brand-600" />
            </div>
            <h3 className="text-lg font-semibold text-slate-900 mb-2">
              Browse Products
            </h3>
            <p className="text-slate-600 text-sm mb-4">
              Explore our catalog of customizable promotional items.
            </p>
            <Button variant="secondary" size="sm">
              View Catalog
            </Button>
          </div>

          <div className="card p-6 hover:shadow-xl transition-shadow">
            <div className="w-12 h-12 bg-accent-100 rounded-xl flex items-center justify-center mb-4">
              <ShoppingBag className="w-6 h-6 text-accent-600" />
            </div>
            <h3 className="text-lg font-semibold text-slate-900 mb-2">
              My Orders
            </h3>
            <p className="text-slate-600 text-sm mb-4">
              Track your orders and view order history.
            </p>
            <Button variant="secondary" size="sm">
              View Orders
            </Button>
          </div>

          <div className="card p-6 hover:shadow-xl transition-shadow">
            <div className="w-12 h-12 bg-green-100 rounded-xl flex items-center justify-center mb-4">
              <FileText className="w-6 h-6 text-green-600" />
            </div>
            <h3 className="text-lg font-semibold text-slate-900 mb-2">
              Saved Designs
            </h3>
            <p className="text-slate-600 text-sm mb-4">
              Access your saved designs and reorder favorites.
            </p>
            <Button variant="secondary" size="sm">
              View Designs
            </Button>
          </div>
        </div>

        {/* Recent Orders Placeholder */}
        <div className="card">
          <div className="p-6 border-b border-slate-100">
            <h2 className="text-lg font-semibold text-slate-900">Recent Orders</h2>
          </div>
          <div className="p-12 text-center">
            <ShoppingBag className="w-12 h-12 text-slate-300 mx-auto mb-4" />
            <p className="text-slate-500 mb-4">No orders yet</p>
            <Button>Start Shopping</Button>
          </div>
        </div>
      </main>
    </div>
  );
}
