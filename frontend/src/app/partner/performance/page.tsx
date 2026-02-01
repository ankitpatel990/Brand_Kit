'use client';

import React, { useState } from 'react';
import Link from 'next/link';
import { 
  TrendingUp, 
  Clock, 
  LogOut,
  Bell,
  Target,
  Zap,
  Award,
  Star,
  User,
  ArrowLeft,
  AlertTriangle,
  CheckCircle,
  IndianRupee,
  Package
} from 'lucide-react';
import { Button } from '@/components/ui/Button';

/**
 * Partner Performance Page - FRD-005 FR-63
 * Performance metrics and monitoring
 */

const mockPerformance = {
  period: 'All Time',
  metrics: {
    fulfillmentRate: 93.5,
    averageLeadTime: 6.2,
    deliverySuccessRate: 98.5,
    averageRating: 4.6,
    totalOrdersFulfilled: 156,
    totalRevenue: 580000,
    totalOrdersAssigned: 168,
    totalOrdersAccepted: 157,
    totalOrdersRejected: 11
  },
  benchmark: {
    platformAverageFulfillment: 95,
    platformAverageLeadTime: 5.5
  },
  alerts: [
    { type: 'INFO', message: 'Your fulfillment rate (93.5%) is slightly below platform average (95%). Consider accepting more orders.' }
  ]
};

const formatCurrency = (amount: number) => {
  return new Intl.NumberFormat('en-IN', { 
    style: 'currency', 
    currency: 'INR',
    maximumFractionDigits: 0 
  }).format(amount);
};

