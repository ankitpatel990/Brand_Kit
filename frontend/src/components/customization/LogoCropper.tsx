'use client';

import { useState, useCallback } from 'react';
import Cropper from 'react-easy-crop';
import { ZoomIn, ZoomOut, RotateCw, Check, X } from 'lucide-react';
import { motion } from 'framer-motion';
import { LogoFile } from './LogoUpload';

/**
 * Logo Cropping Tool with react-easy-crop
 * FRD-003 Sub-Prompt 2: Logo Cropping Tool with react-easy-crop
 * 
 * Features:
 * - Locked aspect ratio based on product print area
 * - Zoom (1x to 3x) and Pan (drag)
 * - Visual aids: Grid overlay, dimmed outside area
 * - Minimum crop 100×100px
 * - Reset and Apply Crop buttons
 */

export interface CropData {
  x: number;
  y: number;
  width: number;
  height: number;
  zoom: number;
  aspectRatio: number;
}

interface LogoCropperProps {
  logo: LogoFile;
  aspectRatio: number; // Product print area aspect ratio (e.g., 4/5 for T-shirt)
  onCropComplete: (cropData: CropData, croppedImage: string) => void;
  onCancel: () => void;
  minCropSize?: { width: number; height: number };
}

const MIN_ZOOM = 1;
const MAX_ZOOM = 3;
const MIN_CROP_SIZE = { width: 100, height: 100 };

