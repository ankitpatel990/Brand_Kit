'use client';

import React, { useState } from 'react';
import Link from 'next/link';
import { 
  Tag, 
  LogOut,
  Bell,
  Plus,
  Percent,
  Edit,
  Trash2,
  CheckCircle,
  Clock,
  XCircle,
  User,
  ArrowLeft,
  IndianRupee,
  AlertTriangle
} from 'lucide-react';
import { Button } from '@/components/ui/Button';

/**
 * Partner Discounts Page - FRD-005 FR-64b
 * Partner discount management
 */

const mockDiscounts = {
  limits: { minDiscount: 0, maxDiscount: 25, autoApprove: false },
  products: [
    { discountId: '1', productId: 'p1', productName: 'Branded T-Shirt', category: 'Apparel', basePrice: 260, discountPercentage: 10, discountedPrice: 234, status: 'APPROVED', earningsImpact: { originalEarnings: 228.80, discountedEarnings: 205.92, difference: 22.88 } },
    { discountId: '2', productId: 'p2', productName: 'Premium Ceramic Mug', category: 'Drinkware', basePrice: 180, discountPercentage: 15, discountedPrice: 153, status: 'APPROVED', earningsImpact: { originalEarnings: 158.40, discountedEarnings: 134.64, difference: 23.76 } },
    { discountId: '3', productId: 'p3', productName: 'Laptop Bag', category: 'Bags', basePrice: 850, discountPercentage: 12, discountedPrice: 748, status: 'PENDING', earningsImpact: { originalEarnings: 748, discountedEarnings: 658.24, difference: 89.76 } },
    { discountId: '4', productId: 'p4', productName: 'Water Bottle', category: 'Drinkware', basePrice: 220, discountPercentage: 8, discountedPrice: 202.40, status: 'APPROVED', earningsImpact: { originalEarnings: 193.60, discountedEarnings: 178.11, difference: 15.49 } },
    { discountId: '5', productId: 'p5', productName: 'Cotton Bag', category: 'Bags', basePrice: 150, discountPercentage: 20, discountedPrice: 120, status: 'DISABLED', earningsImpact: { originalEarnings: 132, discountedEarnings: 105.60, difference: 26.40 } },
  ]
};

const formatCurrency = (amount: number) => {
  return new Intl.NumberFormat('en-IN', { 
    style: 'currency', 
    currency: 'INR',
    minimumFractionDigits: 2
  }).format(amount);
};

const statusConfig: Record<string, { bg: string; text: string; icon: React.ReactNode; label: string }> = {
  'APPROVED': { bg: 'bg-green-100', text: 'text-green-700', icon: <CheckCircle className="w-4 h-4" />, label: 'Active' },
  'PENDING': { bg: 'bg-amber-100', text: 'text-amber-700', icon: <Clock className="w-4 h-4" />, label: 'Pending' },
  'DISABLED': { bg: 'bg-red-100', text: 'text-red-700', icon: <XCircle className="w-4 h-4" />, label: 'Disabled' },
  'REJECTED': { bg: 'bg-slate-100', text: 'text-slate-700', icon: <XCircle className="w-4 h-4" />, label: 'Rejected' },
};

