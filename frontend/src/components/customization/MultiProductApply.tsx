'use client';

import { useState, useCallback } from 'react';
import { Layers, Check, X, ChevronLeft, ChevronRight, AlertCircle } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import toast from 'react-hot-toast';
import { ProductDetail } from '@/lib/product-api';
import { CropData } from './LogoCropper';

/**
 * Multi-Product Logo Application Component
 * FRD-003 Sub-Prompt 9: Multi-Product Logo Application
 * 
 * Apply logo to multiple products with auto-crop adjustment.
 * Features:
 * - Product selection modal with multi-select
 * - Auto-apply logo with adjusted crop per product aspect ratio
 * - Manual crop prompt for significantly different aspect ratios
 * - Preview carousel for all products
 * - Accept or adjust individual crops
 */

interface ProductApplication {
  product: ProductDetail;
  cropData: CropData | null;
  previewUrl: string | null;
  needsManualCrop: boolean;
  isApplied: boolean;
}

interface MultiProductApplyProps {
  sourceProduct: ProductDetail;
  sourceCropData: CropData;
  logoImageUrl: string;
  availableProducts: ProductDetail[];
  onApply: (applications: ProductApplication[]) => void;
  onClose: () => void;
}

const ASPECT_RATIO_THRESHOLD = 0.3; // 30% difference triggers manual crop

