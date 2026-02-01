'use client';

import React, { useState, useEffect } from 'react';
import Link from 'next/link';
import { 
  Package, 
  TrendingUp, 
  Clock, 
  Settings, 
  LogOut,
  Bell,
  Truck,
  CheckCircle,
  AlertTriangle,
  IndianRupee,
  ChevronRight,
  Tag,
  BarChart3,
  FileText,
  User
} from 'lucide-react';
import { Button } from '@/components/ui/Button';

/**
 * Partner Dashboard - FRD-005 FR-52
 * INTERNAL PORTAL - Partner Dashboard Home
 */

// Mock data - would come from API in production
const mockDashboard = {
  summary: {
    pendingOrders: 3,
    activeOrders: 8,
    readyToShip: 2,
    revenueThisMonth: 245000,
    activeDiscounts: 5
  },
  recentOrders: [
    { orderId: '1', orderNumber: 'BK-20260130-001', productName: 'Branded T-Shirt', quantity: 75, status: 'AWAITING_ACCEPTANCE', orderDate: '2026-01-30', expectedShipDate: '2026-02-06', partnerEarnings: 15180 },
    { orderId: '2', orderNumber: 'BK-20260129-003', productName: 'Ceramic Mug', quantity: 100, status: 'IN_PRODUCTION', orderDate: '2026-01-29', expectedShipDate: '2026-02-05', partnerEarnings: 8800 },
    { orderId: '3', orderNumber: 'BK-20260129-002', productName: 'Cotton Bag', quantity: 50, status: 'READY_TO_SHIP', orderDate: '2026-01-29', expectedShipDate: '2026-02-03', partnerEarnings: 6200 },
    { orderId: '4', orderNumber: 'BK-20260128-005', productName: 'Diary Set', quantity: 25, status: 'SHIPPED', orderDate: '2026-01-28', expectedShipDate: '2026-02-02', partnerEarnings: 4500 },
    { orderId: '5', orderNumber: 'BK-20260127-001', productName: 'Water Bottle', quantity: 200, status: 'DELIVERED', orderDate: '2026-01-27', expectedShipDate: '2026-02-01', partnerEarnings: 22000 },
  ],
  alerts: [
    { type: 'ORDER_PENDING', title: 'Orders Awaiting Acceptance', message: '3 order(s) require your action', actionUrl: '/partner/orders?status=AWAITING_ACCEPTANCE' },
    { type: 'READY_TO_SHIP', title: 'Ready to Ship', message: '2 order(s) are ready to be shipped', actionUrl: '/partner/orders?status=READY_TO_SHIP' },
  ],
  discountStatus: { active: 5, pending: 2, disabled: 1 },
  unreadNotifications: 4
};

const statusColors: Record<string, { bg: string; text: string }> = {
  'AWAITING_ACCEPTANCE': { bg: 'bg-amber-100', text: 'text-amber-700' },
  'ACCEPTED': { bg: 'bg-blue-100', text: 'text-blue-700' },
  'IN_PRODUCTION': { bg: 'bg-indigo-100', text: 'text-indigo-700' },
  'READY_TO_SHIP': { bg: 'bg-purple-100', text: 'text-purple-700' },
  'SHIPPED': { bg: 'bg-cyan-100', text: 'text-cyan-700' },
  'DELIVERED': { bg: 'bg-green-100', text: 'text-green-700' },
};

const formatStatus = (status: string) => {
  return status.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, l => l.toUpperCase());
};

const formatCurrency = (amount: number) => {
  return new Intl.NumberFormat('en-IN', { 
    style: 'currency', 
    currency: 'INR',
    maximumFractionDigits: 0 
  }).format(amount);
};

