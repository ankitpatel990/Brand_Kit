'use client';

import React, { useState } from 'react';
import Link from 'next/link';
import { 
  Package, 
  Clock, 
  Settings, 
  LogOut,
  Bell,
  Search,
  Filter,
  ChevronRight,
  CheckCircle,
  XCircle,
  Truck,
  User,
  ArrowLeft
} from 'lucide-react';
import { Button } from '@/components/ui/Button';

/**
 * Partner Orders Page - FRD-005 FR-54
 * Order list view for partners
 */

const mockOrders = [
  { orderId: '1', orderNumber: 'BK-20260130-001', clientName: 'Rajesh K.', productName: 'Branded T-Shirt', quantity: 75, status: 'AWAITING_ACCEPTANCE', partnerStatus: 'AWAITING_ACCEPTANCE', orderDate: '2026-01-30', expectedShipDate: '2026-02-06', partnerEarnings: 15180, actions: ['ACCEPT', 'REJECT'] },
  { orderId: '2', orderNumber: 'BK-20260130-002', clientName: 'Priya S.', productName: 'Premium Pen Set', quantity: 200, status: 'AWAITING_ACCEPTANCE', partnerStatus: 'AWAITING_ACCEPTANCE', orderDate: '2026-01-30', expectedShipDate: '2026-02-05', partnerEarnings: 4400, actions: ['ACCEPT', 'REJECT'] },
  { orderId: '3', orderNumber: 'BK-20260130-003', clientName: 'Amit P.', productName: 'Water Bottle', quantity: 150, status: 'AWAITING_ACCEPTANCE', partnerStatus: 'AWAITING_ACCEPTANCE', orderDate: '2026-01-30', expectedShipDate: '2026-02-06', partnerEarnings: 18750, actions: ['ACCEPT', 'REJECT'] },
  { orderId: '4', orderNumber: 'BK-20260129-003', clientName: 'Vikram M.', productName: 'Ceramic Mug', quantity: 100, status: 'IN_PRODUCTION', partnerStatus: 'IN_PRODUCTION', orderDate: '2026-01-29', expectedShipDate: '2026-02-05', partnerEarnings: 8800, actions: ['UPDATE_STATUS', 'UPLOAD_PROOF'] },
  { orderId: '5', orderNumber: 'BK-20260129-004', clientName: 'Sneha R.', productName: 'Laptop Bag', quantity: 50, status: 'IN_PRODUCTION', partnerStatus: 'IN_PRODUCTION', orderDate: '2026-01-29', expectedShipDate: '2026-02-04', partnerEarnings: 22000, actions: ['UPDATE_STATUS', 'UPLOAD_PROOF'] },
  { orderId: '6', orderNumber: 'BK-20260129-002', clientName: 'Rahul D.', productName: 'Cotton Bag', quantity: 50, status: 'READY_TO_SHIP', partnerStatus: 'READY_TO_SHIP', orderDate: '2026-01-29', expectedShipDate: '2026-02-03', partnerEarnings: 6200, actions: ['MARK_SHIPPED'] },
  { orderId: '7', orderNumber: 'BK-20260128-005', clientName: 'Meera G.', productName: 'Diary Set', quantity: 25, status: 'SHIPPED', partnerStatus: 'SHIPPED', orderDate: '2026-01-28', expectedShipDate: '2026-02-02', partnerEarnings: 4500, actions: ['VIEW_TRACKING'] },
  { orderId: '8', orderNumber: 'BK-20260127-001', clientName: 'Arun T.', productName: 'Water Bottle', quantity: 200, status: 'DELIVERED', partnerStatus: 'DELIVERED', orderDate: '2026-01-27', expectedShipDate: '2026-02-01', partnerEarnings: 22000, actions: [] },
];

const statusTabs = [
  { key: 'all', label: 'All Orders', count: 8 },
  { key: 'AWAITING_ACCEPTANCE', label: 'Pending', count: 3 },
  { key: 'IN_PRODUCTION', label: 'In Production', count: 2 },
  { key: 'READY_TO_SHIP', label: 'Ready to Ship', count: 1 },
  { key: 'SHIPPED', label: 'Shipped', count: 1 },
  { key: 'DELIVERED', label: 'Delivered', count: 1 },
];

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

