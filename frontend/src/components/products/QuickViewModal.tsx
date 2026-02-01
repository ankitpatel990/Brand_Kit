'use client';

import { useEffect, useCallback } from 'react';
import Image from 'next/image';
import Link from 'next/link';
import { motion, AnimatePresence } from 'framer-motion';
import { ProductSummary } from '@/lib/product-api';

/**
 * Quick View Modal Component
 * FRD-002 Sub-Prompt 9: Product Quick View Modal
 */

interface QuickViewModalProps {
  product: ProductSummary | null;
  isOpen: boolean;
  onClose: () => void;
}

export default function QuickViewModal({ product, isOpen, onClose }: QuickViewModalProps) {
  // Handle escape key
  const handleKeyDown = useCallback((e: KeyboardEvent) => {
    if (e.key === 'Escape') {
      onClose();
    }
  }, [onClose]);

  useEffect(() => {
    if (isOpen) {
      document.addEventListener('keydown', handleKeyDown);
      document.body.style.overflow = 'hidden';
    }

    return () => {
      document.removeEventListener('keydown', handleKeyDown);
      document.body.style.overflow = 'unset';
    };
  }, [isOpen, handleKeyDown]);

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0,
    }).format(price);
  };

  if (!product) return null;

  return (
    <AnimatePresence>
      {isOpen && (
        <>
          {/* Backdrop */}
          <motion.div
            className="fixed inset-0 bg-black/60 backdrop-blur-sm z-50"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            onClick={onClose}
          />

          {/* Modal */}
          <motion.div
            className="fixed inset-0 z-50 flex items-center justify-center p-4"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
          >
            <motion.div
              className="relative bg-white rounded-2xl shadow-2xl max-w-3xl w-full max-h-[90vh] overflow-hidden"
              initial={{ scale: 0.9, y: 20 }}
              animate={{ scale: 1, y: 0 }}
              exit={{ scale: 0.9, y: 20 }}
              transition={{ type: 'spring', damping: 25, stiffness: 300 }}
              onClick={(e) => e.stopPropagation()}
            >
              {/* Close Button */}
              <button
                className="absolute top-4 right-4 z-10 p-2 rounded-full bg-white/90 backdrop-blur-sm shadow-lg hover:bg-white transition-colors"
                onClick={onClose}
                aria-label="Close quick view"
              >
                <svg className="w-5 h-5 text-slate-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>

              <div className="grid md:grid-cols-2 gap-0">
                {/* Image */}
                <div className="relative aspect-square bg-slate-100">
                  {product.imageUrl ? (
                    <Image
                      src={product.imageUrl}
                      alt={product.name}
                      fill
                      className="object-cover"
                      sizes="(max-width: 768px) 100vw, 400px"
                    />
                  ) : (
                    <div className="absolute inset-0 flex items-center justify-center">
                      <svg className="w-20 h-20 text-slate-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1} d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                      </svg>
                    </div>
                  )}

                  {/* Badges */}
                  <div className="absolute top-4 left-4 flex flex-col gap-2">
                    {product.hasDiscount && (
                      <span className="px-3 py-1 bg-red-500 text-white text-sm font-semibold rounded-full">
                        -{product.discountPercentage}% OFF
                      </span>
                    )}
                    {product.ecoFriendly && (
                      <span className="px-3 py-1 bg-emerald-500 text-white text-sm font-semibold rounded-full">
                        🌿 Eco-Friendly
                      </span>
                    )}
                  </div>
                </div>

                {/* Content */}
                <div className="p-6 md:p-8 flex flex-col">
                  {/* Category */}
                  <span className="text-sm font-medium text-amber-600 uppercase tracking-wide">
                    {product.category}
                  </span>

                  {/* Name */}
                  <h2 className="mt-2 text-2xl font-bold text-slate-800">
                    {product.name}
                  </h2>

                  {/* Rating */}
                  <div className="mt-3 flex items-center gap-2">
                    <div className="flex">
                      {[1, 2, 3, 4, 5].map((star) => (
                        <svg
                          key={star}
                          className={`w-5 h-5 ${
                            star <= Math.round(product.aggregateRating)
                              ? 'text-amber-400'
                              : 'text-slate-200'
                          }`}
                          fill="currentColor"
                          viewBox="0 0 20 20"
                        >
                          <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                        </svg>
                      ))}
                    </div>
                    <span className="text-sm text-slate-500">
                      {product.aggregateRating.toFixed(1)} ({product.totalReviews} reviews)
                    </span>
                  </div>

                  {/* Description */}
                  <p className="mt-4 text-slate-600 line-clamp-3">
                    {product.shortDescription}
                  </p>

                  {/* Price */}
                  <div className="mt-6">
                    {product.hasDiscount ? (
                      <div className="flex items-baseline gap-3">
                        <span className="text-3xl font-bold text-slate-800">
                          {formatPrice(product.discountedPrice)}
                        </span>
                        <span className="text-lg text-slate-400 line-through">
                          {formatPrice(product.basePrice)}
                        </span>
                        <span className="px-2 py-1 bg-red-100 text-red-600 text-sm font-semibold rounded">
                          Save {formatPrice(product.basePrice - product.discountedPrice)}
                        </span>
                      </div>
                    ) : (
                      <div className="flex items-baseline gap-2">
                        <span className="text-3xl font-bold text-slate-800">
                          {formatPrice(product.basePrice)}
                        </span>
                        <span className="text-sm text-slate-500">per unit</span>
                      </div>
                    )}
                  </div>

                  {/* Features */}
                  <div className="mt-6 flex flex-wrap gap-2">
                    {product.customizable && (
                      <span className="inline-flex items-center px-3 py-1 bg-purple-100 text-purple-700 text-sm rounded-full">
                        <svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
                        </svg>
                        {product.customizationType}
                      </span>
                    )}
                    <span className="inline-flex items-center px-3 py-1 bg-slate-100 text-slate-700 text-sm rounded-full">
                      <svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                      </svg>
                      {product.leadTimeDays} days delivery
                    </span>
                  </div>

                  {/* Actions */}
                  <div className="mt-auto pt-6 flex gap-3">
                    <Link
                      href={`/products/${product.slug}`}
                      className="flex-1 py-3 px-4 border-2 border-amber-500 text-amber-600 font-semibold rounded-xl hover:bg-amber-50 transition-colors text-center"
                      onClick={onClose}
                    >
                      View Full Details
                    </Link>
                    <Link
                      href={`/customize/${product.slug}`}
                      className="flex-1 py-3 px-4 bg-gradient-to-r from-amber-500 to-amber-600 text-white font-semibold rounded-xl hover:from-amber-600 hover:to-amber-700 transition-all text-center shadow-lg shadow-amber-500/25"
                      onClick={onClose}
                    >
                      Customize & Order
                    </Link>
                  </div>
                </div>
              </div>
            </motion.div>
          </motion.div>
        </>
      )}
    </AnimatePresence>
  );
}
