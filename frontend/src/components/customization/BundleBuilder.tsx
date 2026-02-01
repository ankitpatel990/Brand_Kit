'use client';

import { useState, useCallback } from 'react';
import { Plus, X, Package, Check, ShoppingCart, Trash2 } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import toast from 'react-hot-toast';
import { ProductDetail } from '@/lib/product-api';
import { CropData } from './LogoCropper';

/**
 * Bundle Builder Component
 * FRD-003 Sub-Prompt 6: Bundle Builder Workflow
 * 
 * Multi-product customization bundle feature.
 * Features:
 * - Create bundle after first product customization
 * - Add up to 10 products
 * - Use same logo with adjusted crop per product
 * - Review bundle summary
 * - Add to cart as single item
 */

export interface BundleItem {
  productId: string;
  productName: string;
  productSlug: string;
  imageUrl: string;
  customizationId?: string;
  cropData: CropData;
  croppedImageUrl: string;
  previewImageUrl: string;
  quantity: number;
  unitPrice: number;
}

interface BundleBuilderProps {
  initialItem: BundleItem;
  availableProducts: ProductDetail[];
  logoPreviewUrl: string;
  onAddProduct: (product: ProductDetail) => void;
  onRemoveProduct: (productId: string) => void;
  onUpdateQuantity: (productId: string, quantity: number) => void;
  onAddToCart: (bundleName: string, items: BundleItem[]) => void;
  onClose: () => void;
}

const MAX_BUNDLE_SIZE = 10;

