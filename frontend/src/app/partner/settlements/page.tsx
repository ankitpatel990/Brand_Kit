'use client';

import React, { useState } from 'react';
import Link from 'next/link';
import { 
  IndianRupee, 
  Clock, 
  LogOut,
  Bell,
  FileText,
  Download,
  ChevronRight,
  User,
  ArrowLeft,
  Calendar,
  CheckCircle
} from 'lucide-react';
import { Button } from '@/components/ui/Button';

/**
 * Partner Settlements Page - FRD-005 FR-61
 * Commission and settlement dashboard
 */

const mockSettlementData = {
  summary: {
    totalEarningsAllTime: 580000,
    pendingSettlement: 45000,
    lastSettlement: { amount: 220000, date: '2026-02-05' },
    nextSettlementDate: '2026-03-05'
  },
  settlements: [
    { settlementId: '1', settlementNumber: 'SET-2026-02-001', period: 'January 2026', orderCount: 15, totalAmount: 220000, status: 'COMPLETED', date: '2026-02-05', statementUrl: '#' },
    { settlementId: '2', settlementNumber: 'SET-2026-01-001', period: 'December 2025', orderCount: 12, totalAmount: 185000, status: 'COMPLETED', date: '2026-01-05', statementUrl: '#' },
    { settlementId: '3', settlementNumber: 'SET-2025-12-001', period: 'November 2025', orderCount: 10, totalAmount: 145000, status: 'COMPLETED', date: '2025-12-05', statementUrl: '#' },
  ]
};

const formatCurrency = (amount: number) => {
  return new Intl.NumberFormat('en-IN', { 
    style: 'currency', 
    currency: 'INR',
    maximumFractionDigits: 0 
  }).format(amount);
};

const statusColors: Record<string, { bg: string; text: string; icon: React.ReactNode }> = {
  'PENDING': { bg: 'bg-amber-100', text: 'text-amber-700', icon: <Clock className="w-4 h-4" /> },
  'PROCESSING': { bg: 'bg-blue-100', text: 'text-blue-700', icon: <Clock className="w-4 h-4 animate-spin" /> },
  'COMPLETED': { bg: 'bg-green-100', text: 'text-green-700', icon: <CheckCircle className="w-4 h-4" /> },
  'FAILED': { bg: 'bg-red-100', text: 'text-red-700', icon: <Clock className="w-4 h-4" /> },
};

