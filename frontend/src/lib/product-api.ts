import { api } from './api';

/**
 * Product API for BrandKit
 * FRD-002: Product Catalog Management
 * NOTE: Partner information is NEVER exposed in client APIs
 */

// ==================== Types ====================

export interface ProductSummary {
  productId: string;
  name: string;
  slug: string;
  category: string;
  shortDescription: string;
  basePrice: number;
  discountedPrice: number;
  discountPercentage: number | null;
  hasDiscount: boolean;
  imageUrl: string | null;
  ecoFriendly: boolean;
  customizable: boolean;
  customizationType: string;
  aggregateRating: number;
  totalReviews: number;
  leadTimeDays: number;
  availability: 'AVAILABLE' | 'LIMITED' | 'OUT_OF_STOCK' | 'COMING_SOON';
}

export interface PaginationInfo {
  currentPage: number;
  totalPages: number;
  totalProducts: number;
  perPage: number;
  hasNext: boolean;
  hasPrevious: boolean;
}

export interface AppliedFilters {
  minPrice?: number;
  maxPrice?: number;
  materials?: string[];
  ecoFriendly?: boolean;
  customizationTypes?: string[];
  minRating?: number;
  category?: string;
  leadTime?: string;
  hasDiscount?: boolean;
}

export interface ProductListResponse {
  status: string;
  data: {
    products: ProductSummary[];
    pagination: PaginationInfo;
    appliedFilters?: AppliedFilters;
  };
}

export interface ProductImage {
  id: string;
  imageUrl: string;
  thumbnailUrl: string | null;
  mediumUrl: string | null;
  altText: string;
  displayOrder: number;
  isPrimary: boolean;
}

export interface PricingTier {
  tierNumber: number;
  minQuantity: number;
  maxQuantity: number | null;
  unitPrice: number;
  discountPercentage: number;
  description: string;
}

export interface PrintArea {
  width: number;
  height: number;
  unit: string;
}

export interface Specifications {
  material: string;
  weightGrams: number | null;
  dimensions: string | null;
  availableColors: string[] | null;
  leadTimeDays: number;
}

export interface SeoInfo {
  metaTitle: string;
  metaDescription: string;
  canonicalUrl: string;
}

export interface ProductDetail {
  productId: string;
  name: string;
  slug: string;
  category: string;
  categorySlug: string;
  shortDescription: string;
  longDescription: string;
  basePrice: number;
  discountedPrice: number;
  discountPercentage: number | null;
  hasDiscount: boolean;
  discountName: string | null;
  material: string;
  ecoFriendly: boolean;
  customizable: boolean;
  customizationType: string;
  printArea: PrintArea | null;
  images: ProductImage[];
  pricingTiers: PricingTier[];
  specifications: Specifications;
  aggregateRating: number;
  totalReviews: number;
  leadTimeDays: number;
  availability: string;
  tags: string[] | null;
  seo: SeoInfo;
  createdAt: string;
}

export interface ProductDetailResponse {
  status: string;
  data: ProductDetail;
}

export interface AutocompleteSuggestion {
  productId: string;
  name: string;
  slug: string;
  category: string;
  basePrice: number;
  imageUrl: string | null;
}

export interface CategorySuggestion {
  name: string;
  slug: string;
  productCount: number;
}

export interface AutocompleteResponse {
  status: string;
  data: {
    query: string;
    products: AutocompleteSuggestion[];
    categories: CategorySuggestion[];
  };
}

export interface SearchResponse {
  status: string;
  data: {
    query: string;
    products: ProductSummary[];
    pagination: PaginationInfo;
    suggestions: string[];
    categorySuggestions: CategorySuggestion[];
  };
}

export interface PriceCalculationRequest {
  quantity: number;
  customization?: boolean;
  customizationType?: string;
}

export interface ApplicableTier {
  tierNumber: number;
  minQuantity: number;
  maxQuantity: number | null;
  unitPrice: number;
}

export interface Savings {
  amount: number;
  percentage: number;
  description: string | null;
}

