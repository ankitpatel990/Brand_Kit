'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import Image from 'next/image';
import Link from 'next/link';
import { motion, AnimatePresence } from 'framer-motion';
import { useAuth } from '@/lib/auth-context';
import { cartApi, Cart, CartItem, formatPrice } from '@/lib/order-api';
import { Button } from '@/components/ui/Button';

export default function CartPage() {
  const router = useRouter();
  const { user, isLoading: authLoading } = useAuth();
  const [cart, setCart] = useState<Cart | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [updatingItems, setUpdatingItems] = useState<Set<string>>(new Set());
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!authLoading && !user) {
      router.push('/auth/login?redirect=/cart');
      return;
    }

    if (user) {
      fetchCart();
    }
  }, [user, authLoading, router]);

  const fetchCart = async () => {
    try {
      setIsLoading(true);
      const data = await cartApi.getCart();
      setCart(data);
    } catch (err) {
      setError('Failed to load cart');
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  };

  const updateQuantity = async (itemId: string, quantity: number) => {
    if (quantity < 1 || quantity > 10000) return;

    setUpdatingItems((prev) => new Set(prev).add(itemId));
    try {
      const updatedCart = await cartApi.updateCartItem(itemId, { quantity });
      setCart(updatedCart);
    } catch (err) {
      console.error(err);
      setError('Failed to update quantity');
    } finally {
      setUpdatingItems((prev) => {
        const next = new Set(prev);
        next.delete(itemId);
        return next;
      });
    }
  };

  const removeItem = async (itemId: string) => {
    setUpdatingItems((prev) => new Set(prev).add(itemId));
    try {
      const updatedCart = await cartApi.removeCartItem(itemId);
      setCart(updatedCart);
    } catch (err) {
      console.error(err);
      setError('Failed to remove item');
    } finally {
      setUpdatingItems((prev) => {
        const next = new Set(prev);
        next.delete(itemId);
        return next;
      });
    }
  };

  const handleCheckout = async () => {
    try {
      const validation = await cartApi.validateCart();
      if (validation.isValid) {
        router.push('/checkout');
      } else {
        setError(validation.errors[0]?.message || 'Cart validation failed');
      }
    } catch (err) {
      console.error(err);
      setError('Failed to validate cart');
    }
  };

  if (authLoading || isLoading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-slate-50 via-white to-slate-100 flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-4 border-indigo-600 border-t-transparent"></div>
      </div>
    );
  }

  if (!cart || cart.items.length === 0) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-slate-50 via-white to-slate-100 py-12">
        <div className="max-w-4xl mx-auto px-4">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="text-center py-16"
          >
            <div className="w-24 h-24 mx-auto mb-6 rounded-full bg-slate-100 flex items-center justify-center">
              <svg className="w-12 h-12 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
              </svg>
            </div>
            <h1 className="text-2xl font-bold text-slate-900 mb-4">Your cart is empty</h1>
            <p className="text-slate-600 mb-8">Start shopping to add items to your cart</p>
            <Link href="/products">
              <Button size="lg">Browse Products</Button>
            </Link>
          </motion.div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-white to-slate-100 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Header */}
        <motion.div
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          className="mb-8"
        >
          <h1 className="text-3xl font-bold text-slate-900">Shopping Cart</h1>
          <p className="text-slate-600 mt-1">
            {cart.itemCount} {cart.itemCount === 1 ? 'item' : 'items'} in your cart
          </p>
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

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Cart Items */}
          <div className="lg:col-span-2 space-y-4">
            <AnimatePresence mode="popLayout">
              {cart.items.map((item, index) => (
                <CartItemCard
                  key={item.cartItemId}
                  item={item}
                  index={index}
                  isUpdating={updatingItems.has(item.cartItemId)}
                  onUpdateQuantity={updateQuantity}
                  onRemove={removeItem}
                />
              ))}
            </AnimatePresence>
          </div>

          {/* Order Summary */}
          <div className="lg:col-span-1">
            <motion.div
              initial={{ opacity: 0, x: 20 }}
              animate={{ opacity: 1, x: 0 }}
              className="bg-white rounded-2xl shadow-lg border border-slate-100 p-6 sticky top-24"
            >
              <h2 className="text-xl font-bold text-slate-900 mb-6">Order Summary</h2>
              
              <div className="space-y-4">
                {cart.pricing.totalDiscount > 0 && (
                  <>
                    <div className="flex justify-between text-slate-600">
                      <span>Original Price</span>
                      <span className="line-through">{formatPrice(cart.pricing.originalSubtotal)}</span>
                    </div>
                    <div className="flex justify-between text-green-600">
                      <span>Discount</span>
                      <span>-{formatPrice(cart.pricing.totalDiscount)}</span>
                    </div>
                  </>
                )}
                
                <div className="flex justify-between text-slate-600">
                  <span>Subtotal</span>
                  <span>{formatPrice(cart.pricing.subtotal)}</span>
                </div>
                
                <div className="flex justify-between text-slate-600">
                  <span>GST (18%)</span>
                  <span>{formatPrice(cart.pricing.gst)}</span>
                </div>
                
                <div className="flex justify-between text-slate-600">
                  <span>Delivery</span>
                  {cart.pricing.freeDeliveryEligible ? (
                    <span className="text-green-600">Free</span>
                  ) : (
                    <span>{formatPrice(cart.pricing.deliveryCharges)}</span>
                  )}
                </div>

                {!cart.pricing.freeDeliveryEligible && (
                  <div className="text-sm text-slate-500 bg-slate-50 rounded-lg p-3">
                    Add {formatPrice(cart.pricing.freeDeliveryThreshold - cart.pricing.subtotal)} more for free delivery
                  </div>
                )}
                
                <div className="border-t border-slate-200 pt-4">
                  <div className="flex justify-between text-lg font-bold text-slate-900">
                    <span>Total</span>
                    <span>{formatPrice(cart.pricing.total)}</span>
                  </div>
                </div>

                {cart.pricing.totalDiscount > 0 && (
                  <div className="bg-green-50 text-green-700 rounded-lg p-3 text-sm font-medium">
                    🎉 You save {formatPrice(cart.pricing.totalDiscount)} on this order!
                  </div>
                )}
              </div>

              <Button
                onClick={handleCheckout}
                className="w-full mt-6"
                size="lg"
              >
                Proceed to Checkout
              </Button>

              <Link
                href="/products"
                className="block text-center text-indigo-600 hover:text-indigo-700 mt-4 text-sm font-medium"
              >
                Continue Shopping
              </Link>
            </motion.div>
          </div>
        </div>
      </div>
    </div>
  );
}

