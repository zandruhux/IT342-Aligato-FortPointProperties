import { useState, useCallback } from 'react';
import * as propertyApi from '../api';
import { useAuthContext } from '../../../shared/context/useAuthContext';
import {
  getDefaultFilters,
  hasActiveFilters,
  sortPropertiesByPrice,
} from '../../../shared/utils/searchHelpers';

export const usePropertySearch = () => {
  const { authReady, user } = useAuthContext();
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [filters, setFilters] = useState(getDefaultFilters());
  const [hasSearched, setHasSearched] = useState(false);

  const performSearch = useCallback(
    async (searchParams = {}) => {
      setLoading(true);
      setError(null);
      setHasSearched(true);

      try {
        if (!authReady) {
          return [];
        }

        const searchFilters = {
          ...getDefaultFilters(),
          ...searchParams,
          minPrice: searchParams.minPrice ?? null,
          maxPrice: searchParams.maxPrice ?? null,
        };
        const data = await propertyApi.searchProperties(user?.role, searchFilters);
        const results = sortPropertiesByPrice(data || [], searchFilters.sortMode);
        setResults(results);
        return results;
      } catch (err) {
        const errorMessage = err?.message || 'Search failed';
        setError(errorMessage);
        setResults([]);
        return [];
      } finally {
        setLoading(false);
      }
    },
    [authReady, user?.role]
  );

  const getAllProperties = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      if (!authReady) {
        return [];
      }

      const data = await propertyApi.getProperties(user?.role);
      setResults(data || []);
      return data || [];
    } catch (err) {
      const errorMessage = err?.message || 'Failed to fetch properties';
      setError(errorMessage);
      setResults([]);
      return [];
    } finally {
      setLoading(false);
    }
  }, [authReady, user?.role]);

  const search = useCallback(async (overrideFilters = null) => {
    const activeFilters = overrideFilters || getDefaultFilters();

    if (!hasActiveFilters(activeFilters)) {
      return getAllProperties();
    }

    return performSearch(activeFilters);
  }, [getAllProperties, performSearch]);

  const updateFilters = useCallback((newFilters) => {
    setFilters((prev) => ({
      ...prev,
      ...newFilters,
    }));
  }, []);

  const clearFilters = useCallback(() => {
    setFilters(getDefaultFilters());
    setHasSearched(false);
    setResults([]);
    setError(null);
  }, []);

  return {
    results,
    loading,
    error,
    filters,
    hasSearched,
    isFiltered: hasActiveFilters(filters),
    
    performSearch,
    search,
    getAllProperties,
    
    updateFilters,
    clearFilters,
  };
};
