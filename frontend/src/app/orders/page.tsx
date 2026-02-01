'use client';

import { useState, useEffect } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import Link from 'next/link';
import { motion } from 'framer-motion';
import { useAuth } from '@/lib/auth-context';
import {
  orderApi,
  OrderListResponse,
  formatPrice,
  formatDate,
  getStatusColor,
} from '@/lib/order-api';
import { Button } from '@/components/ui/Button';

const ORDER_STATUSES = [
  { value: '', label: 'All Orders' },
  { value: 'CONFIRMED', label: 'Confirmed' },
  { value: 'IN_PRODUCTION', label: 'In Production' },
  { value: 'SHIPPED', label: 'Shipped' },
  { value: 'DELIVERED', label: 'Delivered' },
  { value: 'CANCELLED', label: 'Cancelled' },
];

export default function OrdersPage() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const { user, isLoading: authLoading } = useAuth();
  
  const [orders, setOrders] = useState<OrderListResponse[]>([]);
  const [totalPages, setTotalPages] = useState(0);
  const [currentPage, setCurrentPage] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  
  const [statusFilter, setStatusFilter] = useState('');
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    if (!authLoading && !user) {
      router.push('/auth/login?redirect=/orders');
      return;
    }

    if (user) {
      fetchOrders();
    }
  }, [user, authLoading, router, currentPage, statusFilter]);

  const fetchOrders = async () => {
    try {
      setIsLoading(true);
      const data = await orderApi.getOrders(
        currentPage,
        10,
        statusFilter || undefined,
        searchTerm || undefined
      );
      setOrders(data.content);
      setTotalPages(data.totalPages);
    } catch (err) {
      console.error(err);
      setError('Failed to load orders');
    } finally {
      setIsLoading(false);
    }
  };

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    setCurrentPage(0);
    fetchOrders();
  };

  const handleReorder = async (orderId: string) => {
    try {
      const result = await orderApi.reorder(orderId);
      if (result.success) {
        router.push('/cart');
      } else {
        setError(result.message);
      }
    } catch (err) {
      console.error(err);
      setError('Failed to reorder');
    }
  };

  if (authLoading || (isLoading && orders.length === 0)) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-slate-50 via-white to-slate-100 flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-4 border-indigo-600 border-t-transparent"></div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-white to-slate-100 py-8">
      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Header */}
        <motion.div
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          className="mb-8"
        >
          <h1 className="text-3xl font-bold text-slate-900">My Orders</h1>
          <p className="text-slate-600 mt-1">Track and manage your orders</p>
        </motion.div>

        {error && (
          <motion.div
            initial={{ opacity: 0, y: -10 }}
            animate={{ opacity: 1, y: 0 }}
            className="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg text-red-700"
          >
            {error}
            <button onClick={() => setError(null)} className="ml-4 text-red-500 hover:text-red-700">
              ✕
            </button>
          </motion.div>
        )}

        {/* Filters */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.1 }}
          className="bg-white rounded-2xl shadow-md border border-slate-100 p-4 mb-6"
        >
          <div className="flex flex-col sm:flex-row gap-4">
            <form onSubmit={handleSearch} className="flex-1 flex gap-2">
              <input
                type="text"
                placeholder="Search by Order ID..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="flex-1 px-4 py-2 border border-slate-200 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
              />
              <Button type="submit" variant="secondary">
                Search
              </Button>
            </form>
            <select
              value={statusFilter}
              onChange={(e) => {
                setStatusFilter(e.target.value);
                setCurrentPage(0);
              }}
              className="px-4 py-2 border border-slate-200 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
            >
              {ORDER_STATUSES.map((status) => (
                <option key={status.value} value={status.value}>
                  {status.label}
                </option>
              ))}
            </select>
          </div>
        </motion.div>

        {/* Orders List */}
        {orders.length === 0 ? (
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="text-center py-16 bg-white rounded-2xl shadow-md border border-slate-100"
          >
            <div className="w-24 h-24 mx-auto mb-6 rounded-full bg-slate-100 flex items-center justify-center">
              <svg className="w-12 h-12 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
              </svg>
            </div>
            <h2 className="text-xl font-semibold text-slate-900 mb-2">No orders yet</h2>
            <p className="text-slate-600 mb-6">Start shopping to see your orders here</p>
            <Link href="/products">
              <Button>Browse Products</Button>
            </Link>
          </motion.div>
        ) : (
          <div className="space-y-4">
            {orders.map((order, index) => (
              <motion.div
                key={order.id}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: index * 0.05 }}
                className="bg-white rounded-2xl shadow-md border border-slate-100 p-6 hover:shadow-lg transition-shadow"
              >
                <div className="flex flex-col lg:flex-row lg:items-center gap-4">
                  {/* Order Image */}
                  <div className="w-20 h-20 bg-slate-100 rounded-xl flex-shrink-0 flex items-center justify-center">
                    {order.firstProductImageUrl ? (
                      <img
                        src={order.firstProductImageUrl}
                        alt={order.firstProductName || 'Product'}
                        className="w-full h-full object-cover rounded-xl"
                      />
                    ) : (
                      <svg className="w-8 h-8 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
                      </svg>
                    )}
                  </div>

                  {/* Order Info */}
                  <div className="flex-1 min-w-0">
                    <div className="flex flex-wrap items-center gap-3 mb-2">
                      <Link
                        href={`/orders/${order.id}`}
                        className="text-lg font-semibold text-slate-900 hover:text-indigo-600"
                      >
                        {order.orderNumber}
                      </Link>
                      <span className={`px-3 py-1 rounded-full text-sm font-medium ${getStatusColor(order.status)}`}>
                        {order.statusDisplayName}
                      </span>
                    </div>
                    <p className="text-slate-600 text-sm mb-1">
                      {order.firstProductName}
                      {order.itemCount > 1 && ` +${order.itemCount - 1} more`}
                    </p>
                    <p className="text-slate-500 text-sm">
                      Ordered on {formatDate(order.orderDate)} • {order.totalQuantity} items
                    </p>
                  </div>

                  {/* Price & Actions */}
                  <div className="flex flex-col sm:flex-row lg:flex-col items-start sm:items-center lg:items-end gap-3">
                    <div className="text-right">
                      <p className="text-lg font-bold text-slate-900">{formatPrice(order.totalAmount)}</p>
                    </div>
                    <div className="flex gap-2">
                      <Link href={`/orders/${order.id}`}>
                        <Button variant="secondary" size="sm">
                          View Details
                        </Button>
                      </Link>
                      {order.canReorder && (
                        <Button
                          size="sm"
                          onClick={() => handleReorder(order.id)}
                        >
                          Reorder
                        </Button>
                      )}
                      {order.hasInvoice && (
                        <Button
                          variant="secondary"
                          size="sm"
                          onClick={async () => {
                            const invoice = await orderApi.getInvoice(order.id);
                            window.open(invoice.invoiceUrl, '_blank');
                          }}
                        >
                          Invoice
                        </Button>
                      )}
                    </div>
                  </div>
                </div>
              </motion.div>
            ))}
          </div>
        )}

        {/* Pagination */}
        {totalPages > 1 && (
          <div className="mt-8 flex justify-center gap-2">
            <Button
              variant="secondary"
              disabled={currentPage === 0}
              onClick={() => setCurrentPage((p) => Math.max(0, p - 1))}
            >
              Previous
            </Button>
            <span className="flex items-center px-4 text-slate-600">
              Page {currentPage + 1} of {totalPages}
            </span>
            <Button
              variant="secondary"
              disabled={currentPage >= totalPages - 1}
              onClick={() => setCurrentPage((p) => p + 1)}
            >
              Next
            </Button>
          </div>
        )}
      </div>
    </div>
  );
}
