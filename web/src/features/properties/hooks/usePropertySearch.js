import { useState, useCallback } from 'react';
import * as propertyApi from '../api/propertyApi';
import { useAuthContext } from '../../../shared/context/AuthContext';
import {
  applySearchFilters,
  getDefaultFilters,
  hasActiveFilters,
} from '../../../shared/utils/searchHelpers';

/**
 * usePropertySearch Hook
 * Consolidated search and filtering orchestration for all roles
 * 
 * This hook:
 * - Manages search/filter state and results
 * - Delegates role-based API calls to propertyApi
 * - Consolidates duplicate search logic into single generic flow
 */
export const usePropertySearch = () => {
  const { isLoggedIn, user } = useAuthContext();
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [filters, setFilters] = useState(getDefaultFilters());
  const [hasSearched, setHasSearched] = useState(false);

  /**
   * Generic search method
   * Handles all search scenarios (by name, location, developer, price)
   * Role-based API routing is handled inside propertyApi.js
   */
  const performSearch = useCallback(
    async (searchParams = {}) => {
      setLoading(true);
      setError(null);
      setHasSearched(true);

      try {
        // Call generic propertyApi.searchProperties which handles role routing internally
        const data = await propertyApi.searchProperties(user?.role, searchParams);
        setResults(data || []);
        return data || [];
      } catch (err) {
        const errorMessage = err?.message || 'Search failed';
        setError(errorMessage);
        setResults([]);
        console.error('[usePropertySearch] Search error:', err);
        return [];
      } finally {
        setLoading(false);
      }
    },
    [user?.role]
  );

  /**
   * Get all properties based on user role
   * No filters applied - backend returns full dataset
   */
  const getAllProperties = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      // Call generic propertyApi.getProperties which handles role routing internally
      const data = await propertyApi.getProperties(user?.role);
      setResults(data || []);
      return data || [];
    } catch (err) {
      const errorMessage = err?.message || 'Failed to fetch properties';
      setError(errorMessage);
      setResults([]);
      console.error('[usePropertySearch] Fetch error:', err);
      return [];
    } finally {
      setLoading(false);
    }
  }, [user?.role]);

  /**
   * Execute search with current filters
   * If filters are active, performs server-side search
   * Otherwise fetches all and applies client-side filters
   */
  const search = useCallback(async () => {
    if (hasActiveFilters(filters)) {
      // Server-side search with active filters
      const searchParams = {
        name: filters.name || undefined,
        location: filters.location || undefined,
        developer: filters.developer || undefined,
        minPrice: filters.minPrice ?? undefined,
        maxPrice: filters.maxPrice ?? undefined,
      };
      return performSearch(searchParams);
    }

    // No active filters: fetch all and apply client-side
    const allProps = await getAllProperties();
    const filtered = applySearchFilters(allProps, filters);
    setResults(filtered);
    setHasSearched(true);
    return filtered;
  }, [filters, performSearch, getAllProperties]);

  /**
   * Update filter state
   */
  const updateFilters = useCallback((newFilters) => {
    setFilters((prev) => ({
      ...prev,
      ...newFilters,
    }));
  }, []);

  /**
   * Generic search by field
   * Consolidates searchByName, searchByLocation, searchByDeveloper into one method
   */
  const searchByField = useCallback(
    async (fieldName, fieldValue) => {
      const params = { [fieldName]: fieldValue };
      updateFilters(params);
      return performSearch(params);
    },
    [updateFilters, performSearch]
  );

  /**
   * Backward-compatible search methods
   */
  const searchByName = useCallback(
    async (name) => {
      return searchByField('name', name);
    },
    [searchByField]
  );

  const searchByLocation = useCallback(
    async (location) => {
      return searchByField('location', location);
    },
    [searchByField]
  );

  const searchByDeveloper = useCallback(
    async (developer) => {
      return searchByField('developer', developer);
    },
    [searchByField]
  );

  /**
   * Set price range and search
   */
  const setPriceRange = useCallback(
    (minPrice, maxPrice) => {
      updateFilters({ minPrice, maxPrice });
      return performSearch({ minPrice, maxPrice });
    },
    [updateFilters, performSearch]
  );

  /**
   * Clear all filters and reset state
   */
  const clearFilters = useCallback(() => {
    setFilters(getDefaultFilters());
    setHasSearched(false);
    setResults([]);
    setError(null);
  }, []);

  return {
    // State
    results,
    loading,
    error,
    filters,
    hasSearched,
    isFiltered: hasActiveFilters(filters),
    
    // Generic search methods
    performSearch,
    search,
    searchByField,
    getAllProperties,
    
    // Backward-compatible methods
    searchByName,
    searchByLocation,
    searchByDeveloper,
    
    // Filter management
    updateFilters,
    setPriceRange,
    clearFilters,
  };
};
