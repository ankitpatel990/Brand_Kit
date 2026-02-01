'use client';

import { useState, useEffect, useCallback } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { ArrowLeft, Save, ShoppingCart, Package, Layers } from 'lucide-react';
import Link from 'next/link';
import toast from 'react-hot-toast';
import { productApi, ProductDetail } from '@/lib/product-api';
import LogoUpload, { LogoFile } from '@/components/customization/LogoUpload';
import LogoCropper, { CropData } from '@/components/customization/LogoCropper';
import PreviewRenderer, { PreviewData } from '@/components/customization/PreviewRenderer';
import DownloadPreview from '@/components/customization/DownloadPreview';
import CustomizationValidator, { validateCustomization } from '@/components/customization/CustomizationValidator';
import BundleBuilder, { BundleItem } from '@/components/customization/BundleBuilder';
import MultiProductApply from '@/components/customization/MultiProductApply';
import { useAuth } from '@/lib/auth-context';
import { Button } from '@/components/ui/Button';
import { customizationApi } from '@/lib/customization-api';

/**
 * Customization Page
 * FRD-003: Main customization workflow page
 * Integrates all customization components
 */

type CustomizationStep = 'upload' | 'crop' | 'preview';

export default function CustomizePage() {
  const params = useParams();
  const router = useRouter();
  const { isAuthenticated } = useAuth();
  const productSlug = params.productSlug as string;

  const [product, setProduct] = useState<ProductDetail | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Customization state
  const [step, setStep] = useState<CustomizationStep>('upload');
  const [logo, setLogo] = useState<LogoFile | null>(null);
  const [cropData, setCropData] = useState<CropData | null>(null);
  const [croppedImageUrl, setCroppedImageUrl] = useState<string | null>(null);
  const [previewData, setPreviewData] = useState<PreviewData | null>(null);
  
  // Bundle and multi-product states
  const [showBundleBuilder, setShowBundleBuilder] = useState(false);
  const [showMultiProductApply, setShowMultiProductApply] = useState(false);
  const [availableProducts, setAvailableProducts] = useState<ProductDetail[]>([]);

  // Load product
  useEffect(() => {
    const loadProduct = async () => {
      setIsLoading(true);
      setError(null);

      try {
        const response = await productApi.getProduct(productSlug);
        const productData = response.data;

        if (!productData.customizable) {
          setError('This product does not support customization');
          return;
        }

        if (!productData.printArea) {
          setError('Print area not configured for this product');
          return;
        }

        setProduct(productData);
      } catch (err: any) {
        console.error('Failed to load product:', err);
        if (err.response?.status === 404) {
          setError('Product not found');
        } else {
          setError('Unable to load product. Please try again.');
        }
      } finally {
        setIsLoading(false);
      }
    };

    loadProduct();
  }, [productSlug]);

  // Handle logo upload
  const handleLogoUpload = useCallback((uploadedLogo: LogoFile | null) => {
    if (!uploadedLogo) {
      setLogo(null);
      setCropData(null);
      setCroppedImageUrl(null);
      setPreviewData(null);
      setStep('upload');
      return;
    }

    setLogo(uploadedLogo);
    setStep('crop');
  }, []);

  // Handle crop complete
  const handleCropComplete = useCallback(
    (data: CropData, croppedImage: string) => {
      setCropData(data);
      setCroppedImageUrl(croppedImage);

      if (!product || !product.printArea) {
        return;
      }

      // Create preview data
      const preview: PreviewData = {
        productImageUrl: product.images[0]?.imageUrl || '',
        logoImageUrl: croppedImage,
        printArea: {
          x: product.printArea.x || 0,
          y: product.printArea.y || 0,
          width: product.printArea.width || 0,
          height: product.printArea.height || 0,
        },
        cropData: {
          width: data.width,
          height: data.height,
        },
      };

      setPreviewData(preview);
      setStep('preview');
    },
    [product]
  );

  // Handle adjust crop
  const handleAdjustCrop = useCallback(() => {
    setStep('crop');
  }, []);

  // Handle save draft
  const handleSaveDraft = useCallback(async () => {
    if (!isAuthenticated) {
      toast.error('Please log in to save draft');
      router.push(`/auth/login?redirect=/customize/${productSlug}`);
      return;
    }

    if (!logo || !cropData || !previewData || !product) {
      toast.error('Please complete customization before saving');
      return;
    }

    try {
      await customizationApi.saveDraft({
        productId: product.productId,
        logoFileUrl: logo.preview,
        logoFileName: logo.file.name,
        logoFileSize: logo.file.size,
        logoDimensions: logo.width > 0 && logo.height > 0
          ? { width: logo.width, height: logo.height }
          : undefined,
        cropData: cropData,
        croppedImageUrl: croppedImageUrl || '',
        previewImageUrl: previewData.logoImageUrl,
      });
      toast.success('Draft saved successfully');
    } catch (err: any) {
      console.error('Failed to save draft:', err);
      toast.error(err.response?.data?.message || 'Failed to save draft. Please try again.');
    }
  }, [isAuthenticated, logo, cropData, previewData, product, productSlug, router, croppedImageUrl]);

  // Handle add to cart
  const handleAddToCart = useCallback(async () => {
    if (!logo || !cropData || !previewData || !product) {
      toast.error('Please complete customization before adding to cart');
      return;
    }

    try {
      // TODO: Implement add to cart API call with customization data
      toast.success('Added to cart!');
      router.push('/products');
    } catch (err: any) {
      console.error('Failed to add to cart:', err);
      toast.error('Failed to add to cart. Please try again.');
    }
  }, [logo, cropData, previewData, product, router]);

  // Calculate aspect ratio from print area
  const getAspectRatio = useCallback((): number => {
    if (!product?.printArea) return 1;

    const width = product.printArea.width || 1;
    const height = product.printArea.height || 1;
    return width / height;
  }, [product]);

  // Get validation result
  const validation = validateCustomization(
    logo,
    cropData,
    step === 'preview' && previewData !== null,
    product?.printArea
  );

  // Handle create bundle
  const handleCreateBundle = useCallback(() => {
    if (!product || !logo || !cropData || !croppedImageUrl) {
      toast.error('Please complete customization first');
      return;
    }
    setShowBundleBuilder(true);
  }, [product, logo, cropData, croppedImageUrl]);

  // Handle apply to more products
  const handleApplyToMore = useCallback(() => {
    if (!product || !logo || !cropData) {
      toast.error('Please complete customization first');
      return;
    }
    setShowMultiProductApply(true);
  }, [product, logo, cropData]);

  // Create initial bundle item from current customization
  const createBundleItem = useCallback((): BundleItem | null => {
    if (!product || !cropData || !croppedImageUrl) return null;

    return {
      productId: product.productId,
      productName: product.name,
      productSlug: product.slug,
      imageUrl: product.images[0]?.imageUrl || '',
      cropData: cropData,
      croppedImageUrl: croppedImageUrl,
      previewImageUrl: previewData?.logoImageUrl || '',
      quantity: 1,
      unitPrice: product.basePrice,
    };
  }, [product, cropData, croppedImageUrl, previewData]);

  if (isLoading) {
    return (
      <div className="min-h-screen bg-slate-50 flex items-center justify-center">
        <div className="text-center space-y-4">
          <div className="w-12 h-12 border-4 border-purple-600 border-t-transparent rounded-full animate-spin mx-auto" />
          <p className="text-slate-600">Loading product...</p>
        </div>
      </div>
    );
  }

  if (error || !product) {
    return (
      <div className="min-h-screen bg-slate-50 flex items-center justify-center">
        <div className="text-center space-y-4 max-w-md">
          <p className="text-lg font-semibold text-slate-900">{error || 'Product not found'}</p>
          <Link href="/products">
            <Button>Return to Products</Button>
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-slate-50">
      {/* Header */}
      <div className="bg-white border-b border-slate-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
          <div className="flex items-center gap-4">
            <Link href={`/products/${productSlug}`}>
              <button className="p-2 hover:bg-slate-100 rounded-lg transition-colors">
                <ArrowLeft className="w-5 h-5 text-slate-700" />
              </button>
            </Link>
            <div className="flex-1">
              <h1 className="text-xl font-semibold text-slate-900">{product.name}</h1>
              <p className="text-sm text-slate-600">Customize with your logo</p>
            </div>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="grid lg:grid-cols-3 gap-8">
          {/* Left Column - Customization Steps */}
          <div className="lg:col-span-2 space-y-6">
            {/* Step Indicator */}
            <div className="flex items-center gap-2">
              {['upload', 'crop', 'preview'].map((s, index) => (
                <div key={s} className="flex items-center flex-1">
                  <div
                    className={`flex items-center justify-center w-8 h-8 rounded-full font-medium text-sm ${
                      step === s
                        ? 'bg-purple-600 text-white'
                        : ['upload', 'crop', 'preview'].indexOf(step) > index
                        ? 'bg-green-500 text-white'
                        : 'bg-slate-200 text-slate-600'
                    }`}
                  >
                    {index + 1}
                  </div>
                  {index < 2 && (
                    <div
                      className={`flex-1 h-1 mx-2 ${
                        ['upload', 'crop', 'preview'].indexOf(step) > index
                          ? 'bg-green-500'
                          : 'bg-slate-200'
                      }`}
                    />
                  )}
                </div>
              ))}
            </div>

            {/* Step Content */}
            {step === 'upload' && (
              <div className="bg-white rounded-lg shadow-sm p-6">
                <LogoUpload
                  onUpload={handleLogoUpload}
                  existingLogo={logo}
                />
              </div>
            )}

            {step === 'crop' && logo && (
              <div className="bg-white rounded-lg shadow-sm p-6">
                <LogoCropper
                  logo={logo}
                  aspectRatio={getAspectRatio()}
                  onCropComplete={handleCropComplete}
                  onCancel={() => setStep('upload')}
                />
              </div>
            )}

            {step === 'preview' && previewData && (
              <div className="bg-white rounded-lg shadow-sm p-6">
                <PreviewRenderer
                  previewData={previewData}
                  productName={product.name}
                  onRegenerate={handleAdjustCrop}
                />
              </div>
            )}
          </div>

          {/* Right Column - Actions & Info */}
          <div className="lg:col-span-1 space-y-6">
            {/* Product Info Card */}
            <div className="bg-white rounded-lg shadow-sm p-6">
              <h3 className="font-semibold text-slate-900 mb-4">Product Details</h3>
              <div className="space-y-3">
                <div>
                  <p className="text-sm text-slate-600">Product</p>
                  <p className="font-medium text-slate-900">{product.name}</p>
                </div>
                {product.printArea && (
                  <div>
                    <p className="text-sm text-slate-600">Print Area</p>
                    <p className="font-medium text-slate-900">
                      {product.printArea.width}cm × {product.printArea.height}cm
                    </p>
                  </div>
                )}
                {product.customizationType && (
                  <div>
                    <p className="text-sm text-slate-600">Customization Type</p>
                    <p className="font-medium text-slate-900">{product.customizationType}</p>
                  </div>
                )}
              </div>
            </div>

            {/* Validation Status */}
            <div className="bg-white rounded-lg shadow-sm p-6">
              <CustomizationValidator
                logo={logo}
                cropData={cropData}
                previewGenerated={step === 'preview' && previewData !== null}
                printArea={product.printArea}
              />
            </div>

            {/* Action Buttons */}
            {step === 'preview' && (
              <div className="bg-white rounded-lg shadow-sm p-6 space-y-3">
                <Button
                  onClick={handleAddToCart}
                  className="w-full"
                  disabled={!validation.isValid}
                  leftIcon={<ShoppingCart className="w-5 h-5" />}
                >
                  Add to Cart
                </Button>
                
                {/* Download Preview */}
                {previewData && (
                  <DownloadPreview
                    previewImageUrl={previewData.logoImageUrl}
                    productName={product.name}
                  />
                )}
                
                <Button
                  onClick={handleSaveDraft}
                  variant="outline"
                  className="w-full"
                  leftIcon={<Save className="w-5 h-5" />}
                >
                  Save Draft
                </Button>
                
                {/* Bundle and Multi-Product Options */}
                <div className="pt-3 border-t border-slate-200 space-y-2">
                  <Button
                    onClick={handleCreateBundle}
                    variant="outline"
                    className="w-full"
                    leftIcon={<Package className="w-5 h-5" />}
                  >
                    Create Bundle
                  </Button>
                  <Button
                    onClick={handleApplyToMore}
                    variant="ghost"
                    className="w-full"
                    leftIcon={<Layers className="w-5 h-5" />}
                  >
                    Apply to More Products
                  </Button>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Bundle Builder Modal */}
      {showBundleBuilder && createBundleItem() && (
        <BundleBuilder
          initialItem={createBundleItem()!}
          availableProducts={availableProducts}
          logoPreviewUrl={logo?.preview || ''}
          onAddProduct={(p) => console.log('Add product:', p)}
          onRemoveProduct={(id) => console.log('Remove product:', id)}
          onUpdateQuantity={(id, qty) => console.log('Update qty:', id, qty)}
          onAddToCart={(name, items) => {
            console.log('Add bundle to cart:', name, items);
            toast.success('Bundle added to cart!');
            setShowBundleBuilder(false);
          }}
          onClose={() => setShowBundleBuilder(false)}
        />
      )}

      {/* Multi-Product Apply Modal */}
      {showMultiProductApply && product && cropData && logo && (
        <MultiProductApply
          sourceProduct={product}
          sourceCropData={cropData}
          logoImageUrl={logo.preview}
          availableProducts={availableProducts}
          onApply={(applications) => {
            console.log('Applied to products:', applications);
            toast.success(`Logo applied to ${applications.length} products`);
            setShowMultiProductApply(false);
          }}
          onClose={() => setShowMultiProductApply(false)}
        />
      )}
    </div>
  );
}
