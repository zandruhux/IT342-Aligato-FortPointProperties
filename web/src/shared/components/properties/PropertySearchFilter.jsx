import React, { useCallback, useMemo, useState } from 'react';
import { FiArrowDown, FiArrowUp, FiSearch, FiX } from 'react-icons/fi';

const DEFAULT_SEARCH_TYPES = [
  { value: 'name', label: 'Project Name' },
  { value: 'location', label: 'Location' },
  { value: 'developer', label: 'Developer' },
];

export default function PropertySearchFilter({
  searchTypes = DEFAULT_SEARCH_TYPES,
  onSearch,
  onSortChange,
  onClearFilters,
  isLoading = false,
  showSort = true,
  sortMode = '',
  title = 'Search Properties',
}) {
  const [searchTerm, setSearchTerm] = useState('');
  const [searchType, setSearchType] = useState(searchTypes[0]?.value || 'name');

  const currentSearchLabel = useMemo(
    () => searchTypes.find((option) => option.value === searchType)?.label.toLowerCase() || 'project name',
    [searchType, searchTypes]
  );

  const handleSearch = useCallback(() => {
    if (onSearch) {
      onSearch(searchTerm.trim(), searchType);
    }
  }, [onSearch, searchTerm, searchType]);

  const handleSortToggle = useCallback(() => {
    if (!onSortChange) return;

    const nextSortMode = sortMode === 'minPrice' ? 'maxPrice' : 'minPrice';
    onSortChange(nextSortMode);
  }, [onSortChange, sortMode]);

  const handleClear = useCallback(() => {
    setSearchTerm('');
    setSearchType(searchTypes[0]?.value || 'name');
    if (onClearFilters) {
      onClearFilters();
    }
  }, [onClearFilters, searchTypes]);

  const hasActiveControls = !!searchTerm || !!sortMode;
  const sortLabel = sortMode === 'minPrice' ? 'Highest Price' : 'Minimum Price';
  const SortIcon = sortMode === 'minPrice' ? FiArrowDown : FiArrowUp;

  return (
    <div className="bg-white rounded-lg shadow-md p-6 space-y-4">
      {title && <h3 className="text-lg font-semibold text-slate-900">{title}</h3>}

      <div className="flex gap-3 flex-wrap">
        <select
          value={searchType}
          onChange={(event) => setSearchType(event.target.value)}
          className="px-4 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white font-medium text-slate-700"
          disabled={isLoading}
        >
          {searchTypes.map((option) => (
            <option key={option.value} value={option.value}>
              {option.label}
            </option>
          ))}
        </select>

        <div className="flex-1 min-w-48 flex items-center gap-2 px-4 py-2 border border-slate-300 rounded-lg focus-within:ring-2 focus-within:ring-blue-500 focus-within:border-blue-500">
          <FiSearch className="text-slate-500" size={20} />
          <input
            type="text"
            placeholder={`Search by ${currentSearchLabel}...`}
            value={searchTerm}
            onChange={(event) => setSearchTerm(event.target.value)}
            onKeyDown={(event) => {
              if (event.key === 'Enter') {
                handleSearch();
              }
            }}
            className="flex-1 bg-transparent outline-none font-medium text-slate-700 placeholder-slate-400 min-w-0"
            disabled={isLoading}
          />
          {searchTerm && (
            <button
              type="button"
              onClick={() => setSearchTerm('')}
              className="text-slate-400 hover:text-slate-600 transition"
              aria-label="Clear search term"
              disabled={isLoading}
            >
              <FiX size={18} />
            </button>
          )}
        </div>

        <button
          type="button"
          onClick={handleSearch}
          disabled={isLoading}
          className="px-6 py-2 bg-blue-600 hover:bg-blue-700 disabled:bg-slate-300 text-white rounded-lg font-semibold transition-colors"
        >
          {isLoading ? 'Searching...' : 'Search'}
        </button>

        {showSort && onSortChange && (
          <button
            type="button"
            onClick={handleSortToggle}
            disabled={isLoading}
            className="inline-flex items-center gap-2 px-4 py-2 bg-slate-100 hover:bg-slate-200 disabled:bg-slate-50 text-slate-800 rounded-lg font-semibold transition-colors border border-slate-300"
            aria-label={`Sort by ${sortLabel}`}
          >
            <SortIcon size={18} />
          </button>
        )}
      </div>

      {hasActiveControls && (
        <button
          type="button"
          onClick={handleClear}
          disabled={isLoading}
          className="w-full px-4 py-2 bg-slate-100 hover:bg-slate-200 disabled:bg-slate-50 text-slate-700 rounded-lg font-semibold transition-colors border border-slate-300"
        >
          Clear All Filters
        </button>
      )}
    </div>
  );
}