export default function BundleBuilder({
  initialItem,
  availableProducts,
  logoPreviewUrl,
  onAddProduct,
  onRemoveProduct,
  onUpdateQuantity,
  onAddToCart,
  onClose,
}: BundleBuilderProps) {
  const [bundleItems, setBundleItems] = useState<BundleItem[]>([initialItem]);
  const [bundleName, setBundleName] = useState('');
  const [showProductSelector, setShowProductSelector] = useState(false);
  const [selectedProducts, setSelectedProducts] = useState<Set<string>>(new Set([initialItem.productId]));

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      minimumFractionDigits: 0,
    }).format(price);
  };

  const calculateTotal = useCallback(() => {
    return bundleItems.reduce((total, item) => {
      return total + (item.unitPrice * item.quantity);
    }, 0);
  }, [bundleItems]);

  const handleAddProduct = useCallback((product: ProductDetail) => {
    if (bundleItems.length >= MAX_BUNDLE_SIZE) {
      toast.error(`Maximum ${MAX_BUNDLE_SIZE} products per bundle`);
      return;
    }

    if (selectedProducts.has(product.productId)) {
      toast.error('Product already in bundle');
      return;
    }

    // Create placeholder bundle item - will be customized
    const newItem: BundleItem = {
      productId: product.productId,
      productName: product.name,
      productSlug: product.slug,
      imageUrl: product.images[0]?.imageUrl || '',
      cropData: initialItem.cropData, // Use same crop as starting point
      croppedImageUrl: initialItem.croppedImageUrl,
      previewImageUrl: '', // Will be generated after crop
      quantity: 1,
      unitPrice: product.basePrice,
    };

    setBundleItems(prev => [...prev, newItem]);
    setSelectedProducts(prev => new Set([...prev, product.productId]));
    setShowProductSelector(false);
    
    onAddProduct(product);
    toast.success(`${product.name} added to bundle`);
  }, [bundleItems.length, selectedProducts, initialItem, onAddProduct]);

  const handleRemoveProduct = useCallback((productId: string) => {
    if (bundleItems.length <= 1) {
      toast.error('Bundle must contain at least one product');
      return;
    }

    setBundleItems(prev => prev.filter(item => item.productId !== productId));
    setSelectedProducts(prev => {
      const newSet = new Set(prev);
      newSet.delete(productId);
      return newSet;
    });
    
    onRemoveProduct(productId);
  }, [bundleItems.length, onRemoveProduct]);

  const handleQuantityChange = useCallback((productId: string, quantity: number) => {
    if (quantity < 1 || quantity > 10000) return;

    setBundleItems(prev =>
      prev.map(item =>
        item.productId === productId
          ? { ...item, quantity }
          : item
      )
    );
    onUpdateQuantity(productId, quantity);
  }, [onUpdateQuantity]);

  const handleAddToCart = useCallback(() => {
    if (!bundleName.trim()) {
      toast.error('Please enter a bundle name');
      return;
    }

    if (bundleName.length < 5 || bundleName.length > 100) {
      toast.error('Bundle name must be 5-100 characters');
      return;
    }

    onAddToCart(bundleName, bundleItems);
  }, [bundleName, bundleItems, onAddToCart]);

  return (
    <div className="fixed inset-0 z-50 bg-black/50 flex items-center justify-center p-4">
      <motion.div
        initial={{ opacity: 0, scale: 0.95 }}
        animate={{ opacity: 1, scale: 1 }}
        exit={{ opacity: 0, scale: 0.95 }}
        className="bg-white rounded-2xl shadow-xl max-w-4xl w-full max-h-[90vh] overflow-hidden flex flex-col"
      >
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-slate-200">
          <div className="flex items-center gap-3">
            <div className="p-2 bg-purple-100 rounded-lg">
              <Package className="w-6 h-6 text-purple-600" />
            </div>
            <div>
              <h2 className="text-xl font-semibold text-slate-900">Bundle Builder</h2>
              <p className="text-sm text-slate-600">
                {bundleItems.length} of {MAX_BUNDLE_SIZE} products
              </p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-2 text-slate-400 hover:text-slate-600 hover:bg-slate-100 rounded-lg transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Content */}
        <div className="flex-1 overflow-y-auto p-6">
          {/* Bundle Name */}
          <div className="mb-6">
            <label className="block text-sm font-medium text-slate-700 mb-2">
              Bundle Name
            </label>
            <input
              type="text"
              value={bundleName}
              onChange={(e) => setBundleName(e.target.value)}
              placeholder="e.g., Employee Welcome Kit"
              className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
              maxLength={100}
            />
          </div>

          {/* Bundle Items */}
          <div className="space-y-3 mb-6">
            <div className="flex items-center justify-between">
              <h3 className="font-medium text-slate-900">Products in Bundle</h3>
              <button
                onClick={() => setShowProductSelector(true)}
                disabled={bundleItems.length >= MAX_BUNDLE_SIZE}
                className="flex items-center gap-2 px-3 py-1.5 text-sm font-medium text-purple-600 hover:bg-purple-50 rounded-lg disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
              >
                <Plus className="w-4 h-4" />
                Add Product
              </button>
            </div>

            <AnimatePresence>
              {bundleItems.map((item, index) => (
                <motion.div
                  key={item.productId}
                  initial={{ opacity: 0, y: 10 }}
                  animate={{ opacity: 1, y: 0 }}
                  exit={{ opacity: 0, x: -100 }}
                  className="flex items-center gap-4 p-4 bg-slate-50 rounded-lg"
                >
                  {/* Product Image/Preview */}
                  <div className="w-16 h-16 flex-shrink-0">
                    {item.previewImageUrl ? (
                      <img
                        src={item.previewImageUrl}
                        alt={item.productName}
                        className="w-full h-full object-cover rounded-lg"
                      />
                    ) : (
                      <img
                        src={item.imageUrl}
                        alt={item.productName}
                        className="w-full h-full object-cover rounded-lg"
                      />
                    )}
                  </div>

                  {/* Product Info */}
                  <div className="flex-1 min-w-0">
                    <p className="font-medium text-slate-900 truncate">{item.productName}</p>
                    <p className="text-sm text-slate-600">{formatPrice(item.unitPrice)}/unit</p>
                  </div>

                  {/* Quantity */}
                  <div className="flex items-center gap-2">
                    <button
                      onClick={() => handleQuantityChange(item.productId, item.quantity - 1)}
                      disabled={item.quantity <= 1}
                      className="p-1 text-slate-400 hover:text-slate-600 disabled:opacity-50"
                    >
                      <span className="text-lg font-bold">−</span>
                    </button>
                    <input
                      type="number"
                      value={item.quantity}
                      onChange={(e) => handleQuantityChange(item.productId, parseInt(e.target.value) || 1)}
                      min={1}
                      max={10000}
                      className="w-16 px-2 py-1 text-center border border-slate-300 rounded"
                    />
                    <button
                      onClick={() => handleQuantityChange(item.productId, item.quantity + 1)}
                      disabled={item.quantity >= 10000}
                      className="p-1 text-slate-400 hover:text-slate-600 disabled:opacity-50"
                    >
                      <span className="text-lg font-bold">+</span>
                    </button>
                  </div>

                  {/* Subtotal */}
                  <div className="w-24 text-right">
                    <p className="font-semibold text-slate-900">
                      {formatPrice(item.unitPrice * item.quantity)}
                    </p>
                  </div>

                  {/* Remove */}
                  <button
                    onClick={() => handleRemoveProduct(item.productId)}
                    disabled={bundleItems.length <= 1}
                    className="p-2 text-slate-400 hover:text-red-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                </motion.div>
              ))}
            </AnimatePresence>
          </div>

          {/* Summary */}
          <div className="bg-purple-50 rounded-lg p-4">
            <div className="flex items-center justify-between text-lg font-semibold">
              <span className="text-purple-900">Total Bundle Price</span>
              <span className="text-purple-900">{formatPrice(calculateTotal())}</span>
            </div>
            <p className="text-sm text-purple-700 mt-1">
              {bundleItems.reduce((sum, item) => sum + item.quantity, 0)} items total
            </p>
          </div>
        </div>

        {/* Footer */}
        <div className="px-6 py-4 border-t border-slate-200 flex items-center gap-3">
          <button
            onClick={onClose}
            className="px-4 py-2 text-sm font-medium text-slate-700 bg-white border border-slate-300 rounded-lg hover:bg-slate-50 transition-colors"
          >
            Cancel
          </button>
          <button
            onClick={handleAddToCart}
            disabled={!bundleName.trim() || bundleItems.length < 1}
            className="flex-1 flex items-center justify-center gap-2 px-6 py-2 text-sm font-medium text-white bg-purple-600 rounded-lg hover:bg-purple-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            <ShoppingCart className="w-4 h-4" />
            Add Bundle to Cart
          </button>
        </div>

        {/* Product Selector Modal */}
        <AnimatePresence>
          {showProductSelector && (
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              className="absolute inset-0 bg-white flex flex-col"
            >
              <div className="flex items-center justify-between px-6 py-4 border-b border-slate-200">
                <h3 className="text-lg font-semibold text-slate-900">Select Products</h3>
                <button
                  onClick={() => setShowProductSelector(false)}
                  className="p-2 text-slate-400 hover:text-slate-600 hover:bg-slate-100 rounded-lg"
                >
                  <X className="w-5 h-5" />
                </button>
              </div>

              <div className="flex-1 overflow-y-auto p-6">
                <p className="text-sm text-slate-600 mb-4">
                  Select products to add to your bundle. Same logo will be applied.
                </p>

                <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
                  {availableProducts
                    .filter(p => p.customizable && !selectedProducts.has(p.productId))
                    .map(product => (
                      <button
                        key={product.productId}
                        onClick={() => handleAddProduct(product)}
                        className="p-4 border border-slate-200 rounded-lg hover:border-purple-500 hover:bg-purple-50 transition-colors text-left"
                      >
                        <img
                          src={product.images[0]?.imageUrl || ''}
                          alt={product.name}
                          className="w-full h-24 object-cover rounded-lg mb-2"
                        />
                        <p className="font-medium text-slate-900 text-sm truncate">
                          {product.name}
                        </p>
                        <p className="text-xs text-slate-600">
                          {formatPrice(product.basePrice)}
                        </p>
                      </button>
                    ))}
                </div>

                {availableProducts.filter(p => p.customizable && !selectedProducts.has(p.productId)).length === 0 && (
                  <div className="text-center py-8 text-slate-500">
                    No more customizable products available
                  </div>
                )}
              </div>
            </motion.div>
          )}
        </AnimatePresence>
      </motion.div>
    </div>
  );
}