export default function PartnerDiscountsPage() {
  const [data] = useState(mockDiscounts);
  const [showAddModal, setShowAddModal] = useState(false);
  const [newDiscount, setNewDiscount] = useState({ productId: '', discountPercentage: '' });

  const activeCount = data.products.filter(p => p.status === 'APPROVED').length;
  const pendingCount = data.products.filter(p => p.status === 'PENDING').length;
  const disabledCount = data.products.filter(p => p.status === 'DISABLED').length;

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-slate-50 to-pink-50">
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
            <h1 className="text-3xl font-bold font-display text-slate-900 mb-2">Discounts</h1>
            <p className="text-slate-600">Manage product discounts to attract more orders.</p>
          </div>
          <Button leftIcon={<Plus size={18} />} onClick={() => setShowAddModal(true)}>
            Add Discount
          </Button>
        </div>

        {/* Summary Cards */}
        <div className="grid md:grid-cols-4 gap-4 mb-8">
          <div className="bg-white rounded-2xl p-6 shadow-sm border border-slate-100">
            <div className="flex items-center gap-3 mb-2">
              <div className="w-10 h-10 bg-green-100 rounded-lg flex items-center justify-center">
                <CheckCircle className="w-5 h-5 text-green-600" />
              </div>
              <span className="text-3xl font-bold text-green-600">{activeCount}</span>
            </div>
            <p className="text-sm text-slate-500">Active Discounts</p>
          </div>

          <div className="bg-white rounded-2xl p-6 shadow-sm border border-slate-100">
            <div className="flex items-center gap-3 mb-2">
              <div className="w-10 h-10 bg-amber-100 rounded-lg flex items-center justify-center">
                <Clock className="w-5 h-5 text-amber-600" />
              </div>
              <span className="text-3xl font-bold text-amber-600">{pendingCount}</span>
            </div>
            <p className="text-sm text-slate-500">Pending Approval</p>
          </div>

          <div className="bg-white rounded-2xl p-6 shadow-sm border border-slate-100">
            <div className="flex items-center gap-3 mb-2">
              <div className="w-10 h-10 bg-red-100 rounded-lg flex items-center justify-center">
                <XCircle className="w-5 h-5 text-red-600" />
              </div>
              <span className="text-3xl font-bold text-red-600">{disabledCount}</span>
            </div>
            <p className="text-sm text-slate-500">Disabled</p>
          </div>

          <div className="bg-gradient-to-br from-pink-500 to-rose-500 rounded-2xl p-6 text-white shadow-lg">
            <div className="flex items-center gap-3 mb-2">
              <div className="w-10 h-10 bg-white/20 rounded-lg flex items-center justify-center">
                <Percent className="w-5 h-5" />
              </div>
            </div>
            <p className="text-pink-100 text-sm">Discount Limit</p>
            <p className="text-2xl font-bold">0% - {data.limits.maxDiscount}%</p>
          </div>
        </div>

        {/* Info Banner */}
        <div className="bg-indigo-50 border border-indigo-100 rounded-xl p-4 mb-6 flex items-start gap-3">
          <AlertTriangle className="w-5 h-5 text-indigo-600 mt-0.5" />
          <div>
            <p className="text-sm text-indigo-900 font-medium">How discounts work</p>
            <p className="text-sm text-indigo-700">
              Discounts you propose require admin approval. Your commission is calculated on the discounted price, 
              so offering discounts will reduce your per-order earnings but may increase order volume.
            </p>
          </div>
        </div>

        {/* Discounts Table */}
        <div className="bg-white rounded-2xl shadow-sm border border-slate-100 overflow-hidden">
          <div className="p-6 border-b border-slate-100">
            <h2 className="text-lg font-bold text-slate-900">Your Product Discounts</h2>
            <p className="text-sm text-slate-500">Manage discounts for products assigned to you</p>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-slate-50">
                <tr>
                  <th className="px-6 py-4 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">Product</th>
                  <th className="px-6 py-4 text-right text-xs font-semibold text-slate-500 uppercase tracking-wider">Base Price</th>
                  <th className="px-6 py-4 text-center text-xs font-semibold text-slate-500 uppercase tracking-wider">Discount</th>
                  <th className="px-6 py-4 text-right text-xs font-semibold text-slate-500 uppercase tracking-wider">Client Price</th>
                  <th className="px-6 py-4 text-right text-xs font-semibold text-slate-500 uppercase tracking-wider">Earnings Impact</th>
                  <th className="px-6 py-4 text-center text-xs font-semibold text-slate-500 uppercase tracking-wider">Status</th>
                  <th className="px-6 py-4 text-center text-xs font-semibold text-slate-500 uppercase tracking-wider">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {data.products.map((product) => (
                  <tr key={product.discountId} className="hover:bg-slate-50 transition-colors">
                    <td className="px-6 py-4">
                      <div className="font-medium text-slate-900">{product.productName}</div>
                      <div className="text-xs text-slate-500">{product.category}</div>
                    </td>
                    <td className="px-6 py-4 text-right">
                      <div className="font-medium text-slate-900">{formatCurrency(product.basePrice)}</div>
                    </td>
                    <td className="px-6 py-4 text-center">
                      <span className="inline-flex items-center gap-1 px-3 py-1 bg-pink-100 text-pink-700 rounded-full text-sm font-semibold">
                        <Percent className="w-3 h-3" />
                        {product.discountPercentage}%
                      </span>
                    </td>
                    <td className="px-6 py-4 text-right">
                      <div className="font-medium text-green-600">{formatCurrency(product.discountedPrice)}</div>
                    </td>
                    <td className="px-6 py-4 text-right">
                      <div className="text-xs text-slate-500">
                        <span className="text-slate-400 line-through">{formatCurrency(product.earningsImpact.originalEarnings)}</span>
                        <span className="ml-2 text-slate-900 font-medium">{formatCurrency(product.earningsImpact.discountedEarnings)}</span>
                      </div>
                      <div className="text-xs text-red-500">-{formatCurrency(product.earningsImpact.difference)}</div>
                    </td>
                    <td className="px-6 py-4 text-center">
                      <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-medium ${statusConfig[product.status]?.bg} ${statusConfig[product.status]?.text}`}>
                        {statusConfig[product.status]?.icon}
                        {statusConfig[product.status]?.label}
                      </span>
                    </td>
                    <td className="px-6 py-4">
                      <div className="flex items-center justify-center gap-2">
                        <button className="p-2 bg-slate-100 text-slate-600 rounded-lg hover:bg-slate-200 transition-colors" title="Edit">
                          <Edit size={16} />
                        </button>
                        <button className="p-2 bg-red-100 text-red-600 rounded-lg hover:bg-red-200 transition-colors" title="Remove">
                          <Trash2 size={16} />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </main>

      {/* Add Discount Modal */}
      {showAddModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <div className="bg-white rounded-2xl p-6 max-w-md w-full mx-4 shadow-2xl">
            <h3 className="text-xl font-bold text-slate-900 mb-4">Add New Discount</h3>
            
            <div className="space-y-4 mb-6">
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-2">Select Product</label>
                <select
                  value={newDiscount.productId}
                  onChange={(e) => setNewDiscount({ ...newDiscount, productId: e.target.value })}
                  className="w-full p-3 border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-indigo-500"
                >
                  <option value="">Choose a product...</option>
                  <option value="p6">Diary Set</option>
                  <option value="p7">Metal Pen</option>
                  <option value="p8">USB Drive</option>
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-slate-700 mb-2">Discount Percentage</label>
                <div className="relative">
                  <input
                    type="number"
                    min="0"
                    max={data.limits.maxDiscount}
                    value={newDiscount.discountPercentage}
                    onChange={(e) => setNewDiscount({ ...newDiscount, discountPercentage: e.target.value })}
                    className="w-full p-3 pr-10 border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-indigo-500"
                    placeholder={`0 - ${data.limits.maxDiscount}`}
                  />
                  <Percent className="absolute right-3 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-400" />
                </div>
                <p className="text-xs text-slate-500 mt-1">Maximum allowed: {data.limits.maxDiscount}%</p>
              </div>
            </div>

            <div className="flex gap-3">
              <button
                onClick={() => setShowAddModal(false)}
                className="flex-1 py-2.5 px-4 border border-slate-200 rounded-xl text-slate-600 font-medium hover:bg-slate-50 transition-colors"
              >
                Cancel
              </button>
              <button className="flex-1 py-2.5 px-4 bg-indigo-600 text-white rounded-xl font-medium hover:bg-indigo-700 transition-colors">
                Submit for Approval
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