export default function PartnerSettlementsPage() {
  const [data] = useState(mockSettlementData);

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-slate-50 to-emerald-50">
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
        <div className="mb-8">
          <h1 className="text-3xl font-bold font-display text-slate-900 mb-2">Settlements</h1>
          <p className="text-slate-600">Track your commission earnings and payment history.</p>
        </div>

        {/* Summary Cards */}
        <div className="grid md:grid-cols-4 gap-4 mb-8">
          {/* Total Earnings */}
          <div className="bg-gradient-to-br from-emerald-500 to-green-600 rounded-2xl p-6 text-white shadow-lg shadow-emerald-200">
            <div className="flex items-center gap-3 mb-4">
              <div className="w-12 h-12 bg-white/20 rounded-xl flex items-center justify-center">
                <IndianRupee className="w-6 h-6" />
              </div>
            </div>
            <p className="text-emerald-100 text-sm mb-1">Total Earnings (All Time)</p>
            <p className="text-3xl font-bold">{formatCurrency(data.summary.totalEarningsAllTime)}</p>
          </div>

          {/* Pending Settlement */}
          <div className="bg-white rounded-2xl p-6 shadow-sm border border-slate-100">
            <div className="flex items-center gap-3 mb-4">
              <div className="w-12 h-12 bg-amber-100 rounded-xl flex items-center justify-center">
                <Clock className="w-6 h-6 text-amber-600" />
              </div>
            </div>
            <p className="text-slate-500 text-sm mb-1">Pending Settlement</p>
            <p className="text-2xl font-bold text-slate-900">{formatCurrency(data.summary.pendingSettlement)}</p>
            <p className="text-xs text-amber-600 mt-1">Current period earnings</p>
          </div>

          {/* Last Settlement */}
          <div className="bg-white rounded-2xl p-6 shadow-sm border border-slate-100">
            <div className="flex items-center gap-3 mb-4">
              <div className="w-12 h-12 bg-green-100 rounded-xl flex items-center justify-center">
                <CheckCircle className="w-6 h-6 text-green-600" />
              </div>
            </div>
            <p className="text-slate-500 text-sm mb-1">Last Settlement</p>
            <p className="text-2xl font-bold text-slate-900">{formatCurrency(data.summary.lastSettlement.amount)}</p>
            <p className="text-xs text-slate-500 mt-1">{data.summary.lastSettlement.date}</p>
          </div>

          {/* Next Settlement */}
          <div className="bg-white rounded-2xl p-6 shadow-sm border border-slate-100">
            <div className="flex items-center gap-3 mb-4">
              <div className="w-12 h-12 bg-indigo-100 rounded-xl flex items-center justify-center">
                <Calendar className="w-6 h-6 text-indigo-600" />
              </div>
            </div>
            <p className="text-slate-500 text-sm mb-1">Next Settlement</p>
            <p className="text-xl font-bold text-slate-900">{data.summary.nextSettlementDate}</p>
            <p className="text-xs text-indigo-600 mt-1">Scheduled payout date</p>
          </div>
        </div>

        {/* Settlements List */}
        <div className="bg-white rounded-2xl shadow-sm border border-slate-100 overflow-hidden">
          <div className="p-6 border-b border-slate-100">
            <h2 className="text-lg font-bold text-slate-900">Settlement History</h2>
            <p className="text-sm text-slate-500">View and download past settlement statements</p>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-slate-50">
                <tr>
                  <th className="px-6 py-4 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">Settlement ID</th>
                  <th className="px-6 py-4 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">Period</th>
                  <th className="px-6 py-4 text-center text-xs font-semibold text-slate-500 uppercase tracking-wider">Orders</th>
                  <th className="px-6 py-4 text-right text-xs font-semibold text-slate-500 uppercase tracking-wider">Amount</th>
                  <th className="px-6 py-4 text-center text-xs font-semibold text-slate-500 uppercase tracking-wider">Status</th>
                  <th className="px-6 py-4 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">Date</th>
                  <th className="px-6 py-4 text-center text-xs font-semibold text-slate-500 uppercase tracking-wider">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {data.settlements.map((settlement) => (
                  <tr key={settlement.settlementId} className="hover:bg-slate-50 transition-colors">
                    <td className="px-6 py-4">
                      <div className="font-medium text-indigo-600">{settlement.settlementNumber}</div>
                    </td>
                    <td className="px-6 py-4">
                      <div className="font-medium text-slate-900">{settlement.period}</div>
                    </td>
                    <td className="px-6 py-4 text-center">
                      <div className="font-medium text-slate-900">{settlement.orderCount}</div>
                    </td>
                    <td className="px-6 py-4 text-right">
                      <div className="font-bold text-slate-900">{formatCurrency(settlement.totalAmount)}</div>
                    </td>
                    <td className="px-6 py-4 text-center">
                      <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-medium ${statusColors[settlement.status]?.bg || 'bg-slate-100'} ${statusColors[settlement.status]?.text || 'text-slate-700'}`}>
                        {statusColors[settlement.status]?.icon}
                        {settlement.status}
                      </span>
                    </td>
                    <td className="px-6 py-4">
                      <div className="text-sm text-slate-700">{settlement.date}</div>
                    </td>
                    <td className="px-6 py-4">
                      <div className="flex items-center justify-center gap-2">
                        <button className="p-2 bg-slate-100 text-slate-600 rounded-lg hover:bg-slate-200 transition-colors" title="Download Statement">
                          <Download size={18} />
                        </button>
                        <Link href={`/partner/settlements/${settlement.settlementId}`} className="p-2 bg-indigo-100 text-indigo-600 rounded-lg hover:bg-indigo-200 transition-colors" title="View Details">
                          <ChevronRight size={18} />
                        </Link>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        {/* Commission Info */}
        <div className="mt-8 bg-gradient-to-br from-indigo-50 to-purple-50 rounded-2xl p-6 border border-indigo-100">
          <h3 className="text-lg font-bold text-slate-900 mb-4">Commission Information</h3>
          <div className="grid md:grid-cols-3 gap-6">
            <div>
              <p className="text-sm text-slate-500 mb-1">Your Commission Rate</p>
              <p className="text-2xl font-bold text-indigo-600">12%</p>
            </div>
            <div>
              <p className="text-sm text-slate-500 mb-1">Settlement Schedule</p>
              <p className="text-lg font-semibold text-slate-900">Monthly (5th of each month)</p>
            </div>
            <div>
              <p className="text-sm text-slate-500 mb-1">Minimum Payout</p>
              <p className="text-lg font-semibold text-slate-900">₹1,000</p>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}
