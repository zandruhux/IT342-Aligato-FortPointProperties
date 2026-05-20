import { useState, useCallback } from 'react';
import * as propertyApi from '../api/propertyApi';
import { useAuthContext } from '../../../shared/context/useAuthContext';

/**
 * useProperties Hook
 * Manages fetching and state for properties based on user role
 */
export const useProperties = () => {
  const { authReady, isLoggedIn, user } = useAuthContext();
  const [properties, setProperties] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // Fetch properties based on user role
  const fetchProperties = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      if (!authReady) {
        return;
      }

      const data = await propertyApi.getProperties(isLoggedIn ? user?.role : 'PUBLIC');
      setProperties(data || []);
    } catch (err) {
      setError(err?.message || 'Failed to fetch properties');
      setProperties([]);
    } finally {
      setLoading(false);
    }
  }, [authReady, isLoggedIn, user?.role]);

  // Fetch featured properties (public)
  const fetchFeaturedProperties = useCallback(async (limit = 4) => {
    setLoading(true);
    setError(null);
    try {
      const data = await propertyApi.getPublicFeaturedProperties(limit);
      setProperties(data || []);
    } catch (err) {
      setError(err?.message || 'Failed to fetch featured properties');
      setProperties([]);
    } finally {
      setLoading(false);
    }
  }, []);

  return {
    properties,
    loading,
    error,
    fetchProperties,
    fetchFeaturedProperties,
    setProperties,
    setError,
  };
};

export default useProperties;
