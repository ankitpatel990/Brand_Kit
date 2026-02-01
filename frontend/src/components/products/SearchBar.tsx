'use client';

import { useState, useEffect, useRef, useCallback } from 'react';
import { useRouter } from 'next/navigation';
import { motion, AnimatePresence } from 'framer-motion';
import { productApi, AutocompleteSuggestion, CategorySuggestion } from '@/lib/product-api';

/**
 * Search Bar Component with Autocomplete
 * FRD-002 Sub-Prompt 3: Product Search with Autocomplete
 */

interface SearchBarProps {
  className?: string;
  placeholder?: string;
  autoFocus?: boolean;
}

export default function SearchBar({
  className = '',
  placeholder = 'Search products...',
  autoFocus = false,
}: SearchBarProps) {
  const router = useRouter();
  const inputRef = useRef<HTMLInputElement>(null);
  const containerRef = useRef<HTMLDivElement>(null);

  const [query, setQuery] = useState('');
  const [isOpen, setIsOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [products, setProducts] = useState<AutocompleteSuggestion[]>([]);
  const [categories, setCategories] = useState<CategorySuggestion[]>([]);
  const [selectedIndex, setSelectedIndex] = useState(-1);

  // Debounced search
  const fetchSuggestions = useCallback(async (searchQuery: string) => {
    if (searchQuery.length < 2) {
      setProducts([]);
      setCategories([]);
      return;
    }

    setIsLoading(true);
    try {
      const response = await productApi.getAutocomplete(searchQuery);
      setProducts(response.data.products);
      setCategories(response.data.categories);
    } catch (err) {
      console.error('Autocomplete error:', err);
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    const debounce = setTimeout(() => {
      if (query) {
        fetchSuggestions(query);
      }
    }, 300);

    return () => clearTimeout(debounce);
  }, [query, fetchSuggestions]);

  // Handle click outside
  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (containerRef.current && !containerRef.current.contains(e.target as Node)) {
        setIsOpen(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (query.trim().length >= 2) {
      router.push(`/products/search?q=${encodeURIComponent(query.trim())}`);
      setIsOpen(false);
      inputRef.current?.blur();
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    const totalItems = products.length + categories.length;

    switch (e.key) {
      case 'ArrowDown':
        e.preventDefault();
        setSelectedIndex((prev) => (prev < totalItems - 1 ? prev + 1 : prev));
        break;
      case 'ArrowUp':
        e.preventDefault();
        setSelectedIndex((prev) => (prev > 0 ? prev - 1 : -1));
        break;
      case 'Enter':
        e.preventDefault();
        if (selectedIndex >= 0) {
          if (selectedIndex < products.length) {
            router.push(`/products/${products[selectedIndex].slug}`);
          } else {
            const catIndex = selectedIndex - products.length;
            router.push(`/products?category=${categories[catIndex].slug}`);
          }
          setIsOpen(false);
          inputRef.current?.blur();
        } else {
          handleSubmit(e);
        }
        break;
      case 'Escape':
        setIsOpen(false);
        inputRef.current?.blur();
        break;
    }
  };

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0,
    }).format(price);
  };

  const showDropdown = isOpen && (products.length > 0 || categories.length > 0 || isLoading || query.length >= 2);

  return (
    <div ref={containerRef} className={`relative ${className}`}>
      <form onSubmit={handleSubmit}>
        <div className="relative">
          <input
            ref={inputRef}
            type="text"
            value={query}
            onChange={(e) => {
              setQuery(e.target.value);
              setIsOpen(true);
              setSelectedIndex(-1);
            }}
            onFocus={() => setIsOpen(true)}
            onKeyDown={handleKeyDown}
            placeholder={placeholder}
            autoFocus={autoFocus}
            className="w-full px-4 py-3 pl-12 bg-white border border-slate-200 rounded-xl text-slate-800 placeholder-slate-400 focus:ring-2 focus:ring-amber-500 focus:border-transparent transition-all"
          />
          <svg
            className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-400"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
            />
          </svg>

          {query && (
            <button
              type="button"
              onClick={() => {
                setQuery('');
                setProducts([]);
                setCategories([]);
                inputRef.current?.focus();
              }}
              className="absolute right-4 top-1/2 -translate-y-1/2 p-1 text-slate-400 hover:text-slate-600"
            >
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          )}
        </div>
      </form>

      {/* Dropdown */}
      <AnimatePresence>
        {showDropdown && (
          <motion.div
            className="absolute top-full left-0 right-0 mt-2 bg-white rounded-xl shadow-xl border border-slate-100 overflow-hidden z-50"
            initial={{ opacity: 0, y: -10 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -10 }}
            transition={{ duration: 0.15 }}
          >
            {isLoading && (
              <div className="p-4 text-center text-slate-500">
                <svg className="animate-spin w-5 h-5 mx-auto" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                </svg>
              </div>
            )}

            {!isLoading && products.length === 0 && categories.length === 0 && query.length >= 2 && (
              <div className="p-4 text-center text-slate-500">
                No results found for "{query}"
              </div>
            )}

            {/* Products */}
            {products.length > 0 && (
              <div className="p-2">
                <p className="px-3 py-2 text-xs font-semibold text-slate-500 uppercase tracking-wide">
                  Products
                </p>
                {products.map((product, index) => (
                  <button
                    key={product.productId}
                    onClick={() => {
                      router.push(`/products/${product.slug}`);
                      setIsOpen(false);
                    }}
                    className={`w-full flex items-center gap-3 px-3 py-2 rounded-lg text-left transition-colors ${
                      selectedIndex === index ? 'bg-amber-50' : 'hover:bg-slate-50'
                    }`}
                  >
                    {product.imageUrl ? (
                      <img
                        src={product.imageUrl}
                        alt={product.name}
                        className="w-10 h-10 rounded object-cover"
                      />
                    ) : (
                      <div className="w-10 h-10 rounded bg-slate-100 flex items-center justify-center">
                        <svg className="w-5 h-5 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1} d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                        </svg>
                      </div>
                    )}
                    <div className="flex-1 min-w-0">
                      <p className="font-medium text-slate-800 truncate">{product.name}</p>
                      <p className="text-sm text-slate-500">{product.category}</p>
                    </div>
                    <span className="text-sm font-medium text-amber-600">
                      {formatPrice(product.basePrice)}
                    </span>
                  </button>
                ))}
              </div>
            )}

            {/* Categories */}
            {categories.length > 0 && (
              <div className="p-2 border-t border-slate-100">
                <p className="px-3 py-2 text-xs font-semibold text-slate-500 uppercase tracking-wide">
                  Categories
                </p>
                {categories.map((category, index) => (
                  <button
                    key={category.slug}
                    onClick={() => {
                      router.push(`/products?category=${category.slug}`);
                      setIsOpen(false);
                    }}
                    className={`w-full flex items-center justify-between px-3 py-2 rounded-lg text-left transition-colors ${
                      selectedIndex === products.length + index ? 'bg-amber-50' : 'hover:bg-slate-50'
                    }`}
                  >
                    <span className="font-medium text-slate-800">{category.name}</span>
                    <span className="text-sm text-slate-500">{category.productCount} products</span>
                  </button>
                ))}
              </div>
            )}

            {/* Search All */}
            {query.length >= 2 && (
              <div className="p-2 border-t border-slate-100">
                <button
                  onClick={() => {
                    router.push(`/products/search?q=${encodeURIComponent(query)}`);
                    setIsOpen(false);
                  }}
                  className="w-full px-3 py-2 text-amber-600 hover:bg-amber-50 rounded-lg text-left font-medium"
                >
                  Search all products for "{query}"
                </button>
              </div>
            )}
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}
