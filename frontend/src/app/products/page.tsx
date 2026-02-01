'use client';

import { useState, useEffect, useCallback } from 'react';
import { useSearchParams, useRouter } from 'next/navigation';
import { motion } from 'framer-motion';
import ProductCard from '@/components/products/ProductCard';
import FilterPanel from '@/components/products/FilterPanel';
import QuickViewModal from '@/components/products/QuickViewModal';
import { productApi, ProductSummary, ProductFilters, Category, PaginationInfo } from '@/lib/product-api';

/**
 * Products Page
 * FRD-002 FR-17: Product Listing Page
 */

const SORT_OPTIONS = [
  { value: 'popular', label: 'Most Popular' },
  { value: 'newest', label: 'Newest Arrivals' },
  { value: 'price_asc', label: 'Price: Low to High' },
  { value: 'price_desc', label: 'Price: High to Low' },
  { value: 'rating', label: 'Highest Rated' },
];

export default function ProductsPage() {
  const searchParams = useSearchParams();
  const router = useRouter();

  // State
  const [products, setProducts] = useState<ProductSummary[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [pagination, setPagination] = useState<PaginationInfo | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showMobileFilters, setShowMobileFilters] = useState(false);
  const [quickViewProduct, setQuickViewProduct] = useState<ProductSummary | null>(null);
  const [viewMode, setViewMode] = useState<'grid' | 'list'>('grid');

  // Get filters from URL
  const getFiltersFromUrl = useCallback((): ProductFilters => {
    return {
      category: searchParams.get('category') || undefined,
      minPrice: searchParams.get('minPrice') ? Number(searchParams.get('minPrice')) : undefined,
      maxPrice: searchParams.get('maxPrice') ? Number(searchParams.get('maxPrice')) : undefined,
      material: searchParams.getAll('material').length > 0 ? searchParams.getAll('material') : undefined,
      ecoFriendly: searchParams.get('ecoFriendly') === 'true' ? true : undefined,
      customizationType: searchParams.getAll('customizationType').length > 0 ? searchParams.getAll('customizationType') : undefined,
      minRating: searchParams.get('minRating') ? Number(searchParams.get('minRating')) : undefined,
      hasDiscount: searchParams.get('hasDiscount') === 'true' ? true : undefined,
      leadTime: searchParams.get('leadTime') || undefined,
      sort: (searchParams.get('sort') as ProductFilters['sort']) || 'popular',
      page: searchParams.get('page') ? Number(searchParams.get('page')) : 1,
      limit: 12,
    };
  }, [searchParams]);

  const [filters, setFilters] = useState<ProductFilters>(getFiltersFromUrl());

  // Update URL when filters change
  const updateUrl = useCallback((newFilters: ProductFilters) => {
    const params = new URLSearchParams();
    
    if (newFilters.category) params.set('category', newFilters.category);
    if (newFilters.minPrice) params.set('minPrice', String(newFilters.minPrice));
    if (newFilters.maxPrice) params.set('maxPrice', String(newFilters.maxPrice));
    if (newFilters.material?.length) {
      newFilters.material.forEach(m => params.append('material', m));
    }
    if (newFilters.ecoFriendly) params.set('ecoFriendly', 'true');
    if (newFilters.customizationType?.length) {
      newFilters.customizationType.forEach(t => params.append('customizationType', t));
    }
    if (newFilters.minRating) params.set('minRating', String(newFilters.minRating));
    if (newFilters.hasDiscount) params.set('hasDiscount', 'true');
    if (newFilters.leadTime) params.set('leadTime', newFilters.leadTime);
    if (newFilters.sort && newFilters.sort !== 'popular') params.set('sort', newFilters.sort);
    if (newFilters.page && newFilters.page > 1) params.set('page', String(newFilters.page));

    const queryString = params.toString();
    router.push(queryString ? `/products?${queryString}` : '/products', { scroll: false });
  }, [router]);

  // Handle filter change
  const handleFilterChange = (newFilters: ProductFilters) => {
    const updated = { ...newFilters, page: 1 }; // Reset to page 1 on filter change
    setFilters(updated);
    updateUrl(updated);
  };

  // Handle sort change
  const handleSortChange = (sort: string) => {
    const updated = { ...filters, sort: sort as ProductFilters['sort'], page: 1 };
    setFilters(updated);
    updateUrl(updated);
  };

  // Handle page change
  const handlePageChange = (page: number) => {
    const updated = { ...filters, page };
    setFilters(updated);
    updateUrl(updated);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  // Load categories
  useEffect(() => {
    const loadCategories = async () => {
      try {
        const response = await productApi.getCategories();
        setCategories(response.data);
      } catch (err) {
        console.error('Failed to load categories:', err);
      }
    };
    loadCategories();
  }, []);

  // Load products
  useEffect(() => {
    const loadProducts = async () => {
      setIsLoading(true);
      setError(null);

      try {
        const response = await productApi.getProducts(filters);
        setProducts(response.data.products);
        setPagination(response.data.pagination);
      } catch (err) {
        console.error('Failed to load products:', err);
        setError('Unable to load products. Please try again.');
      } finally {
        setIsLoading(false);
      }
    };

    loadProducts();
  }, [filters]);

  // Update filters when URL changes
  useEffect(() => {
    setFilters(getFiltersFromUrl());
  }, [searchParams, getFiltersFromUrl]);

  // Active filter chips
  const getActiveFilterChips = () => {
    const chips: { key: string; label: string; onRemove: () => void }[] = [];

    if (filters.category) {
      const cat = categories.find(c => c.slug === filters.category);
      chips.push({
        key: 'category',
        label: cat?.name || filters.category,
        onRemove: () => handleFilterChange({ ...filters, category: undefined }),
      });
    }

    if (filters.minPrice || filters.maxPrice) {
      chips.push({
        key: 'price',
        label: `₹${filters.minPrice || 0} - ₹${filters.maxPrice || 10000}`,
        onRemove: () => handleFilterChange({ ...filters, minPrice: undefined, maxPrice: undefined }),
      });
    }

    if (filters.material?.length) {
      filters.material.forEach(m => {
        chips.push({
          key: `material-${m}`,
          label: m,
          onRemove: () => handleFilterChange({
            ...filters,
            material: filters.material?.filter(mat => mat !== m),
          }),
        });
      });
    }

    if (filters.ecoFriendly) {
      chips.push({
        key: 'eco',
        label: '🌿 Eco-Friendly',
        onRemove: () => handleFilterChange({ ...filters, ecoFriendly: undefined }),
      });
    }

    if (filters.hasDiscount) {
      chips.push({
        key: 'discount',
        label: '🏷️ On Sale',
        onRemove: () => handleFilterChange({ ...filters, hasDiscount: undefined }),
      });
    }

    if (filters.minRating) {
      chips.push({
        key: 'rating',
        label: `${filters.minRating}+ Stars`,
        onRemove: () => handleFilterChange({ ...filters, minRating: undefined }),
      });
    }

    return chips;
  };

  const activeChips = getActiveFilterChips();

  return (
    <div className="min-h-screen bg-gradient-to-b from-slate-50 to-white">
      {/* Header */}
      <div className="bg-gradient-to-r from-amber-500 to-amber-600 text-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
          <h1 className="text-3xl md:text-4xl font-bold">
            {filters.category 
              ? categories.find(c => c.slug === filters.category)?.name || 'Products'
              : 'All Products'}
          </h1>
          <p className="mt-2 text-amber-100">
            Discover customizable promotional merchandise for your brand
          </p>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="flex flex-col lg:flex-row gap-8">
          {/* Sidebar Filters (Desktop) */}
          <div className="hidden lg:block w-72 flex-shrink-0">
            <div className="sticky top-4">
              <FilterPanel
                filters={filters}
                categories={categories}
                onFilterChange={handleFilterChange}
              />
            </div>
          </div>

          {/* Main Content */}
          <div className="flex-1">
            {/* Toolbar */}
            <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 mb-6">
              <div className="flex items-center gap-4">
                {/* Mobile Filter Button */}
                <button
                  onClick={() => setShowMobileFilters(true)}
                  className="lg:hidden flex items-center gap-2 px-4 py-2 bg-white border border-slate-200 rounded-lg text-sm font-medium hover:bg-slate-50"
                >
                  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z" />
                  </svg>
                  Filters
                  {activeChips.length > 0 && (
                    <span className="px-2 py-0.5 bg-amber-500 text-white text-xs rounded-full">
                      {activeChips.length}
                    </span>
                  )}
                </button>

                {/* Results Count */}
                <p className="text-sm text-slate-600">
                  {pagination?.totalProducts || 0} products
                </p>
              </div>

              <div className="flex items-center gap-4">
                {/* View Toggle */}
                <div className="hidden sm:flex border border-slate-200 rounded-lg overflow-hidden">
                  <button
                    onClick={() => setViewMode('grid')}
                    className={`p-2 ${viewMode === 'grid' ? 'bg-amber-500 text-white' : 'bg-white text-slate-600 hover:bg-slate-50'}`}
                  >
                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z" />
                    </svg>
                  </button>
                  <button
                    onClick={() => setViewMode('list')}
                    className={`p-2 ${viewMode === 'list' ? 'bg-amber-500 text-white' : 'bg-white text-slate-600 hover:bg-slate-50'}`}
                  >
                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 10h16M4 14h16M4 18h16" />
                    </svg>
                  </button>
                </div>

                {/* Sort */}
                <select
                  value={filters.sort || 'popular'}
                  onChange={(e) => handleSortChange(e.target.value)}
                  className="px-4 py-2 bg-white border border-slate-200 rounded-lg text-sm focus:ring-2 focus:ring-amber-500 focus:border-transparent"
                >
                  {SORT_OPTIONS.map((option) => (
                    <option key={option.value} value={option.value}>
                      {option.label}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            {/* Active Filters Chips */}
            {activeChips.length > 0 && (
              <div className="flex flex-wrap gap-2 mb-6">
                {activeChips.map((chip) => (
                  <span
                    key={chip.key}
                    className="inline-flex items-center gap-1 px-3 py-1 bg-amber-100 text-amber-800 text-sm rounded-full"
                  >
                    {chip.label}
                    <button
                      onClick={chip.onRemove}
                      className="p-0.5 hover:bg-amber-200 rounded-full"
                    >
                      <svg className="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                      </svg>
                    </button>
                  </span>
                ))}
                <button
                  onClick={() => handleFilterChange({})}
                  className="text-sm text-amber-600 hover:text-amber-700 font-medium"
                >
                  Clear all
                </button>
              </div>
            )}

            {/* Loading State */}
            {isLoading && (
              <div className={`grid gap-6 ${
                viewMode === 'grid'
                  ? 'grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4'
                  : 'grid-cols-1'
              }`}>
                {[...Array(12)].map((_, i) => (
                  <div key={i} className="bg-white rounded-xl overflow-hidden shadow-sm animate-pulse">
                    <div className="aspect-square bg-slate-200" />
                    <div className="p-4 space-y-3">
                      <div className="h-4 bg-slate-200 rounded w-1/4" />
                      <div className="h-5 bg-slate-200 rounded w-3/4" />
                      <div className="h-4 bg-slate-200 rounded w-1/2" />
                      <div className="h-6 bg-slate-200 rounded w-1/3" />
                    </div>
                  </div>
                ))}
              </div>
            )}

            {/* Error State */}
            {error && !isLoading && (
              <div className="text-center py-12">
                <svg className="w-16 h-16 text-slate-300 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
                </svg>
                <h3 className="text-lg font-medium text-slate-800">{error}</h3>
                <button
                  onClick={() => setFilters({ ...filters })}
                  className="mt-4 px-4 py-2 bg-amber-500 text-white rounded-lg hover:bg-amber-600"
                >
                  Try Again
                </button>
              </div>
            )}

            {/* Empty State */}
            {!isLoading && !error && products.length === 0 && (
              <div className="text-center py-12">
                <svg className="w-16 h-16 text-slate-300 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1} d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4" />
                </svg>
                <h3 className="text-lg font-medium text-slate-800">No products found</h3>
                <p className="text-slate-500 mt-1">Try adjusting your filters or browse all products</p>
                <button
                  onClick={() => handleFilterChange({})}
                  className="mt-4 px-4 py-2 bg-amber-500 text-white rounded-lg hover:bg-amber-600"
                >
                  Clear Filters
                </button>
              </div>
            )}

            {/* Products Grid */}
            {!isLoading && !error && products.length > 0 && (
              <>
                <motion.div
                  className={`grid gap-6 ${
                    viewMode === 'grid'
                      ? 'grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4'
                      : 'grid-cols-1'
                  }`}
                  initial="hidden"
                  animate="visible"
                  variants={{
                    hidden: { opacity: 0 },
                    visible: {
                      opacity: 1,
                      transition: { staggerChildren: 0.05 },
                    },
                  }}
                >
                  {products.map((product) => (
                    <ProductCard
                      key={product.productId}
                      product={product}
                      onQuickView={setQuickViewProduct}
                    />
                  ))}
                </motion.div>

                {/* Pagination */}
                {pagination && pagination.totalPages > 1 && (
                  <div className="mt-12 flex items-center justify-center gap-2">
                    <button
                      onClick={() => handlePageChange(pagination.currentPage - 1)}
                      disabled={!pagination.hasPrevious}
                      className="px-4 py-2 border border-slate-200 rounded-lg text-sm font-medium disabled:opacity-50 disabled:cursor-not-allowed hover:bg-slate-50"
                    >
                      Previous
                    </button>

                    {[...Array(Math.min(pagination.totalPages, 5))].map((_, i) => {
                      let pageNum: number;
                      if (pagination.totalPages <= 5) {
                        pageNum = i + 1;
                      } else if (pagination.currentPage <= 3) {
                        pageNum = i + 1;
                      } else if (pagination.currentPage >= pagination.totalPages - 2) {
                        pageNum = pagination.totalPages - 4 + i;
                      } else {
                        pageNum = pagination.currentPage - 2 + i;
                      }

                      return (
                        <button
                          key={pageNum}
                          onClick={() => handlePageChange(pageNum)}
                          className={`w-10 h-10 rounded-lg text-sm font-medium ${
                            pagination.currentPage === pageNum
                              ? 'bg-amber-500 text-white'
                              : 'border border-slate-200 hover:bg-slate-50'
                          }`}
                        >
                          {pageNum}
                        </button>
                      );
                    })}

                    <button
                      onClick={() => handlePageChange(pagination.currentPage + 1)}
                      disabled={!pagination.hasNext}
                      className="px-4 py-2 border border-slate-200 rounded-lg text-sm font-medium disabled:opacity-50 disabled:cursor-not-allowed hover:bg-slate-50"
                    >
                      Next
                    </button>
                  </div>
                )}
              </>
            )}
          </div>
        </div>
      </div>

      {/* Mobile Filter Panel */}
      <FilterPanel
        filters={filters}
        categories={categories}
        onFilterChange={handleFilterChange}
        isMobile
        isOpen={showMobileFilters}
        onClose={() => setShowMobileFilters(false)}
      />

      {/* Quick View Modal */}
      <QuickViewModal
        product={quickViewProduct}
        isOpen={!!quickViewProduct}
        onClose={() => setQuickViewProduct(null)}
      />
    </div>
  );
}
