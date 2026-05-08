import React, { useState, useCallback } from 'react';
import { FiSearch, FiX } from 'react-icons/fi';

/**
 * PropertySearchFilter Component
 * Pure UI component for search and price filtering (role-agnostic)
 * 
 * This component is UI-only with NO role-based logic.
 * All role-based business logic is handled by the calling hook/page.
 * 
 * Props:
 * - searchTypes: Array of { value, label } search type options (provided by parent)
 * - onSearch: Callback(searchTerm, searchType) when user searches
 * - onPriceRangeChange: Callback(minPrice, maxPrice) when price range updates
 * - onClearFilters: Callback() when clear button is clicked
 * - isLoading: Boolean indicating if search is in progress
 */
export default function PropertySearchFilter({
  searchTypes = [
    { value: 'name', label: 'Property Name' },
    { value: 'location', label: 'Location' },
    { value: 'developer', label: 'Developer' },
  ],
  onSearch,
  onPriceRangeChange,
  onClearFilters,
  isLoading = false,
  showPriceRange = true,
  title = 'Search Properties',
}) {
  // Local form state - UI only
  const [searchTerm, setSearchTerm] = useState('');
  const [searchType, setSearchType] = useState(searchTypes[0]?.value || 'name');
  const [minPrice, setMinPrice] = useState('');
  const [maxPrice, setMaxPrice] = useState('');

  /**
   * Handle search submission
   */
  const handleSearch = useCallback(() => {
    if (searchTerm.trim() && onSearch) {
      onSearch(searchTerm, searchType);
    }
  }, [searchTerm, searchType, onSearch]);

  /**
   * Handle Enter key in search input
   */
  const handleKeyPress = (e) => {
    if (e.key === 'Enter') {
      handleSearch();
    }
  };

  /**
   * Handle price range change
   */
  const handlePriceRangeChange = useCallback(() => {
    if (onPriceRangeChange) {
      const min = minPrice ? parseInt(minPrice) : null;
      const max = maxPrice ? parseInt(maxPrice) : null;
      onPriceRangeChange(min, max);
    }
  }, [minPrice, maxPrice, onPriceRangeChange]);

  /**
   * Handle clearing all filters
   */
  const handleClear = useCallback(() => {
    setSearchTerm('');
    setSearchType(searchTypes[0]?.value || 'name');
    setMinPrice('');
    setMaxPrice('');
    if (onClearFilters) {
      onClearFilters();
    }
  }, [onClearFilters, searchTypes]);

  const currentSearchLabel = searchTypes.find(
    (opt) => opt.value === searchType
  )?.label.toLowerCase();

  const hasActiveFilters = !!(searchTerm || minPrice || maxPrice);

  return (
    <div className="bg-white rounded-lg shadow-md p-6 space-y-6">
      {/* Search Section */}
      <div className="space-y-4">
        {title && <h3 className="text-lg font-semibold text-slate-900">{title}</h3>}

        {/* Search Controls */}
        <div className="flex gap-3 flex-wrap">
          {/* Search Type Dropdown */}
          <select
            value={searchType}
            onChange={(e) => setSearchType(e.target.value)}
            className="px-4 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white font-medium text-slate-700"
          >
            {searchTypes.map((opt) => (
              <option key={opt.value} value={opt.value}>
                {opt.label}
              </option>
            ))}
          </select>

          {/* Search Input */}
          <div className="flex-1 min-w-48 flex items-center gap-2 px-4 py-2 border border-slate-300 rounded-lg focus-within:ring-2 focus-within:ring-blue-500 focus-within:border-blue-500">
            <FiSearch className="text-slate-500" size={20} />
            <input
              type="text"
              placeholder={`Search by ${currentSearchLabel}...`}
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              onKeyPress={handleKeyPress}
              className="flex-1 bg-transparent outline-none font-medium text-slate-700 placeholder-slate-400"
              disabled={isLoading}
            />
            {searchTerm && (
              <button
                onClick={() => setSearchTerm('')}
                className="text-slate-400 hover:text-slate-600 transition"
                aria-label="Clear search term"
              >
                <FiX size={18} />
              </button>
            )}
          </div>

          {/* Search Button */}
          <button
            onClick={handleSearch}
            disabled={isLoading || !searchTerm.trim()}
            className="px-6 py-2 bg-blue-600 hover:bg-blue-700 disabled:bg-slate-300 text-white rounded-lg font-semibold transition-colors"
          >
            {isLoading ? 'Searching...' : 'Search'}
          </button>
        </div>
      </div>

      {/* Price Range Filter Section */}
      {showPriceRange && <div className="space-y-4">
        <h3 className="text-lg font-semibold text-slate-900">Price Range</h3>

        <div className="grid grid-cols-2 gap-4">
          {/* Min Price Input */}
          <div>
            <label className="block text-sm font-medium text-slate-700 mb-2">
              Min Price (₱)
            </label>
            <input
              type="number"
              placeholder="e.g., 1000000"
              value={minPrice}
              onChange={(e) => setMinPrice(e.target.value)}
              onBlur={handlePriceRangeChange}
              className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-slate-700"
              disabled={isLoading}
            />
          </div>

          {/* Max Price Input */}
          <div>
            <label className="block text-sm font-medium text-slate-700 mb-2">
              Max Price (₱)
            </label>
            <input
              type="number"
              placeholder="e.g., 5000000"
              value={maxPrice}
              onChange={(e) => setMaxPrice(e.target.value)}
              onBlur={handlePriceRangeChange}
              className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-slate-700"
              disabled={isLoading}
            />
          </div>
        </div>

        {/* Price Info Display */}
        {(minPrice || maxPrice) && (
          <p className="text-sm text-slate-600">
            Filter: ₱{minPrice || '0'} - ₱{maxPrice || 'No limit'}
          </p>
        )}
      </div>}

      {/* Clear Filters Button */}
      {hasActiveFilters && (
        <div>
          <button
            onClick={handleClear}
            disabled={isLoading}
            className="w-full px-4 py-2 bg-slate-100 hover:bg-slate-200 disabled:bg-slate-50 text-slate-700 rounded-lg font-semibold transition-colors border border-slate-300"
          >
            Clear All Filters
          </button>
        </div>
      )}
    </div>
  );
}