export default function PartnerDashboardPage() {
  const [dashboard, setDashboard] = useState(mockDashboard);
  const [isLoading, setIsLoading] = useState(false);

  // In production, fetch data from API
  // useEffect(() => {
  //   const fetchDashboard = async () => {
  //     const data = await getDashboard();
  //     setDashboard(data);
  //   };
  //   fetchDashboard();
  // }, []);

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-slate-50 to-indigo-50">
      {/* Header */}
      <header className="bg-white/80 backdrop-blur-md border-b border-slate-200 sticky top-0 z-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between h-16">
            <div className="flex items-center gap-4">
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

            <nav className="hidden md:flex items-center gap-1">
              <Link href="/partner/dashboard" className="px-4 py-2 text-sm font-medium text-indigo-600 bg-indigo-50 rounded-lg">
                Dashboard
              </Link>
              <Link href="/partner/orders" className="px-4 py-2 text-sm font-medium text-slate-600 hover:text-indigo-600 hover:bg-indigo-50 rounded-lg transition-colors">
                Orders
              </Link>
              <Link href="/partner/settlements" className="px-4 py-2 text-sm font-medium text-slate-600 hover:text-indigo-600 hover:bg-indigo-50 rounded-lg transition-colors">
                Settlements
              </Link>
              <Link href="/partner/discounts" className="px-4 py-2 text-sm font-medium text-slate-600 hover:text-indigo-600 hover:bg-indigo-50 rounded-lg transition-colors">
                Discounts
              </Link>
              <Link href="/partner/performance" className="px-4 py-2 text-sm font-medium text-slate-600 hover:text-indigo-600 hover:bg-indigo-50 rounded-lg transition-colors">
                Performance
              </Link>
            </nav>

            <div className="flex items-center gap-3">
              {/* Notifications Bell */}
              <button className="relative p-2 text-slate-500 hover:text-indigo-600 hover:bg-indigo-50 rounded-lg transition-colors">
                <Bell size={20} />
                {dashboard.unreadNotifications > 0 && (
                  <span className="absolute -top-1 -right-1 w-5 h-5 bg-red-500 text-white text-xs font-bold rounded-full flex items-center justify-center">
                    {dashboard.unreadNotifications}
                  </span>
                )}
              </button>
              
              <Link href="/partner/profile">
                <Button variant="ghost" size="sm" leftIcon={<User size={18} />}>
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
        {/* Welcome Section */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold font-display text-slate-900 mb-2">
            Welcome back, Partner! 👋
          </h1>
          <p className="text-slate-600">
            Here's what's happening with your orders today.
          </p>
        </div>

        {/* Alerts */}
        {dashboard.alerts.length > 0 && (
          <div className="mb-8 space-y-3">
            {dashboard.alerts.map((alert, idx) => (
              <Link key={idx} href={alert.actionUrl}>
                <div className="bg-gradient-to-r from-amber-50 to-orange-50 border border-amber-200 rounded-xl p-4 flex items-center justify-between hover:shadow-md transition-shadow cursor-pointer">
                  <div className="flex items-center gap-3">
                    <div className="w-10 h-10 bg-amber-100 rounded-lg flex items-center justify-center">
                      <AlertTriangle className="w-5 h-5 text-amber-600" />
                    </div>
                    <div>
                      <h3 className="font-semibold text-slate-900">{alert.title}</h3>
                      <p className="text-sm text-slate-600">{alert.message}</p>
                    </div>
                  </div>
                  <ChevronRight className="w-5 h-5 text-slate-400" />
                </div>
              </Link>
            ))}
          </div>
        )}

        {/* Stats Grid */}
        <div className="grid md:grid-cols-5 gap-4 mb-8">
          {/* Pending Orders */}
          <div className="bg-white rounded-2xl p-6 shadow-sm border border-slate-100 hover:shadow-lg transition-all duration-300 group">
            <div className="flex items-center justify-between mb-4">
              <div className="w-12 h-12 bg-gradient-to-br from-amber-400 to-orange-500 rounded-xl flex items-center justify-center shadow-lg shadow-amber-200 group-hover:scale-110 transition-transform">
                <Clock className="w-6 h-6 text-white" />
              </div>
              <span className="text-3xl font-bold text-slate-900">{dashboard.summary.pendingOrders}</span>
            </div>
            <p className="text-sm font-medium text-slate-500">Pending Orders</p>
            <p className="text-xs text-amber-600 mt-1">Requires action</p>
          </div>

          {/* Active Orders */}
          <div className="bg-white rounded-2xl p-6 shadow-sm border border-slate-100 hover:shadow-lg transition-all duration-300 group">
            <div className="flex items-center justify-between mb-4">
              <div className="w-12 h-12 bg-gradient-to-br from-blue-400 to-indigo-500 rounded-xl flex items-center justify-center shadow-lg shadow-blue-200 group-hover:scale-110 transition-transform">
                <Package className="w-6 h-6 text-white" />
              </div>
              <span className="text-3xl font-bold text-slate-900">{dashboard.summary.activeOrders}</span>
            </div>
            <p className="text-sm font-medium text-slate-500">In Production</p>
            <p className="text-xs text-blue-600 mt-1">Currently processing</p>
          </div>

          {/* Ready to Ship */}
          <div className="bg-white rounded-2xl p-6 shadow-sm border border-slate-100 hover:shadow-lg transition-all duration-300 group">
            <div className="flex items-center justify-between mb-4">
              <div className="w-12 h-12 bg-gradient-to-br from-purple-400 to-violet-500 rounded-xl flex items-center justify-center shadow-lg shadow-purple-200 group-hover:scale-110 transition-transform">
                <Truck className="w-6 h-6 text-white" />
              </div>
              <span className="text-3xl font-bold text-slate-900">{dashboard.summary.readyToShip}</span>
            </div>
            <p className="text-sm font-medium text-slate-500">Ready to Ship</p>
            <p className="text-xs text-purple-600 mt-1">Awaiting dispatch</p>
          </div>

          {/* Revenue This Month */}
          <div className="bg-white rounded-2xl p-6 shadow-sm border border-slate-100 hover:shadow-lg transition-all duration-300 group">
            <div className="flex items-center justify-between mb-4">
              <div className="w-12 h-12 bg-gradient-to-br from-emerald-400 to-green-500 rounded-xl flex items-center justify-center shadow-lg shadow-emerald-200 group-hover:scale-110 transition-transform">
                <IndianRupee className="w-6 h-6 text-white" />
              </div>
              <span className="text-2xl font-bold text-slate-900">{formatCurrency(dashboard.summary.revenueThisMonth)}</span>
            </div>
            <p className="text-sm font-medium text-slate-500">This Month</p>
            <p className="text-xs text-emerald-600 mt-1">Total earnings</p>
          </div>

          {/* Active Discounts */}
          <div className="bg-white rounded-2xl p-6 shadow-sm border border-slate-100 hover:shadow-lg transition-all duration-300 group">
            <div className="flex items-center justify-between mb-4">
              <div className="w-12 h-12 bg-gradient-to-br from-pink-400 to-rose-500 rounded-xl flex items-center justify-center shadow-lg shadow-pink-200 group-hover:scale-110 transition-transform">
                <Tag className="w-6 h-6 text-white" />
              </div>
              <span className="text-3xl font-bold text-slate-900">{dashboard.summary.activeDiscounts}</span>
            </div>
            <p className="text-sm font-medium text-slate-500">Active Discounts</p>
            <p className="text-xs text-pink-600 mt-1">{dashboard.discountStatus.pending} pending</p>
          </div>
        </div>

        {/* Main Content Grid */}
        <div className="grid lg:grid-cols-3 gap-8">
          {/* Recent Orders Table */}
          <div className="lg:col-span-2 bg-white rounded-2xl shadow-sm border border-slate-100 overflow-hidden">
            <div className="p-6 border-b border-slate-100 flex items-center justify-between">
              <div>
                <h2 className="text-lg font-bold text-slate-900">Recent Orders</h2>
                <p className="text-sm text-slate-500">Your latest assigned orders</p>
              </div>
              <Link href="/partner/orders">
                <Button variant="ghost" size="sm" rightIcon={<ChevronRight size={16} />}>
                  View All
                </Button>
              </Link>
            </div>
            
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead className="bg-slate-50">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">Order</th>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">Product</th>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">Status</th>
                    <th className="px-6 py-3 text-right text-xs font-semibold text-slate-500 uppercase tracking-wider">Earnings</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                  {dashboard.recentOrders.map((order) => (
                    <tr key={order.orderId} className="hover:bg-slate-50 transition-colors cursor-pointer">
                      <td className="px-6 py-4">
                        <Link href={`/partner/orders/${order.orderId}`} className="block">
                          <div className="font-medium text-indigo-600">{order.orderNumber}</div>
                          <div className="text-xs text-slate-500">{order.orderDate}</div>
                        </Link>
                      </td>
                      <td className="px-6 py-4">
                        <div className="font-medium text-slate-900">{order.productName}</div>
                        <div className="text-xs text-slate-500">Qty: {order.quantity}</div>
                      </td>
                      <td className="px-6 py-4">
                        <span className={`inline-flex px-2.5 py-1 rounded-full text-xs font-medium ${statusColors[order.status]?.bg || 'bg-slate-100'} ${statusColors[order.status]?.text || 'text-slate-700'}`}>
                          {formatStatus(order.status)}
                        </span>
                      </td>
                      <td className="px-6 py-4 text-right">
                        <div className="font-semibold text-slate-900">{formatCurrency(order.partnerEarnings)}</div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>

          {/* Quick Actions & Discount Status */}
          <div className="space-y-6">
            {/* Quick Actions */}
            <div className="bg-white rounded-2xl shadow-sm border border-slate-100 p-6">
              <h3 className="text-lg font-bold text-slate-900 mb-4">Quick Actions</h3>
              <div className="space-y-3">
                <Link href="/partner/orders" className="flex items-center gap-3 p-3 rounded-xl bg-slate-50 hover:bg-indigo-50 transition-colors group">
                  <div className="w-10 h-10 bg-indigo-100 rounded-lg flex items-center justify-center group-hover:bg-indigo-200 transition-colors">
                    <Package className="w-5 h-5 text-indigo-600" />
                  </div>
                  <div>
                    <p className="font-medium text-slate-900">View All Orders</p>
                    <p className="text-xs text-slate-500">Manage your assigned orders</p>
                  </div>
                </Link>
                
                <Link href="/partner/settlements" className="flex items-center gap-3 p-3 rounded-xl bg-slate-50 hover:bg-emerald-50 transition-colors group">
                  <div className="w-10 h-10 bg-emerald-100 rounded-lg flex items-center justify-center group-hover:bg-emerald-200 transition-colors">
                    <FileText className="w-5 h-5 text-emerald-600" />
                  </div>
                  <div>
                    <p className="font-medium text-slate-900">View Settlements</p>
                    <p className="text-xs text-slate-500">Track your earnings & payouts</p>
                  </div>
                </Link>
                
                <Link href="/partner/performance" className="flex items-center gap-3 p-3 rounded-xl bg-slate-50 hover:bg-purple-50 transition-colors group">
                  <div className="w-10 h-10 bg-purple-100 rounded-lg flex items-center justify-center group-hover:bg-purple-200 transition-colors">
                    <BarChart3 className="w-5 h-5 text-purple-600" />
                  </div>
                  <div>
                    <p className="font-medium text-slate-900">Performance</p>
                    <p className="text-xs text-slate-500">View your metrics</p>
                  </div>
                </Link>
                
                <Link href="/partner/profile" className="flex items-center gap-3 p-3 rounded-xl bg-slate-50 hover:bg-slate-100 transition-colors group">
                  <div className="w-10 h-10 bg-slate-200 rounded-lg flex items-center justify-center group-hover:bg-slate-300 transition-colors">
                    <Settings className="w-5 h-5 text-slate-600" />
                  </div>
                  <div>
                    <p className="font-medium text-slate-900">Update Profile</p>
                    <p className="text-xs text-slate-500">Business & bank details</p>
                  </div>
                </Link>
              </div>
            </div>

            {/* Discount Status */}
            <div className="bg-gradient-to-br from-indigo-500 to-purple-600 rounded-2xl shadow-lg p-6 text-white">
              <h3 className="text-lg font-bold mb-4 flex items-center gap-2">
                <Tag className="w-5 h-5" />
                Discount Status
              </h3>
              <div className="space-y-3">
                <div className="flex items-center justify-between">
                  <span className="text-indigo-100">Active</span>
                  <span className="font-bold text-xl">{dashboard.discountStatus.active}</span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-indigo-100">Pending Approval</span>
                  <span className="font-bold text-xl">{dashboard.discountStatus.pending}</span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-indigo-100">Disabled</span>
                  <span className="font-bold text-xl">{dashboard.discountStatus.disabled}</span>
                </div>
              </div>
              <Link href="/partner/discounts">
                <button className="mt-4 w-full py-2 px-4 bg-white/20 hover:bg-white/30 rounded-lg text-sm font-medium transition-colors">
                  Manage Discounts
                </button>
              </Link>
            </div>
          </div>
        </div>
      </main>

      {/* Footer */}
      <footer className="border-t border-slate-200 mt-12 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
          <div className="flex items-center justify-between">
            <p className="text-sm text-slate-500">
              © 2026 BrandKit. Partner Portal - Internal Use Only
            </p>
            <div className="flex items-center gap-4 text-sm text-slate-500">
              <a href="#" className="hover:text-indigo-600">Help Center</a>
              <a href="#" className="hover:text-indigo-600">Contact Support</a>
            </div>
          </div>
        </div>
      </footer>
    </div>
  );
}
