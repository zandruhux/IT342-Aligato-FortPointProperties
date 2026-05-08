import { useState, useCallback } from 'react';
import * as propertyApi from '../api/propertyApi';
import { useAuthContext } from '../../../shared/context/useAuthContext';

/**
 * useProperties Hook
 * Manages fetching and state for properties based on user role
 */
export const useProperties = () => {
  const { isLoggedIn, user } = useAuthContext();
  const [properties, setProperties] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // Fetch properties based on user role
  const fetchProperties = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      let data;
      if (!isLoggedIn) {
        // Public user
        data = await propertyApi.getPublicProperties();
        console.log('Fetched public properties:', data);
      } else if (user?.role === 'ADMIN') {
        // Admin user
        data = await propertyApi.getAdminAllProperties();
        console.log('Fetched admin properties:', data);
      } else if (user?.role === 'AGENT') {
        // Agent user
        data = await propertyApi.getAgentAllProperties();
      } else if (user?.role === 'registered_user' || user?.role === 'USER') {
        // Registered user
        data = await propertyApi.getUserProperties();
      } else {
        // Public user (fallback) 
        data = await propertyApi.getPublicProperties();
      }
      setProperties(data || []);
    } catch (err) {
      setError(err?.message || 'Failed to fetch properties');
      setProperties([]);
    } finally {
      setLoading(false);
    }
  }, [isLoggedIn, user?.role]);

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
