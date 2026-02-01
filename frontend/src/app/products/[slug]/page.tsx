'use client';

import { useState, useEffect, useCallback } from 'react';
import { useParams, notFound } from 'next/navigation';
import Image from 'next/image';
import Link from 'next/link';
import { motion, AnimatePresence } from 'framer-motion';
import { productApi, ProductDetail, PriceCalculationResult } from '@/lib/product-api';

/**
 * Product Detail Page
 * FRD-002 Sub-Prompt 4: Product Detail API
 * FRD-002 Sub-Prompt 10: Product SEO and Structured Data
 */

export default function ProductDetailPage() {
  const params = useParams();
  const slug = params.slug as string;

  const [product, setProduct] = useState<ProductDetail | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedImage, setSelectedImage] = useState(0);
  const [quantity, setQuantity] = useState(1);
  const [priceCalculation, setPriceCalculation] = useState<PriceCalculationResult | null>(null);
  const [isCalculating, setIsCalculating] = useState(false);
  const [showLightbox, setShowLightbox] = useState(false);

  // Load product
  useEffect(() => {
    const loadProduct = async () => {
      setIsLoading(true);
      setError(null);

      try {
        const response = await productApi.getProduct(slug);
        setProduct(response.data);
      } catch (err: any) {
        console.error('Failed to load product:', err);
        if (err.response?.status === 404) {
          notFound();
        }
        setError('Unable to load product. Please try again.');
      } finally {
        setIsLoading(false);
      }
    };

    loadProduct();
  }, [slug]);

  // Calculate price when quantity changes
  const calculatePrice = useCallback(async () => {
    if (!product) return;

    setIsCalculating(true);
    try {
      const response = await productApi.calculatePrice(product.slug, {
        quantity,
        customization: product.customizable,
      });
      setPriceCalculation(response.data);
    } catch (err) {
      console.error('Failed to calculate price:', err);
    } finally {
      setIsCalculating(false);
    }
  }, [product, quantity]);

  useEffect(() => {
    const debounce = setTimeout(() => {
      if (product) {
        calculatePrice();
      }
    }, 300);

    return () => clearTimeout(debounce);
  }, [quantity, calculatePrice, product]);

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0,
    }).format(price);
  };

  // Loading state
  if (isLoading) {
    return (
      <div className="min-h-screen bg-slate-50 py-8">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid lg:grid-cols-2 gap-12">
            <div className="aspect-square bg-slate-200 rounded-2xl animate-pulse" />
            <div className="space-y-4">
              <div className="h-6 bg-slate-200 rounded w-1/4 animate-pulse" />
              <div className="h-10 bg-slate-200 rounded w-3/4 animate-pulse" />
              <div className="h-24 bg-slate-200 rounded animate-pulse" />
              <div className="h-12 bg-slate-200 rounded w-1/2 animate-pulse" />
            </div>
          </div>
        </div>
      </div>
    );
  }

  // Error state
  if (error || !product) {
    return (
      <div className="min-h-screen bg-slate-50 flex items-center justify-center">
        <div className="text-center">
          <h2 className="text-2xl font-bold text-slate-800">{error || 'Product not found'}</h2>
          <Link href="/products" className="mt-4 inline-block px-6 py-3 bg-amber-500 text-white rounded-xl">
            Browse Products
          </Link>
        </div>
      </div>
    );
  }

  return (
    <>
      {/* Schema.org Structured Data */}
      <script
        type="application/ld+json"
        dangerouslySetInnerHTML={{
          __html: JSON.stringify({
            '@context': 'https://schema.org',
            '@type': 'Product',
            name: product.name,
            description: product.shortDescription,
            image: product.images.map(img => img.imageUrl),
            sku: product.productId,
            brand: {
              '@type': 'Brand',
              name: 'BrandKit',
            },
            offers: {
              '@type': 'Offer',
              url: `https://brandkit.in/products/${product.slug}`,
              priceCurrency: 'INR',
              price: product.hasDiscount ? product.discountedPrice : product.basePrice,
              availability: product.availability === 'AVAILABLE' 
                ? 'https://schema.org/InStock' 
                : 'https://schema.org/OutOfStock',
            },
            aggregateRating: product.totalReviews > 0 ? {
              '@type': 'AggregateRating',
              ratingValue: product.aggregateRating,
              reviewCount: product.totalReviews,
            } : undefined,
          }),
        }}
      />

      <div className="min-h-screen bg-gradient-to-b from-slate-50 to-white">
        {/* Breadcrumb */}
        <div className="bg-white border-b border-slate-100">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
            <nav className="flex items-center text-sm text-slate-500">
              <Link href="/" className="hover:text-amber-600">Home</Link>
              <span className="mx-2">/</span>
              <Link href="/products" className="hover:text-amber-600">Products</Link>
              <span className="mx-2">/</span>
              <Link href={`/products?category=${product.categorySlug}`} className="hover:text-amber-600">
                {product.category}
              </Link>
              <span className="mx-2">/</span>
              <span className="text-slate-800 font-medium">{product.name}</span>
            </nav>
          </div>
        </div>

        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="grid lg:grid-cols-2 gap-12">
            {/* Image Gallery */}
            <div className="space-y-4">
              {/* Main Image */}
              <motion.div
                className="relative aspect-square bg-white rounded-2xl overflow-hidden shadow-lg cursor-zoom-in"
                onClick={() => setShowLightbox(true)}
                whileHover={{ scale: 1.02 }}
              >
                {product.images[selectedImage] ? (
                  <Image
                    src={product.images[selectedImage].imageUrl}
                    alt={product.images[selectedImage].altText || product.name}
                    fill
                    className="object-contain"
                    priority
                    sizes="(max-width: 768px) 100vw, 50vw"
                  />
                ) : (
                  <div className="absolute inset-0 flex items-center justify-center">
                    <svg className="w-24 h-24 text-slate-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
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

                {/* Zoom hint */}
                <div className="absolute bottom-4 right-4 bg-white/90 backdrop-blur-sm px-3 py-1 rounded-full text-sm text-slate-600">
                  Click to zoom
                </div>
              </motion.div>

              {/* Thumbnails */}
              <div className="flex gap-3 overflow-x-auto pb-2">
                {product.images.map((image, index) => (
                  <button
                    key={image.id}
                    onClick={() => setSelectedImage(index)}
                    className={`relative flex-shrink-0 w-20 h-20 rounded-lg overflow-hidden border-2 transition-all ${
                      selectedImage === index
                        ? 'border-amber-500 shadow-md'
                        : 'border-transparent hover:border-slate-200'
                    }`}
                  >
                    <Image
                      src={image.thumbnailUrl || image.imageUrl}
                      alt={image.altText || `${product.name} ${index + 1}`}
                      fill
                      className="object-cover"
                      sizes="80px"
                    />
                  </button>
                ))}
              </div>
            </div>

            {/* Product Info */}
            <div className="space-y-6">
              {/* Category */}
              <Link
                href={`/products?category=${product.categorySlug}`}
                className="inline-block text-sm font-medium text-amber-600 uppercase tracking-wide hover:text-amber-700"
              >
                {product.category}
              </Link>

              {/* Name */}
              <h1 className="text-3xl md:text-4xl font-bold text-slate-800">
                {product.name}
              </h1>

              {/* Rating */}
              <div className="flex items-center gap-3">
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
                <span className="text-slate-600">
                  {product.aggregateRating.toFixed(1)} ({product.totalReviews} reviews)
                </span>
              </div>

              {/* Description */}
              <p className="text-lg text-slate-600">
                {product.shortDescription}
              </p>

              {/* Price */}
              <div className="py-4 border-y border-slate-100">
                {product.hasDiscount ? (
                  <div className="flex items-baseline gap-3 flex-wrap">
                    <span className="text-4xl font-bold text-slate-800">
                      {formatPrice(product.discountedPrice)}
                    </span>
                    <span className="text-xl text-slate-400 line-through">
                      {formatPrice(product.basePrice)}
                    </span>
                    <span className="px-3 py-1 bg-red-100 text-red-600 text-sm font-semibold rounded">
                      {product.discountName || `Save ${formatPrice(product.basePrice - product.discountedPrice)}`}
                    </span>
                  </div>
                ) : (
                  <div className="flex items-baseline gap-2">
                    <span className="text-4xl font-bold text-slate-800">
                      {formatPrice(product.basePrice)}
                    </span>
                    <span className="text-slate-500">per unit</span>
                  </div>
                )}
              </div>

              {/* Quantity & Price Calculator */}
              <div className="bg-slate-50 rounded-xl p-6 space-y-4">
                <h3 className="font-semibold text-slate-800">Calculate Your Price</h3>
                
                <div className="flex items-center gap-4">
                  <label className="text-sm text-slate-600">Quantity:</label>
                  <div className="flex items-center border border-slate-200 rounded-lg overflow-hidden">
                    <button
                      onClick={() => setQuantity(Math.max(1, quantity - 10))}
                      className="px-3 py-2 bg-slate-100 hover:bg-slate-200 text-slate-600"
                    >
                      -10
                    </button>
                    <button
                      onClick={() => setQuantity(Math.max(1, quantity - 1))}
                      className="px-3 py-2 bg-slate-50 hover:bg-slate-100 text-slate-600"
                    >
                      -
                    </button>
                    <input
                      type="number"
                      value={quantity}
                      onChange={(e) => setQuantity(Math.max(1, Math.min(10000, Number(e.target.value))))}
                      className="w-20 px-3 py-2 text-center border-0 focus:ring-0"
                      min={1}
                      max={10000}
                    />
                    <button
                      onClick={() => setQuantity(Math.min(10000, quantity + 1))}
                      className="px-3 py-2 bg-slate-50 hover:bg-slate-100 text-slate-600"
                    >
                      +
                    </button>
                    <button
                      onClick={() => setQuantity(Math.min(10000, quantity + 10))}
                      className="px-3 py-2 bg-slate-100 hover:bg-slate-200 text-slate-600"
                    >
                      +10
                    </button>
                  </div>
                </div>

                {priceCalculation && !isCalculating && (
                  <div className="space-y-2 text-sm">
                    <div className="flex justify-between">
                      <span className="text-slate-600">Unit Price (Tier {priceCalculation.applicableTier.tierNumber}):</span>
                      <span className="font-medium">{formatPrice(priceCalculation.unitPrice)}</span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-slate-600">Subtotal ({quantity} units):</span>
                      <span className="font-medium">{formatPrice(priceCalculation.subtotal)}</span>
                    </div>
                    {priceCalculation.customizationFee > 0 && (
                      <div className="flex justify-between">
                        <span className="text-slate-600">Customization Fee:</span>
                        <span className="font-medium">{formatPrice(priceCalculation.customizationFee)}</span>
                      </div>
                    )}
                    {priceCalculation.hasDiscount && (
                      <div className="flex justify-between text-emerald-600">
                        <span>Discount Applied:</span>
                        <span className="font-medium">-{formatPrice(priceCalculation.discountAmount)}</span>
                      </div>
                    )}
                    <div className="flex justify-between pt-2 border-t border-slate-200 text-lg">
                      <span className="font-semibold text-slate-800">Total:</span>
                      <span className="font-bold text-amber-600">{formatPrice(priceCalculation.totalPrice)}</span>
                    </div>
                    {priceCalculation.savings.amount > 0 && (
                      <p className="text-emerald-600 text-center pt-2">
                        🎉 {priceCalculation.savings.description}
                      </p>
                    )}
                  </div>
                )}

                {isCalculating && (
                  <div className="text-center py-4 text-slate-500">
                    Calculating...
                  </div>
                )}
              </div>

              {/* Pricing Tiers Table */}
              <div className="border border-slate-200 rounded-xl overflow-hidden">
                <table className="w-full text-sm">
                  <thead className="bg-slate-50">
                    <tr>
                      <th className="px-4 py-3 text-left font-semibold text-slate-800">Quantity</th>
                      <th className="px-4 py-3 text-right font-semibold text-slate-800">Unit Price</th>
                      <th className="px-4 py-3 text-right font-semibold text-slate-800">Savings</th>
                    </tr>
                  </thead>
                  <tbody>
                    {product.pricingTiers.map((tier, index) => (
                      <tr
                        key={tier.tierNumber}
                        className={`border-t border-slate-100 ${
                          priceCalculation?.applicableTier.tierNumber === tier.tierNumber
                            ? 'bg-amber-50'
                            : ''
                        }`}
                      >
                        <td className="px-4 py-3 text-slate-600">
                          {tier.description}
                          {priceCalculation?.applicableTier.tierNumber === tier.tierNumber && (
                            <span className="ml-2 text-xs bg-amber-500 text-white px-2 py-0.5 rounded">
                              Your Price
                            </span>
                          )}
                        </td>
                        <td className="px-4 py-3 text-right font-medium text-slate-800">
                          {formatPrice(tier.unitPrice)}
                        </td>
                        <td className="px-4 py-3 text-right text-emerald-600">
                          {tier.discountPercentage > 0 ? `${tier.discountPercentage}% off` : '-'}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>

              {/* Actions */}
              <div className="flex gap-4">
                <Link
                  href={`/customize/${product.slug}?qty=${quantity}`}
                  className="flex-1 py-4 px-6 bg-gradient-to-r from-amber-500 to-amber-600 text-white font-semibold rounded-xl text-center hover:from-amber-600 hover:to-amber-700 transition-all shadow-lg shadow-amber-500/25"
                >
                  Customize & Order
                </Link>
                <button className="p-4 border-2 border-slate-200 rounded-xl hover:border-amber-500 hover:text-amber-600 transition-colors">
                  <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8.684 13.342C8.886 12.938 9 12.482 9 12c0-.482-.114-.938-.316-1.342m0 2.684a3 3 0 110-2.684m0 2.684l6.632 3.316m-6.632-6l6.632-3.316m0 0a3 3 0 105.367-2.684 3 3 0 00-5.367 2.684zm0 9.316a3 3 0 105.368 2.684 3 3 0 00-5.368-2.684z" />
                  </svg>
                </button>
              </div>

              {/* Features */}
              <div className="grid grid-cols-2 gap-4">
                {product.customizable && (
                  <div className="flex items-center gap-3 p-4 bg-purple-50 rounded-xl">
                    <svg className="w-8 h-8 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
                    </svg>
                    <div>
                      <p className="font-semibold text-purple-900">{product.customizationType}</p>
                      {product.printArea && (
                        <p className="text-sm text-purple-600">
                          {product.printArea.width} × {product.printArea.height} {product.printArea.unit}
                        </p>
                      )}
                    </div>
                  </div>
                )}
                <div className="flex items-center gap-3 p-4 bg-blue-50 rounded-xl">
                  <svg className="w-8 h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                  <div>
                    <p className="font-semibold text-blue-900">Delivery</p>
                    <p className="text-sm text-blue-600">{product.leadTimeDays} days</p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          {/* Long Description */}
          <div className="mt-16 max-w-3xl">
            <h2 className="text-2xl font-bold text-slate-800 mb-4">Product Details</h2>
            <div className="prose prose-slate max-w-none">
              <p>{product.longDescription}</p>
            </div>
          </div>

          {/* Specifications */}
          <div className="mt-12 max-w-3xl">
            <h2 className="text-2xl font-bold text-slate-800 mb-4">Specifications</h2>
            <dl className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div className="p-4 bg-slate-50 rounded-lg">
                <dt className="text-sm text-slate-500">Material</dt>
                <dd className="font-medium text-slate-800">{product.specifications.material}</dd>
              </div>
              {product.specifications.weightGrams && (
                <div className="p-4 bg-slate-50 rounded-lg">
                  <dt className="text-sm text-slate-500">Weight</dt>
                  <dd className="font-medium text-slate-800">{product.specifications.weightGrams}g</dd>
                </div>
              )}
              {product.specifications.dimensions && (
                <div className="p-4 bg-slate-50 rounded-lg">
                  <dt className="text-sm text-slate-500">Dimensions</dt>
                  <dd className="font-medium text-slate-800">{product.specifications.dimensions}</dd>
                </div>
              )}
              {product.specifications.availableColors && product.specifications.availableColors.length > 0 && (
                <div className="p-4 bg-slate-50 rounded-lg">
                  <dt className="text-sm text-slate-500">Available Colors</dt>
                  <dd className="font-medium text-slate-800">
                    {product.specifications.availableColors.join(', ')}
                  </dd>
                </div>
              )}
            </dl>
          </div>

          {/* Tags */}
          {product.tags && product.tags.length > 0 && (
            <div className="mt-8 flex flex-wrap gap-2">
              {product.tags.map((tag) => (
                <Link
                  key={tag}
                  href={`/products?search=${tag}`}
                  className="px-3 py-1 bg-slate-100 text-slate-600 text-sm rounded-full hover:bg-slate-200"
                >
                  #{tag}
                </Link>
              ))}
            </div>
          )}
        </div>
      </div>

      {/* Lightbox */}
      <AnimatePresence>
        {showLightbox && (
          <motion.div
            className="fixed inset-0 bg-black z-50 flex items-center justify-center"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
          >
            <button
              onClick={() => setShowLightbox(false)}
              className="absolute top-4 right-4 p-3 bg-white/10 hover:bg-white/20 rounded-full text-white"
            >
              <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>

            <button
              onClick={() => setSelectedImage(Math.max(0, selectedImage - 1))}
              disabled={selectedImage === 0}
              className="absolute left-4 p-3 bg-white/10 hover:bg-white/20 rounded-full text-white disabled:opacity-50"
            >
              <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
              </svg>
            </button>

            <div className="relative w-full max-w-4xl aspect-square mx-4">
              <Image
                src={product.images[selectedImage].imageUrl}
                alt={product.images[selectedImage].altText || product.name}
                fill
                className="object-contain"
                sizes="100vw"
              />
            </div>

            <button
              onClick={() => setSelectedImage(Math.min(product.images.length - 1, selectedImage + 1))}
              disabled={selectedImage === product.images.length - 1}
              className="absolute right-4 p-3 bg-white/10 hover:bg-white/20 rounded-full text-white disabled:opacity-50"
            >
              <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
              </svg>
            </button>

            <div className="absolute bottom-4 flex gap-2">
              {product.images.map((_, index) => (
                <button
                  key={index}
                  onClick={() => setSelectedImage(index)}
                  className={`w-2 h-2 rounded-full ${
                    selectedImage === index ? 'bg-white' : 'bg-white/50'
                  }`}
                />
              ))}
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </>
  );
}
