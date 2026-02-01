'use client';

import { useState, useRef, useEffect, useCallback } from 'react';
import { ZoomIn, ZoomOut, Download, RefreshCw, X } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import toast from 'react-hot-toast';

/**
 * Client-Side Preview Rendering (HTML5 Canvas)
 * FRD-003 Sub-Prompt 3: Client-Side Preview Rendering (HTML5 Canvas)
 * 
 * Features:
 * - Load product base image
 * - Overlay cropped logo at print area coordinates
 * - Canvas rendering (800×800px for preview)
 * - Display preview immediately after crop
 * - Zoom option for detail inspection
 * - Performance: Render within 500ms
 */

export interface PreviewData {
  productImageUrl: string;
  logoImageUrl: string;
  printArea: {
    x: number;
    y: number;
    width: number;
    height: number;
  };
  cropData: {
    width: number;
    height: number;
  };
}

interface PreviewRendererProps {
  previewData: PreviewData;
  productName: string;
  onRegenerate?: () => void;
  onDownload?: (imageUrl: string) => void;
}

const PREVIEW_SIZE = 800;
const ZOOM_LEVELS = [1, 1.5, 2, 2.5, 3];

export default function PreviewRenderer({
  previewData,
  productName,
  onRegenerate,
  onDownload,
}: PreviewRendererProps) {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const [previewUrl, setPreviewUrl] = useState<string | null>(null);
  const [isRendering, setIsRendering] = useState(false);
  const [renderTime, setRenderTime] = useState<number | null>(null);
  const [zoomLevel, setZoomLevel] = useState(1);
  const [showZoomed, setShowZoomed] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const loadImage = (url: string): Promise<HTMLImageElement> => {
    return new Promise((resolve, reject) => {
      const img = new Image();
      img.crossOrigin = 'anonymous';
      img.onload = () => resolve(img);
      img.onerror = () => reject(new Error(`Failed to load image: ${url}`));
      img.src = url;
    });
  };

  const renderPreview = useCallback(async () => {
    if (!canvasRef.current) return;

    setIsRendering(true);
    setError(null);
    const startTime = performance.now();

    try {
      const canvas = canvasRef.current;
      const ctx = canvas.getContext('2d');

      if (!ctx) {
        throw new Error('Failed to get canvas context');
      }

      // Set canvas size
      canvas.width = PREVIEW_SIZE;
      canvas.height = PREVIEW_SIZE;

      // Clear canvas
      ctx.clearRect(0, 0, canvas.width, canvas.height);

      // Load product base image
      const productImage = await loadImage(previewData.productImageUrl);

      // Calculate scaling factor to fit product image in preview size
      const scale = Math.min(
        PREVIEW_SIZE / productImage.width,
        PREVIEW_SIZE / productImage.height
      );

      const scaledWidth = productImage.width * scale;
      const scaledHeight = productImage.height * scale;
      const offsetX = (PREVIEW_SIZE - scaledWidth) / 2;
      const offsetY = (PREVIEW_SIZE - scaledHeight) / 2;

      // Draw product base image
      ctx.drawImage(
        productImage,
        offsetX,
        offsetY,
        scaledWidth,
        scaledHeight
      );

      // Load logo image
      const logoImage = await loadImage(previewData.logoImageUrl);

      // Calculate print area position and size in preview coordinates
      const printAreaX = offsetX + (previewData.printArea.x * scale);
      const printAreaY = offsetY + (previewData.printArea.y * scale);
      const printAreaWidth = previewData.printArea.width * scale;
      const printAreaHeight = previewData.printArea.height * scale;

      // Calculate logo size to fit print area
      const logoAspectRatio = logoImage.width / logoImage.height;
      const printAreaAspectRatio = printAreaWidth / printAreaHeight;

      let logoWidth, logoHeight;
      if (logoAspectRatio > printAreaAspectRatio) {
        // Logo is wider - fit to width
        logoWidth = printAreaWidth;
        logoHeight = printAreaWidth / logoAspectRatio;
      } else {
        // Logo is taller - fit to height
        logoHeight = printAreaHeight;
        logoWidth = printAreaHeight * logoAspectRatio;
      }

      // Center logo in print area
      const logoX = printAreaX + (printAreaWidth - logoWidth) / 2;
      const logoY = printAreaY + (printAreaHeight - logoHeight) / 2;

      // Draw logo on product
      ctx.drawImage(logoImage, logoX, logoY, logoWidth, logoHeight);

      // Convert canvas to image URL
      const dataUrl = canvas.toDataURL('image/png', 1.0);
      setPreviewUrl(dataUrl);

      const endTime = performance.now();
      const renderTimeMs = endTime - startTime;
      setRenderTime(renderTimeMs);

      if (renderTimeMs > 500) {
        console.warn(`Preview rendering took ${renderTimeMs.toFixed(0)}ms (target: <500ms)`);
      }
    } catch (err: any) {
      console.error('Preview rendering error:', err);
      setError(err.message || 'Failed to generate preview. Please try again.');
      toast.error('Failed to generate preview');
    } finally {
      setIsRendering(false);
    }
  }, [previewData]);

  useEffect(() => {
    renderPreview();
  }, [renderPreview]);

  const handleDownload = useCallback(() => {
    if (!previewUrl) return;

    if (onDownload) {
      onDownload(previewUrl);
    } else {
      // Default download behavior
      const link = document.createElement('a');
      link.download = `${productName}_Customized_${new Date().toISOString().split('T')[0]}.png`;
      link.href = previewUrl;
      link.click();
    }
  }, [previewUrl, productName, onDownload]);

  const handleZoom = useCallback((level: number) => {
    setZoomLevel(level);
    setShowZoomed(true);
  }, []);

  const handleRegenerate = useCallback(() => {
    renderPreview();
    if (onRegenerate) {
      onRegenerate();
    }
  }, [renderPreview, onRegenerate]);

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <div>
          <h3 className="text-lg font-semibold text-slate-900 mb-1">Preview</h3>
          {renderTime !== null && (
            <p className="text-xs text-slate-500">
              Rendered in {renderTime.toFixed(0)}ms
            </p>
          )}
        </div>
        <div className="flex items-center gap-2">
          {onRegenerate && (
            <button
              onClick={handleRegenerate}
              disabled={isRendering}
              className="p-2 text-slate-600 hover:text-slate-900 hover:bg-slate-100 rounded-lg transition-colors disabled:opacity-50"
              aria-label="Regenerate preview"
            >
              <RefreshCw className={`w-5 h-5 ${isRendering ? 'animate-spin' : ''}`} />
            </button>
          )}
          <button
            onClick={handleDownload}
            disabled={!previewUrl || isRendering}
            className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-white bg-purple-600 rounded-lg hover:bg-purple-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            <Download className="w-4 h-4" />
            Download Preview
          </button>
        </div>
      </div>

      {/* Preview Display */}
      <div className="relative bg-slate-100 rounded-lg overflow-hidden">
        <canvas ref={canvasRef} className="hidden" />

        {isRendering ? (
          <div className="aspect-square flex items-center justify-center">
            <div className="text-center space-y-3">
              <div className="w-12 h-12 border-4 border-purple-600 border-t-transparent rounded-full animate-spin mx-auto" />
              <p className="text-sm text-slate-600">Generating preview...</p>
            </div>
          </div>
        ) : error ? (
          <div className="aspect-square flex items-center justify-center p-8">
            <div className="text-center space-y-3">
              <p className="text-sm text-red-600">{error}</p>
              <button
                onClick={handleRegenerate}
                className="text-sm text-purple-600 hover:text-purple-700 font-medium"
              >
                Try again
              </button>
            </div>
          </div>
        ) : previewUrl ? (
          <div className="relative aspect-square group">
            <img
              src={previewUrl}
              alt={`${productName} with custom logo`}
              className="w-full h-full object-contain"
            />
            <div className="absolute inset-0 bg-black/0 group-hover:bg-black/5 transition-colors flex items-center justify-center opacity-0 group-hover:opacity-100">
              <button
                onClick={() => handleZoom(2)}
                className="p-3 bg-white/90 rounded-full shadow-lg hover:bg-white transition-colors"
                aria-label="Zoom preview"
              >
                <ZoomIn className="w-6 h-6 text-slate-700" />
              </button>
            </div>
          </div>
        ) : null}
      </div>

      {/* Zoom Modal */}
      <AnimatePresence>
        {showZoomed && previewUrl && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="fixed inset-0 z-50 bg-black/80 flex items-center justify-center p-4"
            onClick={() => setShowZoomed(false)}
          >
            <motion.div
              initial={{ scale: 0.9, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              exit={{ scale: 0.9, opacity: 0 }}
              className="relative max-w-4xl max-h-[90vh] bg-white rounded-lg overflow-hidden"
              onClick={(e) => e.stopPropagation()}
            >
              <div className="absolute top-4 right-4 z-10 flex items-center gap-2">
                <div className="flex items-center gap-1 bg-white/90 rounded-lg p-1">
                  {ZOOM_LEVELS.map((level) => (
                    <button
                      key={level}
                      onClick={() => setZoomLevel(level)}
                      className={`px-3 py-1 text-sm rounded transition-colors ${
                        zoomLevel === level
                          ? 'bg-purple-600 text-white'
                          : 'text-slate-700 hover:bg-slate-100'
                      }`}
                    >
                      {level}x
                    </button>
                  ))}
                </div>
                <button
                  onClick={() => setShowZoomed(false)}
                  className="p-2 bg-white/90 rounded-lg hover:bg-white transition-colors"
                  aria-label="Close zoom"
                >
                  <X className="w-5 h-5 text-slate-700" />
                </button>
              </div>
              <div className="overflow-auto max-h-[90vh]">
                <img
                  src={previewUrl}
                  alt={`${productName} with custom logo - zoomed`}
                  className="w-full h-auto"
                  style={{ transform: `scale(${zoomLevel})`, transformOrigin: 'center' }}
                />
              </div>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}
