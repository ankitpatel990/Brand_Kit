'use client';

import React, { useState } from 'react';
import Link from 'next/link';
import { 
  Bell, 
  LogOut,
  User,
  ArrowLeft,
  Package,
  CheckCircle,
  XCircle,
  AlertTriangle,
  IndianRupee,
  Clock,
  Check,
  Trash2
} from 'lucide-react';
import { Button } from '@/components/ui/Button';

/**
 * Partner Notifications Page - FRD-005 FR-53
 * Partner notification center
 */

const mockNotifications = [
  {
    id: '1',
    type: 'ORDER_ASSIGNED',
    title: 'New Order Assigned',
    message: 'Order #BK-2026-0789 (T-Shirt x25) has been assigned to you. Accept within 30 minutes.',
    orderId: 'order-789',
    timestamp: '2026-01-31T10:30:00Z',
    isRead: false,
    urgent: true
  },
  {
    id: '2',
    type: 'SETTLEMENT_PAID',
    title: 'Settlement Processed',
    message: 'Your settlement of ₹45,230 for Dec 16-31 has been credited to your bank account.',
    timestamp: '2026-01-30T14:20:00Z',
    isRead: false,
    urgent: false
  },
  {
    id: '3',
    type: 'DISCOUNT_APPROVED',
    title: 'Discount Approved',
    message: 'Your 10% discount on "Premium Ceramic Mug" has been approved by admin.',
    timestamp: '2026-01-29T11:15:00Z',
    isRead: true,
    urgent: false
  },
  {
    id: '4',
    type: 'ORDER_STATUS_UPDATE',
    title: 'Order Status Reminder',
    message: 'Order #BK-2026-0785 is pending production update. Please update the status.',
    orderId: 'order-785',
    timestamp: '2026-01-28T16:45:00Z',
    isRead: true,
    urgent: false
  },
  {
    id: '5',
    type: 'PERFORMANCE_WARNING',
    title: 'Performance Alert',
    message: 'Your fulfillment rate dropped below 90% this week. Maintain quality to keep tier status.',
    timestamp: '2026-01-27T09:00:00Z',
    isRead: true,
    urgent: true
  },
  {
    id: '6',
    type: 'ORDER_ASSIGNED',
    title: 'New Order Assigned',
    message: 'Order #BK-2026-0782 (Cotton Bags x50) has been assigned to you.',
    orderId: 'order-782',
    timestamp: '2026-01-26T12:30:00Z',
    isRead: true,
    urgent: false
  },
  {
    id: '7',
    type: 'DISCOUNT_REJECTED',
    title: 'Discount Rejected',
    message: 'Your 30% discount on "Laptop Bag" was rejected. Reason: Exceeds platform limit.',
    timestamp: '2026-01-25T15:10:00Z',
    isRead: true,
    urgent: false
  },
];

const typeConfig: Record<string, { bg: string; text: string; icon: React.ReactNode }> = {
  'ORDER_ASSIGNED': { bg: 'bg-blue-100', text: 'text-blue-600', icon: <Package className="w-5 h-5" /> },
  'ORDER_ACCEPTED': { bg: 'bg-green-100', text: 'text-green-600', icon: <CheckCircle className="w-5 h-5" /> },
  'ORDER_REJECTED': { bg: 'bg-red-100', text: 'text-red-600', icon: <XCircle className="w-5 h-5" /> },
  'ORDER_STATUS_UPDATE': { bg: 'bg-purple-100', text: 'text-purple-600', icon: <Clock className="w-5 h-5" /> },
  'SETTLEMENT_DUE': { bg: 'bg-amber-100', text: 'text-amber-600', icon: <IndianRupee className="w-5 h-5" /> },
  'SETTLEMENT_PAID': { bg: 'bg-green-100', text: 'text-green-600', icon: <IndianRupee className="w-5 h-5" /> },
  'DISCOUNT_APPROVED': { bg: 'bg-green-100', text: 'text-green-600', icon: <CheckCircle className="w-5 h-5" /> },
  'DISCOUNT_REJECTED': { bg: 'bg-red-100', text: 'text-red-600', icon: <XCircle className="w-5 h-5" /> },
  'PERFORMANCE_WARNING': { bg: 'bg-amber-100', text: 'text-amber-600', icon: <AlertTriangle className="w-5 h-5" /> },
};

const formatRelativeTime = (timestamp: string) => {
  const now = new Date();
  const date = new Date(timestamp);
  const diffInSeconds = Math.floor((now.getTime() - date.getTime()) / 1000);
  
  if (diffInSeconds < 60) return 'Just now';
  if (diffInSeconds < 3600) return `${Math.floor(diffInSeconds / 60)}m ago`;
  if (diffInSeconds < 86400) return `${Math.floor(diffInSeconds / 3600)}h ago`;
  if (diffInSeconds < 604800) return `${Math.floor(diffInSeconds / 86400)}d ago`;
  return date.toLocaleDateString('en-IN', { month: 'short', day: 'numeric' });
};

