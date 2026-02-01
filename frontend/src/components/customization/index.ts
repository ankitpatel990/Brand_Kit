/**
 * Customization Components Index
 * FRD-003: Logo Customization and Real-Time Preview Engine
 */

// Sub-Prompt 1: Logo Upload Component
export { default as LogoUpload } from './LogoUpload';
export type { LogoFile } from './LogoUpload';

// Sub-Prompt 2: Logo Cropping Tool
export { default as LogoCropper } from './LogoCropper';
export type { CropData } from './LogoCropper';

// Sub-Prompt 3: Client-Side Preview Rendering
export { default as PreviewRenderer } from './PreviewRenderer';
export type { PreviewData } from './PreviewRenderer';

// Sub-Prompt 6: Bundle Builder Workflow
export { default as BundleBuilder } from './BundleBuilder';
export type { BundleItem } from './BundleBuilder';

// Sub-Prompt 8: Download Preview Image
export { default as DownloadPreview } from './DownloadPreview';

// Sub-Prompt 9: Multi-Product Logo Application
export { default as MultiProductApply } from './MultiProductApply';

// Sub-Prompt 10: Customization Validation
export { default as CustomizationValidator, validateCustomization } from './CustomizationValidator';
export type { ValidationResult, ValidationError, ValidationWarning } from './CustomizationValidator';