export default function LogoCropper({
  logo,
  aspectRatio,
  onCropComplete,
  onCancel,
  minCropSize = MIN_CROP_SIZE,
}: LogoCropperProps) {
  const [crop, setCrop] = useState({ x: 0, y: 0 });
  const [zoom, setZoom] = useState(1);
  const [croppedAreaPixels, setCroppedAreaPixels] = useState<any>(null);
  const [isProcessing, setIsProcessing] = useState(false);

  const onCropChange = useCallback((crop: { x: number; y: number }) => {
    setCrop(crop);
  }, []);

  const onZoomChange = useCallback((zoom: number) => {
    setZoom(Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, zoom)));
  }, []);

  const onCropCompleteCallback = useCallback(
    (croppedArea: any, croppedAreaPixels: any) => {
      setCroppedAreaPixels(croppedAreaPixels);
    },
    []
  );

  const createImage = (url: string): Promise<HTMLImageElement> => {
    return new Promise((resolve, reject) => {
      const image = new Image();
      image.addEventListener('load', () => resolve(image));
      image.addEventListener('error', (error) => reject(error));
      image.src = url;
    });
  };

  const getCroppedImg = async (
    imageSrc: string,
    pixelCrop: { x: number; y: number; width: number; height: number }
  ): Promise<string> => {
    const image = await createImage(imageSrc);
    const canvas = document.createElement('canvas');
    const ctx = canvas.getContext('2d');

    if (!ctx) {
      throw new Error('Failed to get canvas context');
    }

    // Set canvas size to match crop area
    canvas.width = pixelCrop.width;
    canvas.height = pixelCrop.height;

    // Draw cropped image
    ctx.drawImage(
      image,
      pixelCrop.x,
      pixelCrop.y,
      pixelCrop.width,
      pixelCrop.height,
      0,
      0,
      pixelCrop.width,
      pixelCrop.height
    );

    return new Promise((resolve, reject) => {
      canvas.toBlob(
        (blob) => {
          if (!blob) {
            reject(new Error('Canvas is empty'));
            return;
          }
          const url = URL.createObjectURL(blob);
          resolve(url);
        },
        'image/png',
        1.0
      );
    });
  };

  const handleApplyCrop = useCallback(async () => {
    if (!croppedAreaPixels) {
      return;
    }

    // Validate minimum crop size
    if (
      croppedAreaPixels.width < minCropSize.width ||
      croppedAreaPixels.height < minCropSize.height
    ) {
      alert(
        `Crop area is too small. Minimum size: ${minCropSize.width}×${minCropSize.height}px`
      );
      return;
    }

    setIsProcessing(true);

    try {
      const croppedImage = await getCroppedImg(logo.preview, croppedAreaPixels);

      const cropData: CropData = {
        x: croppedAreaPixels.x,
        y: croppedAreaPixels.y,
        width: croppedAreaPixels.width,
        height: croppedAreaPixels.height,
        zoom,
        aspectRatio,
      };

      onCropComplete(cropData, croppedImage);
    } catch (error) {
      console.error('Failed to crop image:', error);
      alert('Failed to process crop. Please try again.');
    } finally {
      setIsProcessing(false);
    }
  }, [croppedAreaPixels, logo.preview, zoom, aspectRatio, minCropSize, onCropComplete]);

  const handleReset = useCallback(() => {
    setCrop({ x: 0, y: 0 });
    setZoom(1);
    setCroppedAreaPixels(null);
  }, []);

  const handleZoomIn = useCallback(() => {
    setZoom((prev) => Math.min(MAX_ZOOM, prev + 0.1));
  }, []);

  const handleZoomOut = useCallback(() => {
    setZoom((prev) => Math.max(MIN_ZOOM, prev - 0.1));
  }, []);

  return (
    <div className="space-y-4">
      <div>
        <h3 className="text-lg font-semibold text-slate-900 mb-1">Crop Your Logo</h3>
        <p className="text-sm text-slate-600">
          Adjust zoom and position to frame your logo within the print area
        </p>
      </div>

      <div className="relative w-full h-[400px] bg-slate-100 rounded-lg overflow-hidden">
        <Cropper
          image={logo.preview}
          crop={crop}
          zoom={zoom}
          aspect={aspectRatio}
          onCropChange={onCropChange}
          onZoomChange={onZoomChange}
          onCropComplete={onCropCompleteCallback}
          showGrid={true}
          restrictPosition={true}
          minZoom={MIN_ZOOM}
          maxZoom={MAX_ZOOM}
          style={{
            containerStyle: {
              width: '100%',
              height: '100%',
              position: 'relative',
            },
          }}
        />
      </div>

      {/* Controls */}
      <div className="space-y-4">
        {/* Zoom Controls */}
        <div className="flex items-center gap-4">
          <button
            onClick={handleZoomOut}
            disabled={zoom <= MIN_ZOOM}
            className="p-2 rounded-lg border border-slate-300 hover:bg-slate-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            aria-label="Zoom out"
          >
            <ZoomOut className="w-5 h-5 text-slate-700" />
          </button>

          <div className="flex-1">
            <input
              type="range"
              min={MIN_ZOOM}
              max={MAX_ZOOM}
              step={0.1}
              value={zoom}
              onChange={(e) => onZoomChange(parseFloat(e.target.value))}
              className="w-full h-2 bg-slate-200 rounded-lg appearance-none cursor-pointer accent-purple-600"
            />
            <div className="flex justify-between text-xs text-slate-500 mt-1">
              <span>1x</span>
              <span className="font-medium">{zoom.toFixed(1)}x</span>
              <span>3x</span>
            </div>
          </div>

          <button
            onClick={handleZoomIn}
            disabled={zoom >= MAX_ZOOM}
            className="p-2 rounded-lg border border-slate-300 hover:bg-slate-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            aria-label="Zoom in"
          >
            <ZoomIn className="w-5 h-5 text-slate-700" />
          </button>
        </div>

        {/* Action Buttons */}
        <div className="flex items-center gap-3">
          <button
            onClick={handleReset}
            className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-slate-700 bg-white border border-slate-300 rounded-lg hover:bg-slate-50 transition-colors"
          >
            <RotateCw className="w-4 h-4" />
            Reset
          </button>

          <button
            onClick={onCancel}
            className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-slate-700 bg-white border border-slate-300 rounded-lg hover:bg-slate-50 transition-colors"
          >
            <X className="w-4 h-4" />
            Cancel
          </button>

          <button
            onClick={handleApplyCrop}
            disabled={!croppedAreaPixels || isProcessing}
            className="flex-1 flex items-center justify-center gap-2 px-6 py-2 text-sm font-medium text-white bg-purple-600 rounded-lg hover:bg-purple-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            {isProcessing ? (
              <>
                <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                Processing...
              </>
            ) : (
              <>
                <Check className="w-4 h-4" />
                Apply Crop
              </>
            )}
          </button>
        </div>
      </div>

      {/* Instructions */}
      <div className="p-3 bg-blue-50 border border-blue-200 rounded-lg">
        <p className="text-xs text-blue-700">
          <strong>Tip:</strong> Drag the logo to reposition, use the slider or buttons to zoom.
          The crop area is locked to match your product's print area aspect ratio.
        </p>
      </div>
    </div>
  );
}
