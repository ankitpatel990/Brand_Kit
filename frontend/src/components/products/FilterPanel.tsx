'use client';

import { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { ProductFilters, Category } from '@/lib/product-api';

/**
 * Filter Panel Component
 * FRD-002 FR-19: Filtering System
 */

interface FilterPanelProps {
  filters: ProductFilters;
  categories: Category[];
  onFilterChange: (filters: ProductFilters) => void;
  isMobile?: boolean;
  isOpen?: boolean;
  onClose?: () => void;
}

const MATERIALS = [
  'Cotton',
  'Polyester',
  'Stainless Steel',
  'Plastic',
  'Paper',
  'Glass',
  'Bamboo',
  'Jute',
  'Other',
];

const CUSTOMIZATION_TYPES = [
  { value: 'LOGO_PRINT', label: 'Logo Print' },
  { value: 'EMBROIDERY', label: 'Embroidery' },
  { value: 'ENGRAVING', label: 'Engraving' },
];

const LEAD_TIMES = [
  { value: '<7', label: 'Less than 7 days' },
  { value: '7-14', label: '7-14 days' },
  { value: '14+', label: 'More than 14 days' },
];

const RATING_OPTIONS = [
  { value: 5, label: '5 Stars' },
  { value: 4, label: '4+ Stars' },
  { value: 3, label: '3+ Stars' },
];

export default function FilterPanel({
  filters,
  categories,
  onFilterChange,
  isMobile = false,
  isOpen = true,
  onClose,
}: FilterPanelProps) {
  const [priceRange, setPriceRange] = useState({
    min: filters.minPrice || 0,
    max: filters.maxPrice || 10000,
  });

  const [expandedSections, setExpandedSections] = useState({
    categories: true,
    price: true,
    materials: true,
    customization: false,
    rating: false,
    leadTime: false,
    other: false,
  });

  const toggleSection = (section: keyof typeof expandedSections) => {
    setExpandedSections((prev) => ({ ...prev, [section]: !prev[section] }));
  };

  const handlePriceChange = () => {
    onFilterChange({
      ...filters,
      minPrice: priceRange.min || undefined,
      maxPrice: priceRange.max || undefined,
    });
  };

  const toggleMaterial = (material: string) => {
    const current = filters.material || [];
    const updated = current.includes(material)
      ? current.filter((m) => m !== material)
      : [...current, material];
    onFilterChange({ ...filters, material: updated.length > 0 ? updated : undefined });
  };

  const toggleCustomizationType = (type: string) => {
    const current = filters.customizationType || [];
    const updated = current.includes(type)
      ? current.filter((t) => t !== type)
      : [...current, type];
    onFilterChange({ ...filters, customizationType: updated.length > 0 ? updated : undefined });
  };

  const clearAllFilters = () => {
    setPriceRange({ min: 0, max: 10000 });
    onFilterChange({});
  };

  const activeFilterCount = [
    filters.category,
    filters.minPrice || filters.maxPrice,
    filters.material?.length,
    filters.customizationType?.length,
    filters.ecoFriendly,
    filters.minRating,
    filters.leadTime,
    filters.hasDiscount,
  ].filter(Boolean).length;

  const content = (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <h2 className="text-lg font-semibold text-slate-800">Filters</h2>
        {activeFilterCount > 0 && (
          <button
            onClick={clearAllFilters}
            className="text-sm text-amber-600 hover:text-amber-700 font-medium"
          >
            Clear All ({activeFilterCount})
          </button>
        )}
      </div>

      {/* Categories */}
      <FilterSection
        title="Categories"
        isExpanded={expandedSections.categories}
        onToggle={() => toggleSection('categories')}
      >
        <div className="space-y-2">
          {categories.map((category) => (
            <label
              key={category.slug}
              className="flex items-center justify-between cursor-pointer group"
            >
              <div className="flex items-center">
                <input
                  type="radio"
                  name="category"
                  checked={filters.category === category.slug}
                  onChange={() =>
                    onFilterChange({
                      ...filters,
                      category: filters.category === category.slug ? undefined : category.slug,
                    })
                  }
                  className="w-4 h-4 text-amber-500 focus:ring-amber-500 border-slate-300"
                />
                <span className="ml-3 text-sm text-slate-600 group-hover:text-slate-800">
                  {category.name}
                </span>
              </div>
              <span className="text-xs text-slate-400">{category.productCount}</span>
            </label>
          ))}
        </div>
      </FilterSection>

      {/* Price Range */}
      <FilterSection
        title="Price Range"
        isExpanded={expandedSections.price}
        onToggle={() => toggleSection('price')}
      >
        <div className="space-y-4">
          <div className="flex gap-3">
            <div className="flex-1">
              <label className="text-xs text-slate-500">Min (₹)</label>
              <input
                type="number"
                value={priceRange.min}
                onChange={(e) => setPriceRange({ ...priceRange, min: Number(e.target.value) })}
                onBlur={handlePriceChange}
                className="w-full mt-1 px-3 py-2 border border-slate-200 rounded-lg text-sm focus:ring-2 focus:ring-amber-500 focus:border-transparent"
                min={0}
              />
            </div>
            <div className="flex-1">
              <label className="text-xs text-slate-500">Max (₹)</label>
              <input
                type="number"
                value={priceRange.max}
                onChange={(e) => setPriceRange({ ...priceRange, max: Number(e.target.value) })}
                onBlur={handlePriceChange}
                className="w-full mt-1 px-3 py-2 border border-slate-200 rounded-lg text-sm focus:ring-2 focus:ring-amber-500 focus:border-transparent"
                min={0}
              />
            </div>
          </div>
          <input
            type="range"
            min={0}
            max={10000}
            value={priceRange.max}
            onChange={(e) => {
              setPriceRange({ ...priceRange, max: Number(e.target.value) });
            }}
            onMouseUp={handlePriceChange}
            onTouchEnd={handlePriceChange}
            className="w-full h-2 bg-slate-200 rounded-lg appearance-none cursor-pointer accent-amber-500"
          />
        </div>
      </FilterSection>

      {/* Materials */}
      <FilterSection
        title="Material"
        isExpanded={expandedSections.materials}
        onToggle={() => toggleSection('materials')}
      >
        <div className="space-y-2">
          {MATERIALS.map((material) => (
            <label key={material} className="flex items-center cursor-pointer group">
              <input
                type="checkbox"
                checked={filters.material?.includes(material) || false}
                onChange={() => toggleMaterial(material)}
                className="w-4 h-4 text-amber-500 focus:ring-amber-500 border-slate-300 rounded"
              />
              <span className="ml-3 text-sm text-slate-600 group-hover:text-slate-800">
                {material}
              </span>
            </label>
          ))}
        </div>
      </FilterSection>

      {/* Customization */}
      <FilterSection
        title="Customization"
        isExpanded={expandedSections.customization}
        onToggle={() => toggleSection('customization')}
      >
        <div className="space-y-2">
          {CUSTOMIZATION_TYPES.map((type) => (
            <label key={type.value} className="flex items-center cursor-pointer group">
              <input
                type="checkbox"
                checked={filters.customizationType?.includes(type.value) || false}
                onChange={() => toggleCustomizationType(type.value)}
                className="w-4 h-4 text-amber-500 focus:ring-amber-500 border-slate-300 rounded"
              />
              <span className="ml-3 text-sm text-slate-600 group-hover:text-slate-800">
                {type.label}
              </span>
            </label>
          ))}
        </div>
      </FilterSection>

      {/* Rating */}
      <FilterSection
        title="Minimum Rating"
        isExpanded={expandedSections.rating}
        onToggle={() => toggleSection('rating')}
      >
        <div className="space-y-2">
          {RATING_OPTIONS.map((option) => (
            <label key={option.value} className="flex items-center cursor-pointer group">
              <input
                type="radio"
                name="rating"
                checked={filters.minRating === option.value}
                onChange={() =>
                  onFilterChange({
                    ...filters,
                    minRating: filters.minRating === option.value ? undefined : option.value,
                  })
                }
                className="w-4 h-4 text-amber-500 focus:ring-amber-500 border-slate-300"
              />
              <span className="ml-3 flex items-center text-sm text-slate-600 group-hover:text-slate-800">
                {[...Array(5)].map((_, i) => (
                  <svg
                    key={i}
                    className={`w-4 h-4 ${i < option.value ? 'text-amber-400' : 'text-slate-200'}`}
                    fill="currentColor"
                    viewBox="0 0 20 20"
                  >
                    <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                  </svg>
                ))}
                <span className="ml-1">& up</span>
              </span>
            </label>
          ))}
        </div>
      </FilterSection>

      {/* Lead Time */}
      <FilterSection
        title="Lead Time"
        isExpanded={expandedSections.leadTime}
        onToggle={() => toggleSection('leadTime')}
      >
        <div className="space-y-2">
          {LEAD_TIMES.map((option) => (
            <label key={option.value} className="flex items-center cursor-pointer group">
              <input
                type="radio"
                name="leadTime"
                checked={filters.leadTime === option.value}
                onChange={() =>
                  onFilterChange({
                    ...filters,
                    leadTime: filters.leadTime === option.value ? undefined : option.value,
                  })
                }
                className="w-4 h-4 text-amber-500 focus:ring-amber-500 border-slate-300"
              />
              <span className="ml-3 text-sm text-slate-600 group-hover:text-slate-800">
                {option.label}
              </span>
            </label>
          ))}
        </div>
      </FilterSection>

      {/* Other Options */}
      <FilterSection
        title="Other Options"
        isExpanded={expandedSections.other}
        onToggle={() => toggleSection('other')}
      >
        <div className="space-y-3">
          <label className="flex items-center cursor-pointer group">
            <input
              type="checkbox"
              checked={filters.ecoFriendly || false}
              onChange={() =>
                onFilterChange({ ...filters, ecoFriendly: !filters.ecoFriendly || undefined })
              }
              className="w-4 h-4 text-amber-500 focus:ring-amber-500 border-slate-300 rounded"
            />
            <span className="ml-3 text-sm text-slate-600 group-hover:text-slate-800">
              🌿 Eco-Friendly Only
            </span>
          </label>
          <label className="flex items-center cursor-pointer group">
            <input
              type="checkbox"
              checked={filters.hasDiscount || false}
              onChange={() =>
                onFilterChange({ ...filters, hasDiscount: !filters.hasDiscount || undefined })
              }
              className="w-4 h-4 text-amber-500 focus:ring-amber-500 border-slate-300 rounded"
            />
            <span className="ml-3 text-sm text-slate-600 group-hover:text-slate-800">
              🏷️ On Sale
            </span>
          </label>
        </div>
      </FilterSection>
    </div>
  );

  // Mobile: Bottom sheet
  if (isMobile) {
    return (
      <AnimatePresence>
        {isOpen && (
          <>
            <motion.div
              className="fixed inset-0 bg-black/50 z-40"
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              onClick={onClose}
            />
            <motion.div
              className="fixed bottom-0 left-0 right-0 bg-white rounded-t-2xl z-50 max-h-[80vh] overflow-y-auto"
              initial={{ y: '100%' }}
              animate={{ y: 0 }}
              exit={{ y: '100%' }}
              transition={{ type: 'spring', damping: 25, stiffness: 300 }}
            >
              <div className="sticky top-0 bg-white border-b border-slate-100 p-4 flex items-center justify-between">
                <h2 className="text-lg font-semibold">Filters</h2>
                <button onClick={onClose} className="p-2 hover:bg-slate-100 rounded-full">
                  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
              </div>
              <div className="p-4">{content}</div>
              <div className="sticky bottom-0 bg-white border-t border-slate-100 p-4">
                <button
                  onClick={onClose}
                  className="w-full py-3 bg-amber-500 text-white font-semibold rounded-xl hover:bg-amber-600 transition-colors"
                >
                  Apply Filters
                </button>
              </div>
            </motion.div>
          </>
        )}
      </AnimatePresence>
    );
  }

  // Desktop: Sidebar
  return <div className="bg-white rounded-xl p-6 shadow-sm">{content}</div>;
}

// Filter Section Component
function FilterSection({
  title,
  isExpanded,
  onToggle,
  children,
}: {
  title: string;
  isExpanded: boolean;
  onToggle: () => void;
  children: React.ReactNode;
}) {
  return (
    <div className="border-b border-slate-100 pb-4">
      <button
        onClick={onToggle}
        className="w-full flex items-center justify-between py-2 text-left"
      >
        <span className="font-medium text-slate-800">{title}</span>
        <svg
          className={`w-5 h-5 text-slate-400 transition-transform ${isExpanded ? 'rotate-180' : ''}`}
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
        </svg>
      </button>
      <AnimatePresence>
        {isExpanded && (
          <motion.div
            initial={{ height: 0, opacity: 0 }}
            animate={{ height: 'auto', opacity: 1 }}
            exit={{ height: 0, opacity: 0 }}
            transition={{ duration: 0.2 }}
            className="overflow-hidden"
          >
            <div className="pt-2">{children}</div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}