export interface PriceCalculationResult {
  quantity: number;
  applicableTier: ApplicableTier;
  unitPrice: number;
  subtotal: number;
  customizationFee: number;
  discountAmount: number;
  totalPrice: number;
  savings: Savings;
  hasDiscount: boolean;
  discountName: string | null;
}

export interface PriceCalculationResponse {
  status: string;
  data: PriceCalculationResult;
}

export interface Category {
  id: string;
  name: string;
  slug: string;
  description: string | null;
  imageUrl: string | null;
  displayOrder: number;
  productCount: number;
}

export interface CategoryResponse {
  status: string;
  data: Category[];
}

export interface ProductFilters {
  category?: string;
  minPrice?: number;
  maxPrice?: number;
  material?: string[];
  ecoFriendly?: boolean;
  customizationType?: string[];
  minRating?: number;
  hasDiscount?: boolean;
  leadTime?: string;
  sort?: 'popular' | 'price_asc' | 'price_desc' | 'rating' | 'newest';
  page?: number;
  limit?: number;
}

// ==================== API Functions ====================

export const productApi = {
  /**
   * Get products with filters
   * FRD-002 Sub-Prompt 2: Product Listing API
   */
  getProducts: async (filters: ProductFilters = {}): Promise<ProductListResponse> => {
    const params = new URLSearchParams();
    
    if (filters.category) params.append('category', filters.category);
    if (filters.minPrice) params.append('minPrice', String(filters.minPrice));
    if (filters.maxPrice) params.append('maxPrice', String(filters.maxPrice));
    if (filters.material?.length) {
      filters.material.forEach(m => params.append('material', m));
    }
    if (filters.ecoFriendly !== undefined) params.append('ecoFriendly', String(filters.ecoFriendly));
    if (filters.customizationType?.length) {
      filters.customizationType.forEach(t => params.append('customizationType', t));
    }
    if (filters.minRating) params.append('minRating', String(filters.minRating));
    if (filters.hasDiscount !== undefined) params.append('hasDiscount', String(filters.hasDiscount));
    if (filters.leadTime) params.append('leadTime', filters.leadTime);
    if (filters.sort) params.append('sort', filters.sort);
    if (filters.page) params.append('page', String(filters.page));
    if (filters.limit) params.append('limit', String(filters.limit));

    const response = await api.get<ProductListResponse>(`/products?${params.toString()}`);
    return response.data;
  },

  /**
   * Search products
   * FRD-002 Sub-Prompt 3: Product Search
   */
  searchProducts: async (query: string, page = 1, limit = 12): Promise<SearchResponse> => {
    const response = await api.get<SearchResponse>(
      `/products/search?q=${encodeURIComponent(query)}&page=${page}&limit=${limit}`
    );
    return response.data;
  },

  /**
   * Get autocomplete suggestions
   * FRD-002 Sub-Prompt 3: Product Search with Autocomplete
   */
  getAutocomplete: async (query: string): Promise<AutocompleteResponse> => {
    const response = await api.get<AutocompleteResponse>(
      `/products/autocomplete?q=${encodeURIComponent(query)}`
    );
    return response.data;
  },

  /**
   * Get product details
   * FRD-002 Sub-Prompt 4: Product Detail API
   * NOTE: Partner details are NOT included
   */
  getProduct: async (productIdOrSlug: string): Promise<ProductDetailResponse> => {
    const response = await api.get<ProductDetailResponse>(`/products/${productIdOrSlug}`);
    return response.data;
  },

  /**
   * Calculate price for quantity
   * FRD-002 Sub-Prompt 6: Dynamic Price Calculator
   */
  calculatePrice: async (
    productIdOrSlug: string,
    request: PriceCalculationRequest
  ): Promise<PriceCalculationResponse> => {
    const response = await api.post<PriceCalculationResponse>(
      `/products/${productIdOrSlug}/calculate-price`,
      request
    );
    return response.data;
  },

  /**
   * Get categories
   * FRD-002 FR-14: Category Structure
   */
  getCategories: async (): Promise<CategoryResponse> => {
    const response = await api.get<CategoryResponse>('/products/categories');
    return response.data;
  },
};
