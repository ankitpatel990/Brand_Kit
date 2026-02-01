'use client';

import { useState, useEffect } from 'react';
import { useRouter, useParams, useSearchParams } from 'next/navigation';
import Link from 'next/link';
import { motion } from 'framer-motion';
import { useAuth } from '@/lib/auth-context';
import {
  orderApi,
  OrderResponse,
  OrderStatusHistory,
  formatPrice,
  formatDate,
  getStatusColor,
} from '@/lib/order-api';
import { Button } from '@/components/ui/Button';

const STATUS_STEPS = [
  'CONFIRMED',
  'ACCEPTED',
  'IN_PRODUCTION',
  'READY_TO_SHIP',
  'SHIPPED',
  'DELIVERED',
];

export default function OrderDetailPage() {
  const router = useRouter();
  const params = useParams();
  const searchParams = useSearchParams();
  const { user, isLoading: authLoading } = useAuth();
  
  const orderId = params.orderId as string;
  const isNewOrder = searchParams.get('new') === 'true';
  
  const [order, setOrder] = useState<OrderResponse | null>(null);
  const [statusHistory, setStatusHistory] = useState<OrderStatusHistory[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!authLoading && !user) {
      router.push('/auth/login?redirect=/orders/' + orderId);
      return;
    }

    if (user && orderId) {
      fetchOrderDetails();
    }
  }, [user, authLoading, orderId, router]);

  const fetchOrderDetails = async () => {
    try {
      setIsLoading(true);
      const [orderData, historyData] = await Promise.all([
        orderApi.getOrder(orderId),
        orderApi.getOrderStatusHistory(orderId),
      ]);
      setOrder(orderData);
      setStatusHistory(historyData);
    } catch (err) {
      console.error(err);
      setError('Failed to load order details');
    } finally {
      setIsLoading(false);
    }
  };

  const handleReorder = async () => {
    if (!order) return;
    try {
      const result = await orderApi.reorder(order.id);
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

  const downloadInvoice = async () => {
    if (!order) return;
    try {
      const invoice = await orderApi.getInvoice(order.id);
      window.open(invoice.invoiceUrl, '_blank');
    } catch (err) {
      console.error(err);
      setError('Invoice not available');
    }
  };

  if (authLoading || isLoading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-slate-50 via-white to-slate-100 flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-4 border-indigo-600 border-t-transparent"></div>
      </div>
    );
  }

  if (!order) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-slate-50 via-white to-slate-100 flex items-center justify-center">
        <div className="text-center">
          <h1 className="text-2xl font-bold text-slate-900 mb-4">Order not found</h1>
          <Link href="/orders">
            <Button>View All Orders</Button>
          </Link>
        </div>
      </div>
    );
  }

  const currentStatusIndex = STATUS_STEPS.indexOf(order.status);

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-white to-slate-100 py-8">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Success Banner for New Orders */}
        {isNewOrder && (
          <motion.div
            initial={{ opacity: 0, y: -20 }}
            animate={{ opacity: 1, y: 0 }}
            className="mb-6 p-6 bg-gradient-to-r from-green-500 to-emerald-500 rounded-2xl text-white text-center"
          >
            <div className="text-4xl mb-2">🎉</div>
            <h2 className="text-2xl font-bold mb-2">Order Placed Successfully!</h2>
            <p className="opacity-90">Your order {order.orderNumber} has been confirmed.</p>
          </motion.div>
        )}

        {/* Header */}
        <motion.div
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 mb-6"
        >
          <div>
            <Link
              href="/orders"
              className="text-indigo-600 hover:text-indigo-700 text-sm font-medium mb-2 inline-flex items-center"
            >
              ← Back to Orders
            </Link>
            <h1 className="text-2xl font-bold text-slate-900">{order.orderNumber}</h1>
            <p className="text-slate-600">Ordered on {formatDate(order.orderDate)}</p>
          </div>
          <span className={`px-4 py-2 rounded-full text-sm font-semibold ${getStatusColor(order.status)}`}>
            {order.statusDisplayName}
          </span>
        </motion.div>

        {error && (
          <motion.div
            initial={{ opacity: 0, y: -10 }}
            animate={{ opacity: 1, y: 0 }}
            className="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg text-red-700"
          >
            {error}
          </motion.div>
        )}

        {/* Order Progress */}
        {!['CANCELLED', 'REFUNDED', 'PAYMENT_FAILED', 'PENDING_PAYMENT'].includes(order.status) && (
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.1 }}
            className="bg-white rounded-2xl shadow-md border border-slate-100 p-6 mb-6"
          >
            <h2 className="text-lg font-semibold text-slate-900 mb-6">Order Progress</h2>
            <div className="relative">
              <div className="absolute left-0 top-4 w-full h-1 bg-slate-200 rounded">
                <div
                  className="h-full bg-indigo-600 rounded transition-all duration-500"
                  style={{ width: `${Math.max(0, (currentStatusIndex / (STATUS_STEPS.length - 1)) * 100)}%` }}
                />
              </div>
              <div className="relative flex justify-between">
                {['Confirmed', 'Processing', 'In Production', 'Ready', 'Shipped', 'Delivered'].map((step, index) => {
                  const isCompleted = index <= currentStatusIndex;
                  const isCurrent = index === currentStatusIndex;
                  return (
                    <div key={step} className="flex flex-col items-center">
                      <div
                        className={`w-8 h-8 rounded-full flex items-center justify-center transition-colors ${
                          isCompleted
                            ? 'bg-indigo-600 text-white'
                            : 'bg-slate-200 text-slate-400'
                        } ${isCurrent ? 'ring-4 ring-indigo-200' : ''}`}
                      >
                        {isCompleted ? (
                          <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                          </svg>
                        ) : (
                          <span className="text-xs font-semibold">{index + 1}</span>
                        )}
                      </div>
                      <span className={`mt-2 text-xs font-medium ${isCompleted ? 'text-indigo-600' : 'text-slate-400'}`}>
                        {step}
                      </span>
                    </div>
                  );
                })}
              </div>
            </div>
          </motion.div>
        )}

        {/* Tracking Info */}
        {order.trackingInfo && (
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.15 }}
            className="bg-white rounded-2xl shadow-md border border-slate-100 p-6 mb-6"
          >
            <h2 className="text-lg font-semibold text-slate-900 mb-4">Tracking Information</h2>
            <div className="flex flex-col sm:flex-row justify-between gap-4">
              <div>
                <p className="text-slate-600">
                  <span className="font-medium">Courier:</span> {order.trackingInfo.courierName}
                </p>
                <p className="text-slate-600">
                  <span className="font-medium">Tracking ID:</span> {order.trackingInfo.trackingId}
                </p>
              </div>
              <a
                href={order.trackingInfo.trackingUrl}
                target="_blank"
                rel="noopener noreferrer"
                className="inline-flex items-center text-indigo-600 hover:text-indigo-700 font-medium"
              >
                Track Shipment
                <svg className="w-4 h-4 ml-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14" />
                </svg>
              </a>
            </div>
          </motion.div>
        )}

        {/* Order Items */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.2 }}
          className="bg-white rounded-2xl shadow-md border border-slate-100 p-6 mb-6"
        >
          <h2 className="text-lg font-semibold text-slate-900 mb-4">Order Items</h2>
          <div className="space-y-4">
            {order.items.map((item) => (
              <div key={item.id} className="flex gap-4 py-4 border-b border-slate-100 last:border-0">
                <div className="w-20 h-20 bg-slate-100 rounded-lg flex-shrink-0 overflow-hidden">
                  {item.previewImageUrl || item.productImageUrl ? (
                    <img
                      src={item.previewImageUrl || item.productImageUrl || ''}
                      alt={item.productName}
                      className="w-full h-full object-cover"
                    />
                  ) : (
                    <div className="w-full h-full flex items-center justify-center text-slate-400">
                      <svg className="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                      </svg>
                    </div>
                  )}
                </div>
                <div className="flex-1">
                  <Link
                    href={`/products/${item.productSlug}`}
                    className="font-semibold text-slate-900 hover:text-indigo-600"
                  >
                    {item.productName}
                  </Link>
                  {item.hasCustomization && (
                    <p className="text-sm text-indigo-600">With your logo</p>
                  )}
                  <div className="mt-1 flex flex-wrap items-center gap-2 text-sm text-slate-600">
                    <span>Qty: {item.quantity}</span>
                    <span>•</span>
                    <span>{formatPrice(item.effectiveUnitPrice)}/unit</span>
                    {item.discountPercentage > 0 && (
                      <>
                        <span>•</span>
                        <span className="text-green-600">{item.discountPercentage}% off</span>
                      </>
                    )}
                  </div>
                </div>
                <div className="text-right">
                  <p className="font-semibold text-slate-900">{formatPrice(item.subtotal)}</p>
                </div>
              </div>
            ))}
          </div>
        </motion.div>

        {/* Delivery & Pricing */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
          {/* Delivery Address */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.25 }}
            className="bg-white rounded-2xl shadow-md border border-slate-100 p-6"
          >
            <h2 className="text-lg font-semibold text-slate-900 mb-4">Delivery Address</h2>
            <div className="text-slate-600">
              <p className="font-medium text-slate-900">{order.deliveryAddress.fullName}</p>
              <p>{order.deliveryAddress.phone}</p>
              <p>{order.deliveryAddress.formattedAddress}</p>
            </div>
            <div className="mt-4 pt-4 border-t border-slate-100">
              <p className="text-sm text-slate-600">
                <span className="font-medium">Delivery:</span> {order.deliveryOptionDisplayName}
              </p>
              {order.estimatedDeliveryRange && (
                <p className="text-sm text-slate-600">
                  <span className="font-medium">Expected:</span> {order.estimatedDeliveryRange}
                </p>
              )}
            </div>
          </motion.div>

          {/* Price Details */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.3 }}
            className="bg-white rounded-2xl shadow-md border border-slate-100 p-6"
          >
            <h2 className="text-lg font-semibold text-slate-900 mb-4">Price Details</h2>
            <div className="space-y-2 text-sm">
              {order.pricing.totalDiscount > 0 && (
                <>
                  <div className="flex justify-between text-slate-600">
                    <span>Original Price</span>
                    <span className="line-through">{formatPrice(order.pricing.originalSubtotal)}</span>
                  </div>
                  <div className="flex justify-between text-green-600">
                    <span>Discount</span>
                    <span>-{formatPrice(order.pricing.totalDiscount)}</span>
                  </div>
                </>
              )}
              <div className="flex justify-between text-slate-600">
                <span>Subtotal</span>
                <span>{formatPrice(order.pricing.subtotal)}</span>
              </div>
              <div className="flex justify-between text-slate-600">
                <span>GST (18%)</span>
                <span>{formatPrice(order.pricing.gstAmount)}</span>
              </div>
              <div className="flex justify-between text-slate-600">
                <span>Delivery</span>
                <span>
                  {order.pricing.deliveryCharges === 0 ? (
                    <span className="text-green-600">Free</span>
                  ) : (
                    formatPrice(order.pricing.deliveryCharges)
                  )}
                </span>
              </div>
              <div className="pt-2 border-t border-slate-200 flex justify-between font-bold text-slate-900">
                <span>Total</span>
                <span>{formatPrice(order.pricing.totalAmount)}</span>
              </div>
              {order.pricing.totalSavings > 0 && (
                <div className="mt-2 p-2 bg-green-50 text-green-700 rounded-lg text-center font-medium">
                  You saved {formatPrice(order.pricing.totalSavings)} on this order!
                </div>
              )}
            </div>
          </motion.div>
        </div>

        {/* Status History */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.35 }}
          className="bg-white rounded-2xl shadow-md border border-slate-100 p-6 mb-6"
        >
          <h2 className="text-lg font-semibold text-slate-900 mb-4">Order Timeline</h2>
          <div className="space-y-4">
            {statusHistory.map((history, index) => (
              <div key={history.id} className="flex gap-4">
                <div className="relative">
                  <div className={`w-3 h-3 rounded-full ${index === 0 ? 'bg-indigo-600' : 'bg-slate-300'}`} />
                  {index < statusHistory.length - 1 && (
                    <div className="absolute top-3 left-1/2 -translate-x-1/2 w-0.5 h-full bg-slate-200" />
                  )}
                </div>
                <div className="pb-4">
                  <p className="font-medium text-slate-900">{history.statusDisplayName}</p>
                  <p className="text-sm text-slate-600">{history.description}</p>
                  <p className="text-xs text-slate-400 mt-1">
                    {new Date(history.timestamp).toLocaleString()}
                  </p>
                </div>
              </div>
            ))}
          </div>
        </motion.div>

        {/* Actions */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.4 }}
          className="flex flex-wrap gap-4"
        >
          {order.status === 'DELIVERED' && (
            <Button onClick={handleReorder}>
              Reorder
            </Button>
          )}
          {order.invoiceUrl && (
            <Button variant="secondary" onClick={downloadInvoice}>
              Download Invoice
            </Button>
          )}
          <Link href="/orders">
            <Button variant="secondary">
              View All Orders
            </Button>
          </Link>
        </motion.div>
      </div>
    </div>
  );
}
