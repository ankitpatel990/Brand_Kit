'use client';

import { useMemo } from 'react';
import { CheckCircle, XCircle, AlertTriangle, Info } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import { LogoFile } from './LogoUpload';
import { CropData } from './LogoCropper';

/**
 * Customization Validation Component
 * FRD-003 Sub-Prompt 10: Customization Validation
 * 
 * Client-side validation for customizations:
 * - Logo uploaded (required)
 * - Crop applied (required)
 * - Preview generated successfully
 * - Logo resolution sufficient for print
 */

export interface ValidationResult {
  isValid: boolean;
  errors: ValidationError[];
  warnings: ValidationWarning[];
}

export interface ValidationError {
  code: string;
  message: string;
  field: 'logo' | 'crop' | 'preview' | 'product';
}

export interface ValidationWarning {
  code: string;
  message: string;
  field: 'logo' | 'crop' | 'preview';
}

interface CustomizationValidatorProps {
  logo: LogoFile | null;
  cropData: CropData | null;
  previewGenerated: boolean;
  printArea?: { width: number; height: number } | null;
  showInline?: boolean;
}

// Minimum DPI for acceptable print quality
const MIN_PRINT_DPI = 150;
// Standard print area in pixels at 300 DPI (approximately 20cm x 25cm)
const STANDARD_PRINT_WIDTH_PX = 2362; // 20cm at 300 DPI
const STANDARD_PRINT_HEIGHT_PX = 2953; // 25cm at 300 DPI

export function validateCustomization(
  logo: LogoFile | null,
  cropData: CropData | null,
  previewGenerated: boolean,
  printArea?: { width: number; height: number } | null
): ValidationResult {
  const errors: ValidationError[] = [];
  const warnings: ValidationWarning[] = [];

  // Check logo uploaded
  if (!logo) {
    errors.push({
      code: 'CUST_NO_LOGO',
      message: 'Please upload a logo',
      field: 'logo',
    });
  } else {
    // Check logo dimensions for print quality
    if (logo.width > 0 && logo.height > 0 && printArea) {
      // Calculate what DPI the logo would be at print size
      // Print area is in cm, convert to inches (1 inch = 2.54 cm)
      const printWidthInches = printArea.width / 2.54;
      const printHeightInches = printArea.height / 2.54;

      // Calculate effective DPI
      const widthDPI = logo.width / printWidthInches;
      const heightDPI = logo.height / printHeightInches;
      const effectiveDPI = Math.min(widthDPI, heightDPI);

      if (effectiveDPI < MIN_PRINT_DPI) {
        warnings.push({
          code: 'CUST_LOW_RES',
          message: `Logo may appear pixelated when printed (${Math.round(effectiveDPI)} DPI). For best results, upload a higher resolution image.`,
          field: 'logo',
        });
      }
    }

    // Check minimum dimensions
    if (logo.width > 0 && logo.height > 0) {
      if (logo.width < 300 || logo.height < 300) {
        warnings.push({
          code: 'CUST_SMALL_LOGO',
          message: 'Logo resolution is low. Recommended: 1000×1000px minimum for best print quality.',
          field: 'logo',
        });
      }
    }
  }

  // Check crop applied
  if (!cropData) {
    errors.push({
      code: 'CUST_NO_CROP',
      message: 'Please crop your logo',
      field: 'crop',
    });
  } else {
    // Validate crop dimensions
    if (cropData.width < 100 || cropData.height < 100) {
      errors.push({
        code: 'CUST_CROP_TOO_SMALL',
        message: 'Crop area is too small. Minimum size: 100×100px',
        field: 'crop',
      });
    }

    // Validate crop coordinates are positive
    if (cropData.x < 0 || cropData.y < 0) {
      errors.push({
        code: 'CUST_INVALID_CROP',
        message: 'Invalid crop area. Please recrop logo.',
        field: 'crop',
      });
    }
  }

  // Check preview generated
  if (!previewGenerated) {
    errors.push({
      code: 'CUST_NO_PREVIEW',
      message: 'Preview not generated. Please apply crop to see preview.',
      field: 'preview',
    });
  }

  return {
    isValid: errors.length === 0,
    errors,
    warnings,
  };
}

