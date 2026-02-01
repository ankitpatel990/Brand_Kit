'use client';

import { useState, useRef, useEffect, useCallback } from 'react';
import { Save, RotateCcw, Move, ZoomIn, ZoomOut } from 'lucide-react';
import { motion } from 'framer-motion';

/**
 * Print Area Editor Component
 * FRD-003 Sub-Prompt 5: Print Area Configuration (Admin)
 * 
 * Visual editor for configuring product print areas.
 * Features:
 * - Upload product base image
 * - Drag rectangle to define print area
 * - Set dimensions (width × height in cm)
 * - Calculate aspect ratio
 * - Save coordinates to product
 */

export interface PrintAreaData {
  x: number;
  y: number;
  width: number;
  height: number;
  widthCm: number;
  heightCm: number;
  aspectRatio: number;
}

interface PrintAreaEditorProps {
  productImageUrl: string;
  initialPrintArea?: PrintAreaData | null;
  onSave: (printArea: PrintAreaData) => void;
  onCancel: () => void;
}

export default function PrintAreaEditor({
  productImageUrl,
  initialPrintArea,
  onSave,
  onCancel,
}: PrintAreaEditorProps) {
  const containerRef = useRef<HTMLDivElement>(null);
  const [imageSize, setImageSize] = useState({ width: 0, height: 0 });
  const [isDrawing, setIsDrawing] = useState(false);
  const [startPoint, setStartPoint] = useState({ x: 0, y: 0 });
  
  // Print area state (in pixels relative to displayed image)
  const [printArea, setPrintArea] = useState<{
    x: number;
    y: number;
    width: number;
    height: number;
  } | null>(initialPrintArea ? {
    x: initialPrintArea.x,
    y: initialPrintArea.y,
    width: initialPrintArea.width,
    height: initialPrintArea.height,
  } : null);

  // Physical dimensions in cm
  const [widthCm, setWidthCm] = useState(initialPrintArea?.widthCm || 20);
  const [heightCm, setHeightCm] = useState(initialPrintArea?.heightCm || 25);

  // Calculate aspect ratio
  const aspectRatio = heightCm > 0 ? widthCm / heightCm : 1;

  // Handle image load to get dimensions
  const handleImageLoad = useCallback((e: React.SyntheticEvent<HTMLImageElement>) => {
    const img = e.currentTarget;
    setImageSize({ width: img.naturalWidth, height: img.naturalHeight });
  }, []);

  // Mouse down - start drawing
  const handleMouseDown = useCallback((e: React.MouseEvent<HTMLDivElement>) => {
    if (!containerRef.current) return;
    
    const rect = containerRef.current.getBoundingClientRect();
    const x = e.clientX - rect.left;
    const y = e.clientY - rect.top;
    
    setIsDrawing(true);
    setStartPoint({ x, y });
    setPrintArea({ x, y, width: 0, height: 0 });
  }, []);

  // Mouse move - update rectangle while drawing
  const handleMouseMove = useCallback((e: React.MouseEvent<HTMLDivElement>) => {
    if (!isDrawing || !containerRef.current) return;
    
    const rect = containerRef.current.getBoundingClientRect();
    let currentX = Math.max(0, Math.min(e.clientX - rect.left, rect.width));
    let currentY = Math.max(0, Math.min(e.clientY - rect.top, rect.height));
    
    // Calculate rectangle dimensions
    const width = Math.abs(currentX - startPoint.x);
    const height = Math.abs(currentY - startPoint.y);
    const x = Math.min(startPoint.x, currentX);
    const y = Math.min(startPoint.y, currentY);
    
    // Constrain to image bounds
    const constrainedWidth = Math.min(width, rect.width - x);
    const constrainedHeight = Math.min(height, rect.height - y);
    
    setPrintArea({
      x,
      y,
      width: constrainedWidth,
      height: constrainedHeight,
    });
  }, [isDrawing, startPoint]);

  // Mouse up - finish drawing
  const handleMouseUp = useCallback(() => {
    setIsDrawing(false);
  }, []);

  // Reset print area
  const handleReset = useCallback(() => {
    setPrintArea(null);
    setWidthCm(20);
    setHeightCm(25);
  }, []);

  // Save print area
  const handleSave = useCallback(() => {
    if (!printArea || printArea.width < 10 || printArea.height < 10) {
      alert('Please draw a valid print area on the product image');
      return;
    }

    const data: PrintAreaData = {
      x: printArea.x,
      y: printArea.y,
      width: printArea.width,
      height: printArea.height,
      widthCm,
      heightCm,
      aspectRatio,
    };

    onSave(data);
  }, [printArea, widthCm, heightCm, aspectRatio, onSave]);

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h3 className="text-lg font-semibold text-slate-900">Configure Print Area</h3>
          <p className="text-sm text-slate-600">
            Draw a rectangle on the product image to define where logos will be placed
          </p>
        </div>
      </div>

      {/* Image Editor */}
      <div
        ref={containerRef}
        className="relative bg-slate-100 rounded-lg overflow-hidden cursor-crosshair select-none"
        onMouseDown={handleMouseDown}
        onMouseMove={handleMouseMove}
        onMouseUp={handleMouseUp}
        onMouseLeave={handleMouseUp}
      >
        <img
          src={productImageUrl}
          alt="Product"
          className="w-full h-auto max-h-[500px] object-contain"
          onLoad={handleImageLoad}
          draggable={false}
        />

        {/* Print Area Overlay */}
        {printArea && printArea.width > 0 && printArea.height > 0 && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            className="absolute border-2 border-dashed border-purple-500 bg-purple-500/20"
            style={{
              left: printArea.x,
              top: printArea.y,
              width: printArea.width,
              height: printArea.height,
            }}
          >
            {/* Corner handles */}
            <div className="absolute -top-1 -left-1 w-3 h-3 bg-purple-600 rounded-full" />
            <div className="absolute -top-1 -right-1 w-3 h-3 bg-purple-600 rounded-full" />
            <div className="absolute -bottom-1 -left-1 w-3 h-3 bg-purple-600 rounded-full" />
            <div className="absolute -bottom-1 -right-1 w-3 h-3 bg-purple-600 rounded-full" />

            {/* Dimension label */}
            <div className="absolute -top-8 left-1/2 -translate-x-1/2 bg-purple-600 text-white text-xs px-2 py-1 rounded whitespace-nowrap">
              {Math.round(printArea.width)}×{Math.round(printArea.height)} px
            </div>
          </motion.div>
        )}

        {/* Instructions overlay */}
        {!printArea && (
          <div className="absolute inset-0 flex items-center justify-center bg-black/10">
            <div className="bg-white/90 px-4 py-2 rounded-lg text-sm text-slate-700">
              Click and drag to define print area
            </div>
          </div>
        )}
      </div>

      {/* Physical Dimensions */}
      <div className="bg-slate-50 rounded-lg p-4 space-y-4">
        <h4 className="font-medium text-slate-900">Physical Dimensions</h4>
        <div className="grid grid-cols-3 gap-4">
          <div>
            <label className="block text-sm text-slate-600 mb-1">Width (cm)</label>
            <input
              type="number"
              value={widthCm}
              onChange={(e) => setWidthCm(parseFloat(e.target.value) || 0)}
              min={1}
              max={100}
              step={0.5}
              className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
            />
          </div>
          <div>
            <label className="block text-sm text-slate-600 mb-1">Height (cm)</label>
            <input
              type="number"
              value={heightCm}
              onChange={(e) => setHeightCm(parseFloat(e.target.value) || 0)}
              min={1}
              max={100}
              step={0.5}
              className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
            />
          </div>
          <div>
            <label className="block text-sm text-slate-600 mb-1">Aspect Ratio</label>
            <div className="px-3 py-2 bg-slate-100 rounded-lg text-slate-700 font-mono">
              {aspectRatio.toFixed(2)} : 1
            </div>
          </div>
        </div>
      </div>

      {/* Print Area Summary */}
      {printArea && printArea.width > 0 && (
        <div className="bg-purple-50 rounded-lg p-4">
          <h4 className="font-medium text-purple-900 mb-2">Print Area Configuration</h4>
          <dl className="grid grid-cols-2 gap-4 text-sm">
            <div>
              <dt className="text-purple-700">Position</dt>
              <dd className="font-mono text-purple-900">
                X: {Math.round(printArea.x)}, Y: {Math.round(printArea.y)}
              </dd>
            </div>
            <div>
              <dt className="text-purple-700">Size (pixels)</dt>
              <dd className="font-mono text-purple-900">
                {Math.round(printArea.width)} × {Math.round(printArea.height)}
              </dd>
            </div>
            <div>
              <dt className="text-purple-700">Size (cm)</dt>
              <dd className="font-mono text-purple-900">
                {widthCm} × {heightCm}
              </dd>
            </div>
            <div>
              <dt className="text-purple-700">Aspect Ratio</dt>
              <dd className="font-mono text-purple-900">
                {aspectRatio.toFixed(2)}:1
              </dd>
            </div>
          </dl>
        </div>
      )}

      {/* Action Buttons */}
      <div className="flex items-center gap-3">
        <button
          onClick={handleReset}
          className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-slate-700 bg-white border border-slate-300 rounded-lg hover:bg-slate-50 transition-colors"
        >
          <RotateCcw className="w-4 h-4" />
          Reset
        </button>
        <button
          onClick={onCancel}
          className="px-4 py-2 text-sm font-medium text-slate-700 bg-white border border-slate-300 rounded-lg hover:bg-slate-50 transition-colors"
        >
          Cancel
        </button>
        <button
          onClick={handleSave}
          disabled={!printArea || printArea.width < 10}
          className="flex-1 flex items-center justify-center gap-2 px-6 py-2 text-sm font-medium text-white bg-purple-600 rounded-lg hover:bg-purple-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
        >
          <Save className="w-4 h-4" />
          Save Print Area
        </button>
      </div>
    </div>
  );
}
