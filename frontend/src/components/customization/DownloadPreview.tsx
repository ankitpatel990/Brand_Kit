'use client';

import { useState, useCallback } from 'react';
import { Download, Loader2 } from 'lucide-react';
import toast from 'react-hot-toast';

/**
 * Download Preview Component
 * FRD-003 Sub-Prompt 8: Download Preview Image
 * 
 * Features:
 * - Generate PNG: 1200×1200px
 * - Add watermark: "BrandKit Preview" (bottom-right, 30% opacity)
 * - Filename: {ProductName}_Customized_{Timestamp}.png
 * - Trigger browser download
 */

interface DownloadPreviewProps {
  previewImageUrl: string;
  productName: string;
  disabled?: boolean;
}

export default function DownloadPreview({
  previewImageUrl,
  productName,
  disabled = false,
}: DownloadPreviewProps) {
  const [isGenerating, setIsGenerating] = useState(false);

  const generateWatermarkedPreview = useCallback(async (): Promise<string> => {
    return new Promise((resolve, reject) => {
      const canvas = document.createElement('canvas');
      const ctx = canvas.getContext('2d');

      if (!ctx) {
        reject(new Error('Failed to create canvas context'));
        return;
      }

      const img = new Image();
      img.crossOrigin = 'anonymous';

      img.onload = () => {
        // Set canvas size to 1200×1200px
        const targetSize = 1200;
        canvas.width = targetSize;
        canvas.height = targetSize;

        // Fill white background
        ctx.fillStyle = '#ffffff';
        ctx.fillRect(0, 0, targetSize, targetSize);

        // Calculate scaling to fit image
        const scale = Math.min(
          targetSize / img.width,
          targetSize / img.height
        );
        const scaledWidth = img.width * scale;
        const scaledHeight = img.height * scale;
        const offsetX = (targetSize - scaledWidth) / 2;
        const offsetY = (targetSize - scaledHeight) / 2;

        // Draw the preview image
        ctx.drawImage(img, offsetX, offsetY, scaledWidth, scaledHeight);

        // Add watermark
        const watermarkText = 'BrandKit Preview';
        ctx.save();
        ctx.globalAlpha = 0.3;
        ctx.font = 'bold 32px system-ui, -apple-system, sans-serif';
        ctx.fillStyle = '#1e293b';

        // Measure text width
        const textMetrics = ctx.measureText(watermarkText);
        const textWidth = textMetrics.width;
        const textHeight = 32;

        // Position in bottom-right corner with padding
        const padding = 24;
        const x = targetSize - textWidth - padding;
        const y = targetSize - padding;

        // Draw text shadow for better visibility
        ctx.fillStyle = '#ffffff';
        ctx.globalAlpha = 0.5;
        ctx.fillText(watermarkText, x + 1, y + 1);

        // Draw main text
        ctx.fillStyle = '#1e293b';
        ctx.globalAlpha = 0.3;
        ctx.fillText(watermarkText, x, y);

        ctx.restore();

        // Convert to data URL
        const dataUrl = canvas.toDataURL('image/png', 1.0);
        resolve(dataUrl);
      };

      img.onerror = () => {
        reject(new Error('Failed to load preview image'));
      };

      img.src = previewImageUrl;
    });
  }, [previewImageUrl]);

  const handleDownload = useCallback(async () => {
    if (disabled || isGenerating) return;

    setIsGenerating(true);

    try {
      const watermarkedImage = await generateWatermarkedPreview();

      // Generate filename
      const timestamp = new Date().toISOString().split('T')[0].replace(/-/g, '');
      const sanitizedProductName = productName.replace(/[^a-zA-Z0-9]/g, '_');
      const filename = `${sanitizedProductName}_Customized_${timestamp}.png`;

      // Trigger download
      const link = document.createElement('a');
      link.download = filename;
      link.href = watermarkedImage;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);

      toast.success('Preview downloaded successfully');
    } catch (error: any) {
      console.error('Download failed:', error);
      toast.error('Failed to download preview. Please try again.');
    } finally {
      setIsGenerating(false);
    }
  }, [disabled, isGenerating, generateWatermarkedPreview, productName]);

  return (
    <button
      onClick={handleDownload}
      disabled={disabled || isGenerating}
      className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-white bg-purple-600 rounded-lg hover:bg-purple-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
    >
      {isGenerating ? (
        <>
          <Loader2 className="w-4 h-4 animate-spin" />
          Generating...
        </>
      ) : (
        <>
          <Download className="w-4 h-4" />
          Download Preview
        </>
      )}
    </button>
  );
}