export default function MultiProductApply({
  sourceProduct,
  sourceCropData,
  logoImageUrl,
  availableProducts,
  onApply,
  onClose,
}: MultiProductApplyProps) {
  const [step, setStep] = useState<'select' | 'preview'>('select');
  const [selectedProducts, setSelectedProducts] = useState<Set<string>>(new Set());
  const [applications, setApplications] = useState<ProductApplication[]>([]);
  const [currentPreviewIndex, setCurrentPreviewIndex] = useState(0);
  const [isProcessing, setIsProcessing] = useState(false);

  const calculateAspectRatio = (product: ProductDetail): number => {
    if (!product.printArea) return 1;
    const width = product.printArea.width || 1;
    const height = product.printArea.height || 1;
    return width / height;
  };

  const sourceAspectRatio = calculateAspectRatio(sourceProduct);

  const toggleProduct = useCallback((productId: string) => {
    setSelectedProducts(prev => {
      const newSet = new Set(prev);
      if (newSet.has(productId)) {
        newSet.delete(productId);
      } else {
        newSet.add(productId);
      }
      return newSet;
    });
  }, []);

  const getProductId = (product: ProductDetail) => product.productId;

  const handleApplyLogo = useCallback(async () => {
    if (selectedProducts.size === 0) {
      toast.error('Please select at least one product');
      return;
    }

    setIsProcessing(true);

    try {
      const apps: ProductApplication[] = [];

      for (const productId of selectedProducts) {
        const product = availableProducts.find(p => p.productId === productId);
        if (!product) continue;

        const productAspectRatio = calculateAspectRatio(product);
        const aspectDiff = Math.abs(productAspectRatio - sourceAspectRatio) / sourceAspectRatio;

        // Check if aspect ratios differ significantly
        const needsManualCrop = aspectDiff > ASPECT_RATIO_THRESHOLD;

        // If aspect ratios are similar, auto-apply
        let cropData: CropData | null = null;
        let previewUrl: string | null = null;

        if (!needsManualCrop) {
          // Adjust crop data for new aspect ratio
          cropData = {
            ...sourceCropData,
            aspectRatio: productAspectRatio,
          };
          // TODO: Generate preview using Canvas
          previewUrl = logoImageUrl; // Placeholder
        }

        apps.push({
          product,
          cropData,
          previewUrl,
          needsManualCrop,
          isApplied: !needsManualCrop,
        });
      }

      setApplications(apps);
      setStep('preview');
    } catch (error) {
      console.error('Failed to apply logo:', error);
      toast.error('Failed to process. Please try again.');
    } finally {
      setIsProcessing(false);
    }
  }, [selectedProducts, availableProducts, sourceAspectRatio, sourceCropData, logoImageUrl]);

  const handleAcceptAll = useCallback(() => {
    const allApplied = applications.every(app => app.isApplied);
    if (!allApplied) {
      toast.error('Please complete manual crop for all products');
      return;
    }

    onApply(applications);
    toast.success(`Logo applied to ${applications.length} products`);
    onClose();
  }, [applications, onApply, onClose]);

  const navigatePreview = useCallback((direction: 'prev' | 'next') => {
    setCurrentPreviewIndex(prev => {
      if (direction === 'prev') {
        return prev > 0 ? prev - 1 : applications.length - 1;
      } else {
        return prev < applications.length - 1 ? prev + 1 : 0;
      }
    });
  }, [applications.length]);

  const currentApplication = applications[currentPreviewIndex];

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
            <div className="p-2 bg-blue-100 rounded-lg">
              <Layers className="w-6 h-6 text-blue-600" />
            </div>
            <div>
              <h2 className="text-xl font-semibold text-slate-900">
                {step === 'select' ? 'Apply to More Products' : 'Review Previews'}
              </h2>
              <p className="text-sm text-slate-600">
                {step === 'select'
                  ? 'Select products to apply the same logo'
                  : `${currentPreviewIndex + 1} of ${applications.length} products`}
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
          {step === 'select' && (
            <div className="space-y-4">
              <p className="text-sm text-slate-600">
                Select products to apply your logo. Products with similar print area aspect ratios
                will be auto-cropped; others may require manual adjustment.
              </p>

              <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
                {availableProducts
                  .filter(p => p.customizable && p.productId !== sourceProduct.productId)
                  .map(product => {
                    const isSelected = selectedProducts.has(product.productId);
                    const productAspectRatio = calculateAspectRatio(product);
                    const aspectDiff = Math.abs(productAspectRatio - sourceAspectRatio) / sourceAspectRatio;
                    const willNeedManualCrop = aspectDiff > ASPECT_RATIO_THRESHOLD;

                    return (
                      <button
                        key={product.productId}
                        onClick={() => toggleProduct(product.productId)}
                        className={`relative p-4 border-2 rounded-lg transition-colors text-left ${
                          isSelected
                            ? 'border-blue-500 bg-blue-50'
                            : 'border-slate-200 hover:border-slate-300'
                        }`}
                      >
                        {isSelected && (
                          <div className="absolute top-2 right-2 w-6 h-6 bg-blue-500 rounded-full flex items-center justify-center">
                            <Check className="w-4 h-4 text-white" />
                          </div>
                        )}

                        <img
                          src={product.images[0]?.imageUrl || ''}
                          alt={product.name}
                          className="w-full h-24 object-cover rounded-lg mb-2"
                        />
                        <p className="font-medium text-slate-900 text-sm truncate">
                          {product.name}
                        </p>
                        <p className="text-xs text-slate-600">
                          {product.printArea
                            ? `${product.printArea.width}×${product.printArea.height} cm`
                            : 'No print area'}
                        </p>

                        {willNeedManualCrop && (
                          <div className="mt-2 flex items-center gap-1 text-xs text-amber-600">
                            <AlertCircle className="w-3 h-3" />
                            Manual crop needed
                          </div>
                        )}
                      </button>
                    );
                  })}
              </div>

              {availableProducts.filter(p => p.customizable && p.productId !== sourceProduct.productId).length === 0 && (
                <div className="text-center py-12 text-slate-500">
                  No other customizable products available
                </div>
              )}
            </div>
          )}

          {step === 'preview' && currentApplication && (
            <div className="space-y-6">
              {/* Preview Carousel */}
              <div className="relative">
                <div className="aspect-square bg-slate-100 rounded-lg overflow-hidden">
                  {currentApplication.previewUrl ? (
                    <img
                      src={currentApplication.previewUrl}
                      alt={currentApplication.product.name}
                      className="w-full h-full object-contain"
                    />
                  ) : (
                    <div className="w-full h-full flex items-center justify-center">
                      <div className="text-center p-8">
                        <AlertCircle className="w-12 h-12 text-amber-500 mx-auto mb-4" />
                        <p className="font-medium text-slate-900">Manual Crop Required</p>
                        <p className="text-sm text-slate-600 mt-1">
                          This product has a different aspect ratio and needs manual adjustment
                        </p>
                      </div>
                    </div>
                  )}
                </div>

                {/* Navigation Arrows */}
                {applications.length > 1 && (
                  <>
                    <button
                      onClick={() => navigatePreview('prev')}
                      className="absolute left-2 top-1/2 -translate-y-1/2 p-2 bg-white/90 rounded-full shadow hover:bg-white transition-colors"
                    >
                      <ChevronLeft className="w-5 h-5 text-slate-700" />
                    </button>
                    <button
                      onClick={() => navigatePreview('next')}
                      className="absolute right-2 top-1/2 -translate-y-1/2 p-2 bg-white/90 rounded-full shadow hover:bg-white transition-colors"
                    >
                      <ChevronRight className="w-5 h-5 text-slate-700" />
                    </button>
                  </>
                )}
              </div>

              {/* Product Info */}
              <div className="bg-slate-50 rounded-lg p-4">
                <h3 className="font-medium text-slate-900">{currentApplication.product.name}</h3>
                <p className="text-sm text-slate-600 mt-1">
                  Print area: {currentApplication.product.printArea?.width}×
                  {currentApplication.product.printArea?.height} cm
                </p>
                <div className="mt-2">
                  {currentApplication.isApplied ? (
                    <span className="inline-flex items-center gap-1 text-sm text-green-600">
                      <Check className="w-4 h-4" />
                      Logo applied
                    </span>
                  ) : (
                    <span className="inline-flex items-center gap-1 text-sm text-amber-600">
                      <AlertCircle className="w-4 h-4" />
                      Needs manual crop
                    </span>
                  )}
                </div>
              </div>

              {/* Preview Dots */}
              <div className="flex items-center justify-center gap-2">
                {applications.map((app, index) => (
                  <button
                    key={app.product.id}
                    onClick={() => setCurrentPreviewIndex(index)}
                    className={`w-3 h-3 rounded-full transition-colors ${
                      index === currentPreviewIndex
                        ? 'bg-blue-600'
                        : app.isApplied
                        ? 'bg-green-400'
                        : 'bg-amber-400'
                    }`}
                  />
                ))}
              </div>
            </div>
          )}
        </div>

        {/* Footer */}
        <div className="px-6 py-4 border-t border-slate-200 flex items-center gap-3">
          {step === 'select' ? (
            <>
              <button
                onClick={onClose}
                className="px-4 py-2 text-sm font-medium text-slate-700 bg-white border border-slate-300 rounded-lg hover:bg-slate-50 transition-colors"
              >
                Cancel
              </button>
              <button
                onClick={handleApplyLogo}
                disabled={selectedProducts.size === 0 || isProcessing}
                className="flex-1 flex items-center justify-center gap-2 px-6 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
              >
                {isProcessing ? (
                  'Processing...'
                ) : (
                  <>
                    Apply Logo ({selectedProducts.size})
                  </>
                )}
              </button>
            </>
          ) : (
            <>
              <button
                onClick={() => setStep('select')}
                className="px-4 py-2 text-sm font-medium text-slate-700 bg-white border border-slate-300 rounded-lg hover:bg-slate-50 transition-colors"
              >
                Back
              </button>
              <button
                onClick={handleAcceptAll}
                disabled={!applications.every(app => app.isApplied)}
                className="flex-1 flex items-center justify-center gap-2 px-6 py-2 text-sm font-medium text-white bg-green-600 rounded-lg hover:bg-green-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
              >
                <Check className="w-4 h-4" />
                Accept All & Add to Bundle
              </button>
            </>
          )}
        </div>
      </motion.div>
    </div>
  );
}