export default function PartnerPerformancePage() {
  const [data] = useState(mockPerformance);
  const [selectedPeriod, setSelectedPeriod] = useState('all_time');

  const getPerformanceColor = (value: number, threshold: number) => {
    if (value >= threshold) return 'text-green-600';
    if (value >= threshold - 5) return 'text-amber-600';
    return 'text-red-600';
  };

  const getProgressColor = (value: number, threshold: number) => {
    if (value >= threshold) return 'bg-green-500';
    if (value >= threshold - 5) return 'bg-amber-500';
    return 'bg-red-500';
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-slate-50 to-purple-50">
      {/* Header */}
      <header className="bg-white/80 backdrop-blur-md border-b border-slate-200 sticky top-0 z-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between h-16">
            <div className="flex items-center gap-4">
              <Link href="/partner/dashboard" className="p-2 hover:bg-slate-100 rounded-lg transition-colors">
                <ArrowLeft size={20} className="text-slate-600" />
              </Link>
              <div className="flex items-center gap-2">
                <div className="w-9 h-9 bg-gradient-to-br from-indigo-500 to-purple-600 rounded-xl flex items-center justify-center shadow-lg shadow-indigo-200">
                  <span className="text-white font-bold text-lg font-display">B</span>
                </div>
                <span className="text-xl font-bold font-display text-slate-900">
                  Brand<span className="text-indigo-600">Kit</span>
                </span>
              </div>
              <span className="px-3 py-1 bg-gradient-to-r from-indigo-500 to-purple-500 text-white text-xs font-semibold rounded-full shadow-sm">
                Partner Portal
              </span>
            </div>

            <div className="flex items-center gap-3">
              <button className="relative p-2 text-slate-500 hover:text-indigo-600 hover:bg-indigo-50 rounded-lg transition-colors">
                <Bell size={20} />
              </button>
              <Link href="/partner/profile">
                <Button variant="ghost" size="sm" leftIcon={<User size={18} />}>Profile</Button>
              </Link>
              <Link href="/api/auth/logout">
                <Button variant="ghost" size="sm" leftIcon={<LogOut size={18} />}>Logout</Button>
              </Link>
            </div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Page Header */}
        <div className="flex items-center justify-between mb-8">
          <div>
            <h1 className="text-3xl font-bold font-display text-slate-900 mb-2">Performance</h1>
            <p className="text-slate-600">Track your fulfillment metrics and platform standing.</p>
          </div>
          <select
            value={selectedPeriod}
            onChange={(e) => setSelectedPeriod(e.target.value)}
            className="px-4 py-2 border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-indigo-500"
          >
            <option value="30_days">Last 30 Days</option>
            <option value="90_days">Last 3 Months</option>
            <option value="1_year">Last Year</option>
            <option value="all_time">All Time</option>
          </select>
        </div>

        {/* Alerts */}
        {data.alerts.length > 0 && (
          <div className="mb-8 space-y-3">
            {data.alerts.map((alert, idx) => (
              <div key={idx} className={`rounded-xl p-4 flex items-center gap-3 ${
                alert.type === 'WARNING' ? 'bg-amber-50 border border-amber-200' : 'bg-blue-50 border border-blue-200'
              }`}>
                <AlertTriangle className={`w-5 h-5 ${alert.type === 'WARNING' ? 'text-amber-600' : 'text-blue-600'}`} />
                <p className="text-sm text-slate-700">{alert.message}</p>
              </div>
            ))}
          </div>
        )}

        {/* Key Metrics Grid */}
        <div className="grid md:grid-cols-4 gap-4 mb-8">
          {/* Fulfillment Rate */}
          <div className="bg-white rounded-2xl p-6 shadow-sm border border-slate-100">
            <div className="flex items-center gap-3 mb-4">
              <div className="w-12 h-12 bg-gradient-to-br from-green-400 to-emerald-500 rounded-xl flex items-center justify-center shadow-lg shadow-green-200">
                <Target className="w-6 h-6 text-white" />
              </div>
            </div>
            <p className="text-slate-500 text-sm mb-1">Fulfillment Rate</p>
            <p className={`text-3xl font-bold ${getPerformanceColor(data.metrics.fulfillmentRate, data.benchmark.platformAverageFulfillment)}`}>
              {data.metrics.fulfillmentRate}%
            </p>
            <div className="mt-3">
              <div className="flex items-center justify-between text-xs text-slate-500 mb-1">
                <span>Platform Avg: {data.benchmark.platformAverageFulfillment}%</span>
              </div>
              <div className="w-full h-2 bg-slate-100 rounded-full overflow-hidden">
                <div
                  className={`h-full ${getProgressColor(data.metrics.fulfillmentRate, data.benchmark.platformAverageFulfillment)} rounded-full transition-all`}
                  style={{ width: `${Math.min(data.metrics.fulfillmentRate, 100)}%` }}
                />
              </div>
            </div>
          </div>

          {/* Average Lead Time */}
          <div className="bg-white rounded-2xl p-6 shadow-sm border border-slate-100">
            <div className="flex items-center gap-3 mb-4">
              <div className="w-12 h-12 bg-gradient-to-br from-blue-400 to-indigo-500 rounded-xl flex items-center justify-center shadow-lg shadow-blue-200">
                <Zap className="w-6 h-6 text-white" />
              </div>
            </div>
            <p className="text-slate-500 text-sm mb-1">Average Lead Time</p>
            <p className="text-3xl font-bold text-slate-900">{data.metrics.averageLeadTime} <span className="text-lg font-normal text-slate-500">days</span></p>
            <div className="mt-3">
              <div className="flex items-center justify-between text-xs text-slate-500 mb-1">
                <span>Platform Avg: {data.benchmark.platformAverageLeadTime} days</span>
              </div>
              <div className="w-full h-2 bg-slate-100 rounded-full overflow-hidden">
                <div
                  className="h-full bg-indigo-500 rounded-full transition-all"
                  style={{ width: `${Math.min((data.benchmark.platformAverageLeadTime / data.metrics.averageLeadTime) * 100, 100)}%` }}
                />
              </div>
            </div>
          </div>

          {/* Delivery Success Rate */}
          <div className="bg-white rounded-2xl p-6 shadow-sm border border-slate-100">
            <div className="flex items-center gap-3 mb-4">
              <div className="w-12 h-12 bg-gradient-to-br from-purple-400 to-violet-500 rounded-xl flex items-center justify-center shadow-lg shadow-purple-200">
                <CheckCircle className="w-6 h-6 text-white" />
              </div>
            </div>
            <p className="text-slate-500 text-sm mb-1">Delivery Success Rate</p>
            <p className="text-3xl font-bold text-green-600">{data.metrics.deliverySuccessRate}%</p>
            <p className="text-xs text-green-600 mt-2 flex items-center gap-1">
              <TrendingUp className="w-3 h-3" />
              Excellent performance!
            </p>
          </div>

          {/* Average Rating */}
          <div className="bg-white rounded-2xl p-6 shadow-sm border border-slate-100">
            <div className="flex items-center gap-3 mb-4">
              <div className="w-12 h-12 bg-gradient-to-br from-amber-400 to-orange-500 rounded-xl flex items-center justify-center shadow-lg shadow-amber-200">
                <Star className="w-6 h-6 text-white" />
              </div>
            </div>
            <p className="text-slate-500 text-sm mb-1">Average Rating</p>
            <div className="flex items-center gap-2">
              <p className="text-3xl font-bold text-slate-900">{data.metrics.averageRating}</p>
              <div className="flex text-amber-400">
                {[1, 2, 3, 4, 5].map((star) => (
                  <Star
                    key={star}
                    size={16}
                    className={star <= Math.round(data.metrics.averageRating) ? 'fill-current' : 'text-slate-200'}
                  />
                ))}
              </div>
            </div>
            <p className="text-xs text-slate-500 mt-2">Based on client feedback</p>
          </div>
        </div>

        {/* Order Statistics */}
        <div className="grid md:grid-cols-2 gap-6 mb-8">
          {/* Order Breakdown */}
          <div className="bg-white rounded-2xl p-6 shadow-sm border border-slate-100">
            <h3 className="text-lg font-bold text-slate-900 mb-6">Order Statistics</h3>
            <div className="space-y-4">
              <div className="flex items-center justify-between p-4 bg-slate-50 rounded-xl">
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 bg-slate-200 rounded-lg flex items-center justify-center">
                    <Package className="w-5 h-5 text-slate-600" />
                  </div>
                  <div>
                    <p className="font-medium text-slate-900">Total Assigned</p>
                    <p className="text-xs text-slate-500">Orders routed to you</p>
                  </div>
                </div>
                <p className="text-2xl font-bold text-slate-900">{data.metrics.totalOrdersAssigned}</p>
              </div>

              <div className="flex items-center justify-between p-4 bg-green-50 rounded-xl">
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 bg-green-200 rounded-lg flex items-center justify-center">
                    <CheckCircle className="w-5 h-5 text-green-600" />
                  </div>
                  <div>
                    <p className="font-medium text-slate-900">Accepted</p>
                    <p className="text-xs text-slate-500">Orders you accepted</p>
                  </div>
                </div>
                <p className="text-2xl font-bold text-green-600">{data.metrics.totalOrdersAccepted}</p>
              </div>

              <div className="flex items-center justify-between p-4 bg-amber-50 rounded-xl">
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 bg-amber-200 rounded-lg flex items-center justify-center">
                    <AlertTriangle className="w-5 h-5 text-amber-600" />
                  </div>
                  <div>
                    <p className="font-medium text-slate-900">Rejected</p>
                    <p className="text-xs text-slate-500">Orders you declined</p>
                  </div>
                </div>
                <p className="text-2xl font-bold text-amber-600">{data.metrics.totalOrdersRejected}</p>
              </div>

              <div className="flex items-center justify-between p-4 bg-indigo-50 rounded-xl">
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 bg-indigo-200 rounded-lg flex items-center justify-center">
                    <Award className="w-5 h-5 text-indigo-600" />
                  </div>
                  <div>
                    <p className="font-medium text-slate-900">Fulfilled</p>
                    <p className="text-xs text-slate-500">Successfully completed</p>
                  </div>
                </div>
                <p className="text-2xl font-bold text-indigo-600">{data.metrics.totalOrdersFulfilled}</p>
              </div>
            </div>
          </div>

          {/* Revenue Summary */}
          <div className="bg-gradient-to-br from-indigo-500 to-purple-600 rounded-2xl p-6 text-white shadow-lg">
            <h3 className="text-lg font-bold mb-6">Revenue Summary</h3>
            <div className="flex items-center gap-4 mb-8">
              <div className="w-16 h-16 bg-white/20 rounded-2xl flex items-center justify-center">
                <IndianRupee className="w-8 h-8" />
              </div>
              <div>
                <p className="text-indigo-100 text-sm">Total Revenue Generated</p>
                <p className="text-4xl font-bold">{formatCurrency(data.metrics.totalRevenue)}</p>
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div className="bg-white/10 rounded-xl p-4">
                <p className="text-indigo-100 text-sm mb-1">Avg. per Order</p>
                <p className="text-xl font-bold">
                  {formatCurrency(data.metrics.totalRevenue / data.metrics.totalOrdersFulfilled)}
                </p>
              </div>
              <div className="bg-white/10 rounded-xl p-4">
                <p className="text-indigo-100 text-sm mb-1">This Month</p>
                <p className="text-xl font-bold">₹45,000</p>
              </div>
            </div>

            <div className="mt-6 pt-6 border-t border-white/20">
              <p className="text-indigo-100 text-sm flex items-center gap-2">
                <TrendingUp className="w-4 h-4" />
                You're in the top 15% of partners!
              </p>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}
