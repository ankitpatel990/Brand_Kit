'use client';

import { useState, useEffect, Suspense } from 'react';
import { useSearchParams } from 'next/navigation';
import Link from 'next/link';
import ProductCard from '@/components/products/ProductCard';
import QuickViewModal from '@/components/products/QuickViewModal';
import SearchBar from '@/components/products/SearchBar';
import { productApi, ProductSummary, PaginationInfo, CategorySuggestion } from '@/lib/product-api';

/**
 * Search Results Page
 * FRD-002 FR-18: Search Functionality
 */

function SearchContent() {
  const searchParams = useSearchParams();
  const query = searchParams.get('q') || '';
  const page = parseInt(searchParams.get('page') || '1', 10);

  const [products, setProducts] = useState<ProductSummary[]>([]);
  const [pagination, setPagination] = useState<PaginationInfo | null>(null);
  const [suggestions, setSuggestions] = useState<string[]>([]);
  const [categorySuggestions, setCategorySuggestions] = useState<CategorySuggestion[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [quickViewProduct, setQuickViewProduct] = useState<ProductSummary | null>(null);

  useEffect(() => {
    const search = async () => {
      if (!query || query.length < 2) {
        setProducts([]);
        setIsLoading(false);
        return;
      }

      setIsLoading(true);
      try {
        const response = await productApi.searchProducts(query, page, 12);
        setProducts(response.data.products);
        setPagination(response.data.pagination);
        setSuggestions(response.data.suggestions);
        setCategorySuggestions(response.data.categorySuggestions);
      } catch (err) {
        console.error('Search failed:', err);
      } finally {
        setIsLoading(false);
      }
    };

    search();
  }, [query, page]);

  return (
    <div className="min-h-screen bg-gradient-to-b from-slate-50 to-white">
      {/* Header */}
      <div className="bg-gradient-to-r from-amber-500 to-amber-600 text-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="max-w-2xl mx-auto">
            <SearchBar
              className="w-full"
              placeholder="Search products..."
              autoFocus
            />
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {query ? (
          <>
            {/* Results Header */}
            <div className="mb-8">
              <h1 className="text-2xl font-bold text-slate-800">
                {isLoading ? (
                  'Searching...'
                ) : products.length > 0 ? (
                  <>
                    {pagination?.totalProducts} results for "{query}"
                  </>
                ) : (
                  `No results found for "${query}"`
                )}
              </h1>
            </div>

            {/* Loading State */}
            {isLoading && (
              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
                {[...Array(8)].map((_, i) => (
                  <div key={i} className="bg-white rounded-xl overflow-hidden shadow-sm animate-pulse">
                    <div className="aspect-square bg-slate-200" />
                    <div className="p-4 space-y-3">
                      <div className="h-4 bg-slate-200 rounded w-1/4" />
                      <div className="h-5 bg-slate-200 rounded w-3/4" />
                      <div className="h-4 bg-slate-200 rounded w-1/2" />
                    </div>
                  </div>
                ))}
              </div>
            )}

            {/* No Results */}
            {!isLoading && products.length === 0 && (
              <div className="text-center py-16">
                <svg
                  className="w-20 h-20 text-slate-300 mx-auto mb-6"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={1}
                    d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
                  />
                </svg>
                <h2 className="text-xl font-semibold text-slate-800 mb-2">
                  No products found for "{query}"
                </h2>
                <p className="text-slate-500 mb-6">
                  Try different keywords or browse our categories
                </p>

                {/* Suggestions */}
                {suggestions.length > 0 && (
                  <div className="mb-8">
                    <p className="text-slate-600 mb-3">Did you mean:</p>
                    <div className="flex flex-wrap justify-center gap-2">
                      {suggestions.map((suggestion) => (
                        <Link
                          key={suggestion}
                          href={`/products/search?q=${encodeURIComponent(suggestion)}`}
                          className="px-4 py-2 bg-amber-100 text-amber-700 rounded-full hover:bg-amber-200 transition-colors"
                        >
                          {suggestion}
                        </Link>
                      ))}
                    </div>
                  </div>
                )}

                {/* Category Suggestions */}
                {categorySuggestions.length > 0 && (
                  <div>
                    <p className="text-slate-600 mb-3">Browse by category:</p>
                    <div className="flex flex-wrap justify-center gap-2">
                      {categorySuggestions.map((category) => (
                        <Link
                          key={category.slug}
                          href={`/products?category=${category.slug}`}
                          className="px-4 py-2 bg-slate-100 text-slate-700 rounded-full hover:bg-slate-200 transition-colors"
                        >
                          {category.name} ({category.productCount})
                        </Link>
                      ))}
                    </div>
                  </div>
                )}
              </div>
            )}

            {/* Results Grid */}
            {!isLoading && products.length > 0 && (
              <>
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
                  {products.map((product) => (
                    <ProductCard
                      key={product.productId}
                      product={product}
                      onQuickView={setQuickViewProduct}
                    />
                  ))}
                </div>

                {/* Pagination */}
                {pagination && pagination.totalPages > 1 && (
                  <div className="mt-12 flex items-center justify-center gap-2">
                    <Link
                      href={`/products/search?q=${encodeURIComponent(query)}&page=${page - 1}`}
                      className={`px-4 py-2 border border-slate-200 rounded-lg text-sm font-medium ${
                        !pagination.hasPrevious
                          ? 'opacity-50 pointer-events-none'
                          : 'hover:bg-slate-50'
                      }`}
                    >
                      Previous
                    </Link>

                    <span className="px-4 py-2 text-sm text-slate-600">
                      Page {pagination.currentPage} of {pagination.totalPages}
                    </span>

                    <Link
                      href={`/products/search?q=${encodeURIComponent(query)}&page=${page + 1}`}
                      className={`px-4 py-2 border border-slate-200 rounded-lg text-sm font-medium ${
                        !pagination.hasNext
                          ? 'opacity-50 pointer-events-none'
                          : 'hover:bg-slate-50'
                      }`}
                    >
                      Next
                    </Link>
                  </div>
                )}
              </>
            )}
          </>
        ) : (
          <div className="text-center py-16">
            <svg
              className="w-20 h-20 text-slate-300 mx-auto mb-6"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={1}
                d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
              />
            </svg>
            <h2 className="text-xl font-semibold text-slate-800 mb-2">
              Enter a search term
            </h2>
            <p className="text-slate-500">
              Type at least 2 characters to search our product catalog
            </p>
          </div>
        )}
      </div>

      {/* Quick View Modal */}
      <QuickViewModal
        product={quickViewProduct}
        isOpen={!!quickViewProduct}
        onClose={() => setQuickViewProduct(null)}
      />
    </div>
  );
}

export default function SearchPage() {
  return (
    <Suspense fallback={
      <div className="min-h-screen bg-slate-50 flex items-center justify-center">
        <div className="animate-spin w-8 h-8 border-4 border-amber-500 border-t-transparent rounded-full" />
      </div>
    }>
      <SearchContent />
    </Suspense>
  );
}