export default function PartnerNotificationsPage() {
  const [notifications, setNotifications] = useState(mockNotifications);
  const [filter, setFilter] = useState<'all' | 'unread'>('all');

  const unreadCount = notifications.filter(n => !n.isRead).length;
  const filteredNotifications = filter === 'unread' 
    ? notifications.filter(n => !n.isRead) 
    : notifications;

  const markAsRead = (id: string) => {
    setNotifications(prev => prev.map(n => 
      n.id === id ? { ...n, isRead: true } : n
    ));
  };

  const markAllAsRead = () => {
    setNotifications(prev => prev.map(n => ({ ...n, isRead: true })));
  };

  const deleteNotification = (id: string) => {
    setNotifications(prev => prev.filter(n => n.id !== id));
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-slate-50 to-amber-50">
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
      <main className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Page Header */}
        <div className="flex items-center justify-between mb-6">
          <div className="flex items-center gap-3">
            <div className="w-12 h-12 bg-gradient-to-br from-amber-400 to-orange-500 rounded-xl flex items-center justify-center shadow-lg">
              <Bell className="w-6 h-6 text-white" />
            </div>
            <div>
              <h1 className="text-2xl font-bold font-display text-slate-900">Notifications</h1>
              <p className="text-sm text-slate-600">
                {unreadCount > 0 ? `${unreadCount} unread notifications` : 'All caught up!'}
              </p>
            </div>
          </div>
          {unreadCount > 0 && (
            <Button variant="outline" size="sm" onClick={markAllAsRead}>
              <Check size={16} className="mr-1" /> Mark all as read
            </Button>
          )}
        </div>

        {/* Filter Tabs */}
        <div className="flex gap-2 mb-6">
          <button
            onClick={() => setFilter('all')}
            className={`px-4 py-2 rounded-xl text-sm font-medium transition-all ${
              filter === 'all'
                ? 'bg-slate-900 text-white'
                : 'bg-white text-slate-600 hover:bg-slate-50 border border-slate-200'
            }`}
          >
            All
          </button>
          <button
            onClick={() => setFilter('unread')}
            className={`px-4 py-2 rounded-xl text-sm font-medium transition-all flex items-center gap-2 ${
              filter === 'unread'
                ? 'bg-slate-900 text-white'
                : 'bg-white text-slate-600 hover:bg-slate-50 border border-slate-200'
            }`}
          >
            Unread
            {unreadCount > 0 && (
              <span className="px-2 py-0.5 bg-red-500 text-white text-xs rounded-full">{unreadCount}</span>
            )}
          </button>
        </div>

        {/* Notifications List */}
        <div className="space-y-3">
          {filteredNotifications.length === 0 ? (
            <div className="bg-white rounded-2xl p-12 text-center border border-slate-100">
              <Bell className="w-12 h-12 text-slate-300 mx-auto mb-4" />
              <p className="text-slate-500">No notifications to show</p>
            </div>
          ) : (
            filteredNotifications.map((notification) => (
              <div
                key={notification.id}
                className={`bg-white rounded-xl p-4 border transition-all hover:shadow-md ${
                  notification.isRead ? 'border-slate-100' : 'border-indigo-200 bg-indigo-50/30'
                } ${notification.urgent && !notification.isRead ? 'ring-2 ring-red-100' : ''}`}
              >
                <div className="flex items-start gap-4">
                  <div className={`w-10 h-10 rounded-lg flex items-center justify-center ${
                    typeConfig[notification.type]?.bg || 'bg-slate-100'
                  } ${typeConfig[notification.type]?.text || 'text-slate-600'}`}>
                    {typeConfig[notification.type]?.icon || <Bell className="w-5 h-5" />}
                  </div>
                  
                  <div className="flex-1 min-w-0">
                    <div className="flex items-start justify-between gap-2">
                      <div>
                        <div className="flex items-center gap-2">
                          <h3 className={`font-medium ${notification.isRead ? 'text-slate-700' : 'text-slate-900'}`}>
                            {notification.title}
                          </h3>
                          {notification.urgent && !notification.isRead && (
                            <span className="px-2 py-0.5 bg-red-100 text-red-600 text-xs font-medium rounded-full">
                              Urgent
                            </span>
                          )}
                          {!notification.isRead && (
                            <span className="w-2 h-2 bg-indigo-500 rounded-full" />
                          )}
                        </div>
                        <p className={`text-sm mt-1 ${notification.isRead ? 'text-slate-500' : 'text-slate-600'}`}>
                          {notification.message}
                        </p>
                      </div>
                      <span className="text-xs text-slate-400 whitespace-nowrap">
                        {formatRelativeTime(notification.timestamp)}
                      </span>
                    </div>

                    <div className="flex items-center gap-2 mt-3">
                      {notification.orderId && (
                        <Link 
                          href={`/partner/orders/${notification.orderId}`}
                          className="text-xs px-3 py-1.5 bg-indigo-100 text-indigo-700 rounded-lg font-medium hover:bg-indigo-200 transition-colors"
                        >
                          View Order
                        </Link>
                      )}
                      {!notification.isRead && (
                        <button
                          onClick={() => markAsRead(notification.id)}
                          className="text-xs px-3 py-1.5 text-slate-500 hover:text-slate-700 hover:bg-slate-100 rounded-lg transition-colors"
                        >
                          Mark as read
                        </button>
                      )}
                      <button
                        onClick={() => deleteNotification(notification.id)}
                        className="text-xs px-3 py-1.5 text-red-500 hover:text-red-700 hover:bg-red-50 rounded-lg transition-colors ml-auto"
                      >
                        <Trash2 size={14} />
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            ))
          )}
        </div>
      </main>
    </div>
  );
}