interface CartItemCardProps {
  item: CartItem;
  index: number;
  isUpdating: boolean;
  onUpdateQuantity: (itemId: string, quantity: number) => void;
  onRemove: (itemId: string) => void;
}

function CartItemCard({ item, index, isUpdating, onUpdateQuantity, onRemove }: CartItemCardProps) {
  return (
    <motion.div
      layout
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      exit={{ opacity: 0, x: -100 }}
      transition={{ delay: index * 0.05 }}
      className={`bg-white rounded-2xl shadow-md border border-slate-100 p-4 sm:p-6 ${
        isUpdating ? 'opacity-60' : ''
      }`}
    >
      <div className="flex flex-col sm:flex-row gap-4">
        {/* Product Image */}
        <div className="relative w-full sm:w-32 h-32 rounded-xl overflow-hidden bg-slate-100 flex-shrink-0">
          {item.previewUrl || item.productImageUrl ? (
            <Image
              src={item.previewUrl || item.productImageUrl || ''}
              alt={item.productName}
              fill
              className="object-cover"
            />
          ) : (
            <div className="w-full h-full flex items-center justify-center text-slate-400">
              <svg className="w-12 h-12" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
              </svg>
            </div>
          )}
          {item.hasCustomization && (
            <div className="absolute top-2 left-2 px-2 py-1 bg-indigo-600 text-white text-xs font-medium rounded-full">
              Customized
            </div>
          )}
        </div>

        {/* Product Details */}
        <div className="flex-1">
          <div className="flex justify-between items-start">
            <div>
              <Link
                href={`/products/${item.productSlug}`}
                className="text-lg font-semibold text-slate-900 hover:text-indigo-600 transition-colors"
              >
                {item.productName}
              </Link>
              {item.hasCustomization && (
                <p className="text-sm text-indigo-600 mt-1">With your logo</p>
              )}
            </div>
            <button
              onClick={() => onRemove(item.cartItemId)}
              disabled={isUpdating}
              className="text-slate-400 hover:text-red-500 transition-colors p-1"
            >
              <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
              </svg>
            </button>
          </div>

          {/* Pricing */}
          <div className="mt-2 flex items-baseline gap-2">
            <span className="text-lg font-bold text-slate-900">
              {formatPrice(item.effectiveUnitPrice)}
            </span>
            {item.discountPercentage > 0 && (
              <>
                <span className="text-sm text-slate-400 line-through">
                  {formatPrice(item.originalUnitPrice)}
                </span>
                <span className="text-sm font-medium text-green-600">
                  {item.discountPercentage}% off
                </span>
              </>
            )}
            <span className="text-sm text-slate-500">/unit</span>
          </div>

          {item.customizationFee > 0 && (
            <p className="text-sm text-slate-500 mt-1">
              Includes customization: +{formatPrice(item.customizationFee)}/unit
            </p>
          )}

          {/* Quantity and Subtotal */}
          <div className="mt-4 flex items-center justify-between">
            <div className="flex items-center gap-3">
              <span className="text-sm text-slate-600">Qty:</span>
              <div className="flex items-center border border-slate-200 rounded-lg">
                <button
                  onClick={() => onUpdateQuantity(item.cartItemId, item.quantity - 1)}
                  disabled={isUpdating || item.quantity <= 1}
                  className="px-3 py-1 text-slate-600 hover:bg-slate-100 disabled:opacity-50 disabled:cursor-not-allowed rounded-l-lg"
                >
                  −
                </button>
                <input
                  type="number"
                  value={item.quantity}
                  onChange={(e) => onUpdateQuantity(item.cartItemId, parseInt(e.target.value) || 1)}
                  disabled={isUpdating}
                  className="w-16 text-center border-x border-slate-200 py-1 focus:outline-none disabled:bg-slate-50"
                  min={1}
                  max={10000}
                />
                <button
                  onClick={() => onUpdateQuantity(item.cartItemId, item.quantity + 1)}
                  disabled={isUpdating || item.quantity >= 10000}
                  className="px-3 py-1 text-slate-600 hover:bg-slate-100 disabled:opacity-50 disabled:cursor-not-allowed rounded-r-lg"
                >
                  +
                </button>
              </div>
            </div>

            <div className="text-right">
              <div className="text-lg font-bold text-slate-900">
                {formatPrice(item.subtotal)}
              </div>
            </div>
          </div>
        </div>
      </div>
    </motion.div>
  );
}