export default function CustomizationValidator({
  logo,
  cropData,
  previewGenerated,
  printArea,
  showInline = false,
}: CustomizationValidatorProps) {
  const validation = useMemo(
    () => validateCustomization(logo, cropData, previewGenerated, printArea),
    [logo, cropData, previewGenerated, printArea]
  );

  // Validation steps with status
  const steps = useMemo(() => [
    {
      id: 'logo',
      label: 'Logo uploaded',
      status: logo ? 'complete' : 'incomplete',
      error: validation.errors.find(e => e.field === 'logo'),
      warning: validation.warnings.find(w => w.field === 'logo'),
    },
    {
      id: 'crop',
      label: 'Logo cropped',
      status: cropData ? 'complete' : 'incomplete',
      error: validation.errors.find(e => e.field === 'crop'),
      warning: validation.warnings.find(w => w.field === 'crop'),
    },
    {
      id: 'preview',
      label: 'Preview generated',
      status: previewGenerated ? 'complete' : 'incomplete',
      error: validation.errors.find(e => e.field === 'preview'),
      warning: validation.warnings.find(w => w.field === 'preview'),
    },
  ], [logo, cropData, previewGenerated, validation]);

  if (showInline) {
    return (
      <div className="flex items-center gap-3">
        {steps.map(step => (
          <div
            key={step.id}
            className="flex items-center gap-1.5"
            title={step.error?.message || step.warning?.message || step.label}
          >
            {step.status === 'complete' ? (
              step.warning ? (
                <AlertTriangle className="w-4 h-4 text-amber-500" />
              ) : (
                <CheckCircle className="w-4 h-4 text-green-500" />
              )
            ) : (
              <XCircle className="w-4 h-4 text-slate-300" />
            )}
            <span className={`text-xs ${
              step.status === 'complete'
                ? step.warning ? 'text-amber-600' : 'text-green-600'
                : 'text-slate-400'
            }`}>
              {step.label}
            </span>
          </div>
        ))}
      </div>
    );
  }

  return (
    <div className="space-y-4">
      <div className="flex items-center gap-2">
        <h4 className="font-medium text-slate-900">Customization Status</h4>
        {validation.isValid ? (
          <span className="flex items-center gap-1 text-xs text-green-600 bg-green-50 px-2 py-0.5 rounded-full">
            <CheckCircle className="w-3 h-3" />
            Ready
          </span>
        ) : (
          <span className="flex items-center gap-1 text-xs text-red-600 bg-red-50 px-2 py-0.5 rounded-full">
            <XCircle className="w-3 h-3" />
            Incomplete
          </span>
        )}
      </div>

      <div className="space-y-2">
        {steps.map(step => (
          <div
            key={step.id}
            className="flex items-start gap-3 p-3 rounded-lg bg-slate-50"
          >
            {step.status === 'complete' ? (
              step.warning ? (
                <AlertTriangle className="w-5 h-5 text-amber-500 flex-shrink-0 mt-0.5" />
              ) : (
                <CheckCircle className="w-5 h-5 text-green-500 flex-shrink-0 mt-0.5" />
              )
            ) : step.error ? (
              <XCircle className="w-5 h-5 text-red-500 flex-shrink-0 mt-0.5" />
            ) : (
              <div className="w-5 h-5 border-2 border-slate-300 rounded-full flex-shrink-0 mt-0.5" />
            )}

            <div className="flex-1 min-w-0">
              <p className={`text-sm font-medium ${
                step.status === 'complete'
                  ? step.warning ? 'text-amber-900' : 'text-green-900'
                  : step.error ? 'text-red-900' : 'text-slate-600'
              }`}>
                {step.label}
              </p>
              
              <AnimatePresence>
                {step.error && (
                  <motion.p
                    initial={{ opacity: 0, height: 0 }}
                    animate={{ opacity: 1, height: 'auto' }}
                    exit={{ opacity: 0, height: 0 }}
                    className="text-xs text-red-600 mt-1"
                  >
                    {step.error.message}
                  </motion.p>
                )}
                {step.warning && (
                  <motion.p
                    initial={{ opacity: 0, height: 0 }}
                    animate={{ opacity: 1, height: 'auto' }}
                    exit={{ opacity: 0, height: 0 }}
                    className="text-xs text-amber-600 mt-1"
                  >
                    {step.warning.message}
                  </motion.p>
                )}
              </AnimatePresence>
            </div>
          </div>
        ))}
      </div>

      {/* Overall status message */}
      {validation.isValid && validation.warnings.length === 0 && (
        <div className="flex items-start gap-2 p-3 bg-green-50 border border-green-200 rounded-lg">
          <CheckCircle className="w-5 h-5 text-green-600 flex-shrink-0" />
          <p className="text-sm text-green-700">
            Your customization is complete and ready to add to cart.
          </p>
        </div>
      )}

      {validation.isValid && validation.warnings.length > 0 && (
        <div className="flex items-start gap-2 p-3 bg-amber-50 border border-amber-200 rounded-lg">
          <AlertTriangle className="w-5 h-5 text-amber-600 flex-shrink-0" />
          <div className="text-sm text-amber-700">
            <p className="font-medium">Customization ready with warnings</p>
            <p className="mt-1">You can proceed, but the print quality may be affected.</p>
          </div>
        </div>
      )}

      {!validation.isValid && (
        <div className="flex items-start gap-2 p-3 bg-red-50 border border-red-200 rounded-lg">
          <XCircle className="w-5 h-5 text-red-600 flex-shrink-0" />
          <p className="text-sm text-red-700">
            Please complete all customization steps before adding to cart.
          </p>
        </div>
      )}
    </div>
  );
}