export default function PartnerOrdersPage() {
  const [activeTab, setActiveTab] = useState('all');
  const [searchQuery, setSearchQuery] = useState('');
  const [showAcceptModal, setShowAcceptModal] = useState<string | null>(null);
  const [showRejectModal, setShowRejectModal] = useState<string | null>(null);

  const filteredOrders = mockOrders.filter(order => {
    const matchesTab = activeTab === 'all' || order.partnerStatus === activeTab;
    const matchesSearch = order.orderNumber.toLowerCase().includes(searchQuery.toLowerCase()) ||
                         order.productName.toLowerCase().includes(searchQuery.toLowerCase());
    return matchesTab && matchesSearch;
  });

  const handleAccept = (orderId: string) => {
    // API call to accept order
    console.log('Accepting order:', orderId);
    setShowAcceptModal(null);
  };

  const handleReject = (orderId: string, reason: string) => {
    // API call to reject order
    console.log('Rejecting order:', orderId, 'Reason:', reason);
    setShowRejectModal(null);
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-slate-50 to-indigo-50">
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
                <span className="absolute -top-1 -right-1 w-5 h-5 bg-red-500 text-white text-xs font-bold rounded-full flex items-center justify-center">4</span>
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
          <h1 className="text-3xl font-bold font-display text-slate-900 mb-2">Orders</h1>
          <p className="text-slate-600">Manage your assigned orders and track fulfillment progress.</p>
        </div>

        {/* Filters & Search */}
        <div className="bg-white rounded-2xl shadow-sm border border-slate-100 p-4 mb-6">
          <div className="flex flex-col md:flex-row gap-4">
            {/* Search */}
            <div className="flex-1 relative">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-400" />
              <input
                type="text"
                placeholder="Search by order ID or product..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="w-full pl-10 pr-4 py-2.5 border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
              />
            </div>
          </div>

          {/* Status Tabs */}
          <div className="flex flex-wrap gap-2 mt-4">
            {statusTabs.map((tab) => (
              <button
                key={tab.key}
                onClick={() => setActiveTab(tab.key)}
                className={`px-4 py-2 rounded-lg text-sm font-medium transition-all ${
                  activeTab === tab.key
                    ? 'bg-indigo-600 text-white shadow-lg shadow-indigo-200'
                    : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                }`}
              >
                {tab.label} ({tab.count})
              </button>
            ))}
          </div>
        </div>

        {/* Orders Table */}
        <div className="bg-white rounded-2xl shadow-sm border border-slate-100 overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-slate-50">
                <tr>
                  <th className="px-6 py-4 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">Order</th>
                  <th className="px-6 py-4 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">Client</th>
                  <th className="px-6 py-4 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">Product</th>
                  <th className="px-6 py-4 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">Expected Ship</th>
                  <th className="px-6 py-4 text-left text-xs font-semibold text-slate-500 uppercase tracking-wider">Status</th>
                  <th className="px-6 py-4 text-right text-xs font-semibold text-slate-500 uppercase tracking-wider">Earnings</th>
                  <th className="px-6 py-4 text-center text-xs font-semibold text-slate-500 uppercase tracking-wider">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {filteredOrders.map((order) => (
                  <tr key={order.orderId} className="hover:bg-slate-50 transition-colors">
                    <td className="px-6 py-4">
                      <Link href={`/partner/orders/${order.orderId}`} className="block">
                        <div className="font-medium text-indigo-600 hover:text-indigo-700">{order.orderNumber}</div>
                        <div className="text-xs text-slate-500">{order.orderDate}</div>
                      </Link>
                    </td>
                    <td className="px-6 py-4">
                      <div className="font-medium text-slate-900">{order.clientName}</div>
                    </td>
                    <td className="px-6 py-4">
                      <div className="font-medium text-slate-900">{order.productName}</div>
                      <div className="text-xs text-slate-500">Qty: {order.quantity}</div>
                    </td>
                    <td className="px-6 py-4">
                      <div className="text-sm text-slate-700">{order.expectedShipDate}</div>
                    </td>
                    <td className="px-6 py-4">
                      <span className={`inline-flex px-2.5 py-1 rounded-full text-xs font-medium ${statusColors[order.partnerStatus]?.bg || 'bg-slate-100'} ${statusColors[order.partnerStatus]?.text || 'text-slate-700'}`}>
                        {formatStatus(order.partnerStatus)}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-right">
                      <div className="font-semibold text-slate-900">{formatCurrency(order.partnerEarnings)}</div>
                    </td>
                    <td className="px-6 py-4">
                      <div className="flex items-center justify-center gap-2">
                        {order.actions.includes('ACCEPT') && (
                          <button
                            onClick={() => setShowAcceptModal(order.orderId)}
                            className="p-2 bg-green-100 text-green-600 rounded-lg hover:bg-green-200 transition-colors"
                            title="Accept Order"
                          >
                            <CheckCircle size={18} />
                          </button>
                        )}
                        {order.actions.includes('REJECT') && (
                          <button
                            onClick={() => setShowRejectModal(order.orderId)}
                            className="p-2 bg-red-100 text-red-600 rounded-lg hover:bg-red-200 transition-colors"
                            title="Reject Order"
                          >
                            <XCircle size={18} />
                          </button>
                        )}
                        {order.actions.includes('MARK_SHIPPED') && (
                          <button className="p-2 bg-purple-100 text-purple-600 rounded-lg hover:bg-purple-200 transition-colors" title="Mark as Shipped">
                            <Truck size={18} />
                          </button>
                        )}
                        <Link href={`/partner/orders/${order.orderId}`} className="p-2 bg-slate-100 text-slate-600 rounded-lg hover:bg-slate-200 transition-colors" title="View Details">
                          <ChevronRight size={18} />
                        </Link>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {filteredOrders.length === 0 && (
            <div className="p-12 text-center">
              <Package className="w-12 h-12 text-slate-300 mx-auto mb-4" />
              <p className="text-slate-500 mb-2">No orders found</p>
              <p className="text-sm text-slate-400">Try adjusting your filters</p>
            </div>
          )}
        </div>
      </main>

      {/* Accept Modal */}
      {showAcceptModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <div className="bg-white rounded-2xl p-6 max-w-md w-full mx-4 shadow-2xl">
            <h3 className="text-xl font-bold text-slate-900 mb-4">Accept Order?</h3>
            <p className="text-slate-600 mb-6">
              By accepting this order, you commit to fulfilling it by the expected ship date. Full delivery address will be revealed after acceptance.
            </p>
            <div className="flex gap-3">
              <button
                onClick={() => setShowAcceptModal(null)}
                className="flex-1 py-2.5 px-4 border border-slate-200 rounded-xl text-slate-600 font-medium hover:bg-slate-50 transition-colors"
              >
                Cancel
              </button>
              <button
                onClick={() => handleAccept(showAcceptModal)}
                className="flex-1 py-2.5 px-4 bg-green-600 text-white rounded-xl font-medium hover:bg-green-700 transition-colors"
              >
                Accept Order
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Reject Modal */}
      {showRejectModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <div className="bg-white rounded-2xl p-6 max-w-md w-full mx-4 shadow-2xl">
            <h3 className="text-xl font-bold text-slate-900 mb-4">Reject Order?</h3>
            <p className="text-slate-600 mb-4">Please select a reason for rejection:</p>
            <select className="w-full p-3 border border-slate-200 rounded-xl mb-6 focus:outline-none focus:ring-2 focus:ring-indigo-500">
              <option value="">Select reason...</option>
              <option value="capacity">Insufficient capacity</option>
              <option value="stock">Product out of stock</option>
              <option value="customization">Customization not feasible</option>
              <option value="pricing">Pricing issue</option>
              <option value="other">Other</option>
            </select>
            <div className="flex gap-3">
              <button
                onClick={() => setShowRejectModal(null)}
                className="flex-1 py-2.5 px-4 border border-slate-200 rounded-xl text-slate-600 font-medium hover:bg-slate-50 transition-colors"
              >
                Cancel
              </button>
              <button
                onClick={() => handleReject(showRejectModal, 'capacity')}
                className="flex-1 py-2.5 px-4 bg-red-600 text-white rounded-xl font-medium hover:bg-red-700 transition-colors"
              >
                Reject Order
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
