'use client';

import { useState, useRef, useCallback } from 'react';
import { Upload, X, AlertCircle, CheckCircle2, Loader2 } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import toast from 'react-hot-toast';

/**
 * Logo Upload Component
 * FRD-003 Sub-Prompt 1: Logo Upload Component (Frontend)
 * 
 * Features:
 * - Click to select or drag-and-drop
 * - Supported formats: PNG, JPG, SVG (max 10MB)
 * - Client-side validation: File type, size, dimensions
 * - Preview thumbnail after upload
 * - Progress indicator during upload
 * - Replace option
 * - Error handling with user-friendly messages
 */

export interface LogoFile {
  file: File;
  preview: string;
  width: number;
  height: number;
}

interface LogoUploadProps {
  onUpload: (logo: LogoFile) => void;
  existingLogo?: LogoFile | null;
  maxSizeMB?: number;
  minDimensions?: { width: number; height: number };
}

const MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
const MIN_DIMENSIONS = { width: 300, height: 300 };
const ALLOWED_TYPES = ['image/png', 'image/jpeg', 'image/jpg', 'image/svg+xml'];

export default function LogoUpload({
  onUpload,
  existingLogo,
  maxSizeMB = 10,
  minDimensions = MIN_DIMENSIONS,
}: LogoUploadProps) {
  const [isDragging, setIsDragging] = useState(false);
  const [isUploading, setIsUploading] = useState(false);
  const [uploadProgress, setUploadProgress] = useState(0);
  const [error, setError] = useState<string | null>(null);
  const [warning, setWarning] = useState<string | null>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const validateFile = useCallback((file: File): { valid: boolean; error?: string; warning?: string } => {
    // Check file type
    if (!ALLOWED_TYPES.includes(file.type)) {
      return {
        valid: false,
        error: 'Invalid file type. Please upload PNG, JPG, or SVG',
      };
    }

    // Check file size
    if (file.size > maxSizeMB * 1024 * 1024) {
      return {
        valid: false,
        error: `File exceeds ${maxSizeMB}MB limit. Please use a smaller image`,
      };
    }

    return { valid: true };
  }, [maxSizeMB]);

  const getImageDimensions = (file: File): Promise<{ width: number; height: number }> => {
    return new Promise((resolve, reject) => {
      const img = new Image();
      const url = URL.createObjectURL(file);

      img.onload = () => {
        URL.revokeObjectURL(url);
        resolve({ width: img.width, height: img.height });
      };

      img.onerror = () => {
        URL.revokeObjectURL(url);
        reject(new Error('Failed to load image'));
      };

      img.src = url;
    });
  };

  const handleFile = useCallback(async (file: File) => {
    setError(null);
    setWarning(null);

    // Validate file
    const validation = validateFile(file);
    if (!validation.valid) {
      setError(validation.error || 'Invalid file');
      return;
    }

    setIsUploading(true);
    setUploadProgress(0);

    try {
      // Simulate upload progress
      const progressInterval = setInterval(() => {
        setUploadProgress((prev) => {
          if (prev >= 90) {
            clearInterval(progressInterval);
            return 90;
          }
          return prev + 10;
        });
      }, 100);

      // Get image dimensions (skip for SVG)
      let dimensions = { width: 0, height: 0 };
      if (file.type !== 'image/svg+xml') {
        try {
          dimensions = await getImageDimensions(file);
          
          // Check minimum dimensions
          if (dimensions.width < minDimensions.width || dimensions.height < minDimensions.height) {
            setWarning(
              `Logo resolution is low (${dimensions.width}×${dimensions.height}px). Recommended: ${minDimensions.width}×${minDimensions.height}px minimum for best print quality.`
            );
          }
        } catch (err) {
          console.error('Failed to get image dimensions:', err);
        }
      }

      // Create preview URL
      const preview = URL.createObjectURL(file);

      // Complete upload
      clearInterval(progressInterval);
      setUploadProgress(100);

      const logoFile: LogoFile = {
        file,
        preview,
        width: dimensions.width,
        height: dimensions.height,
      };

      onUpload(logoFile);
      toast.success('Logo uploaded successfully');
    } catch (err: any) {
      console.error('Upload error:', err);
      setError(err.message || 'Upload failed. Please check connection and retry');
      toast.error('Upload failed. Please try again');
    } finally {
      setIsUploading(false);
      setUploadProgress(0);
    }
  }, [validateFile, minDimensions, onUpload]);

  const handleDrop = useCallback(
    (e: React.DragEvent<HTMLDivElement>) => {
      e.preventDefault();
      setIsDragging(false);

      const file = e.dataTransfer.files[0];
      if (file) {
        handleFile(file);
      }
    },
    [handleFile]
  );

  const handleDragOver = useCallback((e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    setIsDragging(true);
  }, []);

  const handleDragLeave = useCallback((e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    setIsDragging(false);
  }, []);

  const handleFileSelect = useCallback(
    (e: React.ChangeEvent<HTMLInputElement>) => {
      const file = e.target.files?.[0];
      if (file) {
        handleFile(file);
      }
      // Reset input to allow selecting same file again
      if (fileInputRef.current) {
        fileInputRef.current.value = '';
      }
    },
    [handleFile]
  );

  const handleReplace = useCallback(() => {
    fileInputRef.current?.click();
  }, []);

  const logo = existingLogo || null;

  return (
    <div className="space-y-4">
      <div>
        <label className="block text-sm font-medium text-slate-700 mb-2">
          Upload Logo
        </label>
        <p className="text-xs text-slate-500 mb-4">
          Supported formats: PNG, JPG, SVG (max {maxSizeMB}MB, min {minDimensions.width}×{minDimensions.height}px)
        </p>
      </div>

      {!logo ? (
        <div
          onDrop={handleDrop}
          onDragOver={handleDragOver}
          onDragLeave={handleDragLeave}
          className={`
            relative border-2 border-dashed rounded-lg p-8 text-center
            transition-all duration-200
            ${isDragging
              ? 'border-purple-500 bg-purple-50'
              : 'border-slate-300 hover:border-purple-400 hover:bg-slate-50'
            }
            ${isUploading ? 'pointer-events-none opacity-60' : 'cursor-pointer'}
          `}
          onClick={() => !isUploading && fileInputRef.current?.click()}
        >
          <input
            ref={fileInputRef}
            type="file"
            accept="image/png,image/jpeg,image/jpg,image/svg+xml"
            onChange={handleFileSelect}
            className="hidden"
            disabled={isUploading}
          />

          <AnimatePresence>
            {isUploading ? (
              <motion.div
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                exit={{ opacity: 0 }}
                className="space-y-4"
              >
                <Loader2 className="w-12 h-12 text-purple-600 animate-spin mx-auto" />
                <div className="space-y-2">
                  <p className="text-sm font-medium text-slate-700">Uploading...</p>
                  <div className="w-full bg-slate-200 rounded-full h-2">
                    <motion.div
                      className="bg-purple-600 h-2 rounded-full"
                      initial={{ width: 0 }}
                      animate={{ width: `${uploadProgress}%` }}
                      transition={{ duration: 0.3 }}
                    />
                  </div>
                  <p className="text-xs text-slate-500">{uploadProgress}%</p>
                </div>
              </motion.div>
            ) : (
              <motion.div
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                exit={{ opacity: 0 }}
                className="space-y-4"
              >
                <Upload className="w-12 h-12 text-slate-400 mx-auto" />
                <div>
                  <p className="text-sm font-medium text-slate-700">
                    Drag and drop your logo here
                  </p>
                  <p className="text-xs text-slate-500 mt-1">or click to browse</p>
                </div>
              </motion.div>
            )}
          </AnimatePresence>
        </div>
      ) : (
        <div className="relative">
          <div className="border-2 border-slate-200 rounded-lg p-4 bg-slate-50">
            <div className="flex items-center gap-4">
              <div className="relative w-24 h-24 flex-shrink-0">
                <img
                  src={logo.preview}
                  alt="Logo preview"
                  className="w-full h-full object-contain rounded"
                />
              </div>
              <div className="flex-1 min-w-0">
                <p className="text-sm font-medium text-slate-700 truncate">
                  {logo.file.name}
                </p>
                <p className="text-xs text-slate-500 mt-1">
                  {logo.width > 0 && logo.height > 0
                    ? `${logo.width}×${logo.height}px`
                    : 'SVG file'}
                  {' • '}
                  {(logo.file.size / 1024).toFixed(1)} KB
                </p>
                <button
                  onClick={handleReplace}
                  className="mt-2 text-xs text-purple-600 hover:text-purple-700 font-medium"
                >
                  Replace logo
                </button>
              </div>
              <button
                onClick={() => onUpload(null as any)}
                className="p-2 text-slate-400 hover:text-red-500 transition-colors"
                aria-label="Remove logo"
              >
                <X className="w-5 h-5" />
              </button>
            </div>
          </div>
          <input
            ref={fileInputRef}
            type="file"
            accept="image/png,image/jpeg,image/jpg,image/svg+xml"
            onChange={handleFileSelect}
            className="hidden"
          />
        </div>
      )}

      <AnimatePresence>
        {error && (
          <motion.div
            initial={{ opacity: 0, y: -10 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -10 }}
            className="flex items-start gap-2 p-3 bg-red-50 border border-red-200 rounded-lg"
          >
            <AlertCircle className="w-5 h-5 text-red-600 flex-shrink-0 mt-0.5" />
            <p className="text-sm text-red-700 flex-1">{error}</p>
          </motion.div>
        )}

        {warning && (
          <motion.div
            initial={{ opacity: 0, y: -10 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -10 }}
            className="flex items-start gap-2 p-3 bg-yellow-50 border border-yellow-200 rounded-lg"
          >
            <AlertCircle className="w-5 h-5 text-yellow-600 flex-shrink-0 mt-0.5" />
            <p className="text-sm text-yellow-700 flex-1">{warning}</p>
          </motion.div>
        )}

        {logo && !error && !warning && (
          <motion.div
            initial={{ opacity: 0, y: -10 }}
            animate={{ opacity: 1, y: 0 }}
            className="flex items-center gap-2 p-3 bg-green-50 border border-green-200 rounded-lg"
          >
            <CheckCircle2 className="w-5 h-5 text-green-600 flex-shrink-0" />
            <p className="text-sm text-green-700">Logo uploaded successfully</p>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}
