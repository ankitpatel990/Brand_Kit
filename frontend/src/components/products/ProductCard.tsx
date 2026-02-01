'use client';

import { useState } from 'react';
import Link from 'next/link';
import Image from 'next/image';
import { motion } from 'framer-motion';
import { ProductSummary } from '@/lib/product-api';

/**
 * Product Card Component
 * FRD-002 FR-17: Product Listing Page
 */

interface ProductCardProps {
  product: ProductSummary;
  onQuickView?: (product: ProductSummary) => void;
}

export default function ProductCard({ product, onQuickView }: ProductCardProps) {
  const [isHovered, setIsHovered] = useState(false);
  const [imageLoaded, setImageLoaded] = useState(false);

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0,
    }).format(price);
  };

  const getAvailabilityBadge = () => {
    switch (product.availability) {
      case 'AVAILABLE':
        return <span className="badge badge-success">In Stock</span>;
      case 'LIMITED':
        return <span className="badge badge-warning">Limited</span>;
      case 'OUT_OF_STOCK':
        return <span className="badge badge-error">Out of Stock</span>;
      case 'COMING_SOON':
        return <span className="badge badge-info">Coming Soon</span>;
      default:
        return null;
    }
  };

  return (
    <motion.div
      className="product-card group"
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.3 }}
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
    >
      {/* Image Container */}
      <Link href={`/products/${product.slug}`} className="block relative aspect-square overflow-hidden rounded-t-xl bg-slate-100">
        {/* Skeleton loader */}
        {!imageLoaded && (
          <div className="absolute inset-0 bg-gradient-to-r from-slate-200 via-slate-100 to-slate-200 animate-shimmer" />
        )}
        
        {product.imageUrl ? (
          <Image
            src={product.imageUrl}
            alt={product.name}
            fill
            className={`object-cover transition-transform duration-500 group-hover:scale-110 ${
              imageLoaded ? 'opacity-100' : 'opacity-0'
            }`}
            onLoad={() => setImageLoaded(true)}
            sizes="(max-width: 768px) 100vw, (max-width: 1200px) 50vw, 25vw"
          />
        ) : (
          <div className="absolute inset-0 flex items-center justify-center bg-slate-100">
            <svg className="w-16 h-16 text-slate-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1} d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
          </div>
        )}

        {/* Badges */}
        <div className="absolute top-3 left-3 flex flex-col gap-2">
          {product.hasDiscount && (
            <span className="badge badge-discount">
              -{product.discountPercentage}%
            </span>
          )}
          {product.ecoFriendly && (
            <span className="badge badge-eco">
              🌿 Eco
            </span>
          )}
          {product.customizable && (
            <span className="badge badge-customizable">
              ✨ Customizable
            </span>
          )}
        </div>

        {/* Quick View Button */}
        <motion.button
          className="absolute bottom-3 left-1/2 -translate-x-1/2 bg-white/95 backdrop-blur-sm px-4 py-2 rounded-full text-sm font-medium text-slate-800 shadow-lg hover:bg-white transition-all"
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: isHovered ? 1 : 0, y: isHovered ? 0 : 10 }}
          transition={{ duration: 0.2 }}
          onClick={(e) => {
            e.preventDefault();
            onQuickView?.(product);
          }}
        >
          Quick View
        </motion.button>
      </Link>

      {/* Content */}
      <div className="p-4">
        {/* Category */}
        <span className="text-xs font-medium text-amber-600 uppercase tracking-wide">
          {product.category}
        </span>

        {/* Name */}
        <Link href={`/products/${product.slug}`}>
          <h3 className="mt-1 font-semibold text-slate-800 line-clamp-2 hover:text-amber-600 transition-colors">
            {product.name}
          </h3>
        </Link>

        {/* Rating */}
        <div className="mt-2 flex items-center gap-1">
          <div className="flex">
            {[1, 2, 3, 4, 5].map((star) => (
              <svg
                key={star}
                className={`w-4 h-4 ${
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
            ({product.totalReviews})
          </span>
        </div>

        {/* Price */}
        <div className="mt-3 flex items-baseline gap-2">
          {product.hasDiscount ? (
            <>
              <span className="text-lg font-bold text-slate-800">
                {formatPrice(product.discountedPrice)}
              </span>
              <span className="text-sm text-slate-400 line-through">
                {formatPrice(product.basePrice)}
              </span>
            </>
          ) : (
            <span className="text-lg font-bold text-slate-800">
              From {formatPrice(product.basePrice)}
            </span>
          )}
        </div>

        {/* Lead Time */}
        <p className="mt-2 text-xs text-slate-500">
          Delivery in {product.leadTimeDays} days
        </p>

        {/* Actions */}
        <div className="mt-4 flex gap-2">
          <Link
            href={`/products/${product.slug}`}
            className="flex-1 btn btn-primary text-sm"
          >
            View Details
          </Link>
        </div>
      </div>

      <style jsx>{`
        .product-card {
          background: white;
          border-radius: 1rem;
          box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
          overflow: hidden;
          transition: box-shadow 0.3s ease, transform 0.3s ease;
        }
        
        .product-card:hover {
          box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
          transform: translateY(-4px);
        }

        .badge {
          padding: 0.25rem 0.5rem;
          border-radius: 9999px;
          font-size: 0.75rem;
          font-weight: 600;
        }

        .badge-discount {
          background: #ef4444;
          color: white;
        }

        .badge-eco {
          background: #10b981;
          color: white;
        }

        .badge-customizable {
          background: #8b5cf6;
          color: white;
        }

        .badge-success {
          background: #dcfce7;
          color: #166534;
        }

        .badge-warning {
          background: #fef3c7;
          color: #92400e;
        }

        .badge-error {
          background: #fee2e2;
          color: #991b1b;
        }

        .badge-info {
          background: #dbeafe;
          color: #1e40af;
        }

        .btn {
          display: inline-flex;
          align-items: center;
          justify-content: center;
          padding: 0.5rem 1rem;
          border-radius: 0.5rem;
          font-weight: 500;
          transition: all 0.2s ease;
        }

        .btn-primary {
          background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
          color: white;
        }

        .btn-primary:hover {
          background: linear-gradient(135deg, #d97706 0%, #b45309 100%);
        }

        @keyframes shimmer {
          0% { background-position: -200% 0; }
          100% { background-position: 200% 0; }
        }

        .animate-shimmer {
          background-size: 200% 100%;
          animation: shimmer 1.5s ease-in-out infinite;
        }
      `}</style>
    </motion.div>
  );
}
