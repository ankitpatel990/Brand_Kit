'use client';

import React from 'react';
import Link from 'next/link';
import { Users, Package, TrendingUp, Settings, LogOut, ShieldCheck } from 'lucide-react';
import { Button } from '@/components/ui/Button';

/**
 * Admin Dashboard
 * FRD-001 FR-8: Role-Based Access - Admin Dashboard
 * FRD-001 FR-11: Account Status Management
 */

export default function AdminDashboardPage() {
  return (
    <div className="min-h-screen bg-slate-50">
      {/* Header */}
      <header className="bg-slate-900 text-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between h-16">
            <div className="flex items-center gap-4">
              <div className="flex items-center gap-2">
                <div className="w-9 h-9 bg-white rounded-xl flex items-center justify-center">
                  <span className="text-brand-600 font-bold text-lg font-display">B</span>
                </div>
                <span className="text-xl font-bold font-display">
                  BrandKit
                </span>
              </div>
              <span className="px-2 py-1 bg-red-500 text-white text-xs font-semibold rounded-full flex items-center gap-1">
                <ShieldCheck size={12} />
                Admin
              </span>
            </div>

            <div className="flex items-center gap-4">
              <Link href="/auth/profile">
                <Button variant="ghost" size="sm" className="text-white hover:bg-slate-800" leftIcon={<Settings size={18} />}>
                  Profile
                </Button>
              </Link>
              <Link href="/api/auth/logout">
                <Button variant="ghost" size="sm" className="text-white hover:bg-slate-800" leftIcon={<LogOut size={18} />}>
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
            Admin Dashboard
          </h1>
          <p className="text-slate-600">
            Manage users, orders, partners, and platform settings.
          </p>
        </div>

        {/* Stats */}
        <div className="grid md:grid-cols-4 gap-6 mb-8">
          <div className="card p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-slate-500">Total Users</p>
                <p className="text-2xl font-bold text-slate-900">0</p>
              </div>
              <div className="w-10 h-10 bg-brand-100 rounded-lg flex items-center justify-center">
                <Users className="w-5 h-5 text-brand-600" />
              </div>
            </div>
            <Link href="/admin/users" className="text-sm text-brand-600 hover:underline mt-2 inline-block">
              Manage Users →
            </Link>
          </div>

          <div className="card p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-slate-500">Total Orders</p>
                <p className="text-2xl font-bold text-slate-900">0</p>
              </div>
              <div className="w-10 h-10 bg-green-100 rounded-lg flex items-center justify-center">
                <Package className="w-5 h-5 text-green-600" />
              </div>
            </div>
            <Link href="/admin/orders" className="text-sm text-brand-600 hover:underline mt-2 inline-block">
              View Orders →
            </Link>
          </div>

          <div className="card p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-slate-500">Active Partners</p>
                <p className="text-2xl font-bold text-slate-900">0</p>
              </div>
              <div className="w-10 h-10 bg-purple-100 rounded-lg flex items-center justify-center">
                <Users className="w-5 h-5 text-purple-600" />
              </div>
            </div>
            <Link href="/admin/partners" className="text-sm text-brand-600 hover:underline mt-2 inline-block">
              Manage Partners →
            </Link>
          </div>

          <div className="card p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-slate-500">Revenue (MTD)</p>
                <p className="text-2xl font-bold text-slate-900">₹0</p>
              </div>
              <div className="w-10 h-10 bg-yellow-100 rounded-lg flex items-center justify-center">
                <TrendingUp className="w-5 h-5 text-yellow-600" />
              </div>
            </div>
            <Link href="/admin/analytics" className="text-sm text-brand-600 hover:underline mt-2 inline-block">
              View Analytics →
            </Link>
          </div>
        </div>

        {/* Quick Links */}
        <div className="grid md:grid-cols-2 gap-6">
          <div className="card p-6">
            <h2 className="text-lg font-semibold text-slate-900 mb-4">User Management</h2>
            <div className="space-y-2">
              <Link href="/admin/users" className="block p-3 bg-slate-50 rounded-lg hover:bg-slate-100 transition-colors">
                <p className="font-medium text-slate-900">All Users</p>
                <p className="text-sm text-slate-500">View and manage all registered users</p>
              </Link>
              <Link href="/admin/users?status=PENDING_VERIFICATION" className="block p-3 bg-slate-50 rounded-lg hover:bg-slate-100 transition-colors">
                <p className="font-medium text-slate-900">Pending Verification</p>
                <p className="text-sm text-slate-500">Users awaiting email verification</p>
              </Link>
            </div>
          </div>

          <div className="card p-6">
            <h2 className="text-lg font-semibold text-slate-900 mb-4">Platform Settings</h2>
            <div className="space-y-2">
              <Link href="/admin/settings/commission" className="block p-3 bg-slate-50 rounded-lg hover:bg-slate-100 transition-colors">
                <p className="font-medium text-slate-900">Commission Settings</p>
                <p className="text-sm text-slate-500">Configure platform commission rates</p>
              </Link>
              <Link href="/admin/settings/discounts" className="block p-3 bg-slate-50 rounded-lg hover:bg-slate-100 transition-colors">
                <p className="font-medium text-slate-900">Discount Management</p>
                <p className="text-sm text-slate-500">Manage partner discounts and limits</p>
              </Link>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}
