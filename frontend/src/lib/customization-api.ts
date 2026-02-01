/**
 * Customization API Client
 * FRD-003: Customization Engine API Integration
 */

import { api } from './api';

export interface CropData {
  x: number;
  y: number;
  width: number;
  height: number;
  zoom: number;
  aspectRatio: number;
}

export interface SaveDraftRequest {
  productId: string;
  logoFileUrl: string;
  logoFileName: string;
  logoFileSize: number;
  logoDimensions?: { width: number; height: number };
  cropData: CropData;
  croppedImageUrl: string;
  previewImageUrl?: string;
  bundleId?: string;
  bundleName?: string;
}

export interface Draft {
  id: string;
  productId: string;
  logoFileUrl: string;
  logoFileName: string;
  croppedImageUrl: string;
  previewImageUrl: string;
  bundleName: string;
  createdAt: string;
  expiresAt: string;
}

export const customizationApi = {
  /**
   * Save customization draft
   * FRD-003 Sub-Prompt 7: Save Draft Customization
   */
  saveDraft: async (request: SaveDraftRequest): Promise<{ draftId: string }> => {
    const response = await api.post('/customization/save-draft', {
      productId: request.productId,
      logoFileUrl: request.logoFileUrl,
      logoFileName: request.logoFileName,
      logoFileSize: request.logoFileSize,
      logoDimensions: request.logoDimensions
        ? JSON.stringify(request.logoDimensions)
        : undefined,
      cropData: request.cropData,
      croppedImageUrl: request.croppedImageUrl,
      previewImageUrl: request.previewImageUrl,
      bundleId: request.bundleId,
      bundleName: request.bundleName,
    });
    return response.data.data;
  },

  /**
   * Get user's drafts
   * FRD-003 Sub-Prompt 7: List Drafts
   */
  getDrafts: async (): Promise<Draft[]> => {
    const response = await api.get('/customization/drafts');
    return response.data.data;
  },

  /**
   * Get draft by ID
   * FRD-003 Sub-Prompt 7: Load Draft
   */
  getDraft: async (draftId: string): Promise<Draft> => {
    const response = await api.get(`/customization/draft/${draftId}`);
    return response.data.data;
  },

  /**
   * Delete draft
   * FRD-003 Sub-Prompt 7: Delete Draft
   */
  deleteDraft: async (draftId: string): Promise<void> => {
    await api.delete(`/customization/draft/${draftId}`);
  },

  // ==================== Bundle APIs ====================

  /**
   * Create bundle
   * FRD-003 Sub-Prompt 6: Bundle Builder
   */
  createBundle: async (request: CreateBundleRequest): Promise<{ bundleId: string }> => {
    const response = await api.post('/customization/bundles', request);
    return response.data.data;
  },

  /**
   * Get user's bundles
   */
  getBundles: async (): Promise<Bundle[]> => {
    const response = await api.get('/customization/bundles');
    return response.data.data;
  },

  /**
   * Get bundle by ID
   */
  getBundle: async (bundleId: string): Promise<Bundle> => {
    const response = await api.get(`/customization/bundles/${bundleId}`);
    return response.data.data;
  },

  /**
   * Delete bundle
   */
  deleteBundle: async (bundleId: string): Promise<void> => {
    await api.delete(`/customization/bundles/${bundleId}`);
  },

  /**
   * Validate customization
   * FRD-003 Sub-Prompt 10: Validation
   */
  validateCustomization: async (
    logoFileId: string,
    productId: string,
    cropData: CropData
  ): Promise<ValidationResult> => {
    const response = await api.post(
      `/customization/validate?logoFileId=${logoFileId}&productId=${productId}`,
      cropData
    );
    return response.data;
  },
};

// Additional types for bundles
export interface CreateBundleRequest {
  bundleName: string;
  items: BundleItemRequest[];
}

export interface BundleItemRequest {
  productId: string;
  customizationId: string;
  quantity: number;
  unitPrice: number;
}

export interface Bundle {
  id: string;
  bundleName: string;
  totalPrice: number;
  productCount: number;
  status: string;
  items: BundleItem[];
  createdAt: string;
}

export interface BundleItem {
  id: string;
  productId: string;
  customizationId: string;
  quantity: number;
  unitPrice: number;
  subtotal: number;
}

export interface ValidationResult {
  valid: boolean;
  errors: Array<{ code: string; message: string; field: string }>;
  warnings: Array<{ code: string; message: string; field: string }>;
}
