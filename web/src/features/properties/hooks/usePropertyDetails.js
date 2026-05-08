import { useState, useCallback } from 'react';
import * as propertyApi from '../api/propertyApi';
import { useAuthContext } from '../../../shared/context/AuthContext';

/**
 * usePropertyDetails Hook
 * Manages fetching and state for a single property based on user role
 */
export const usePropertyDetails = (propertyId) => {
  const { isLoggedIn, user } = useAuthContext();
  const [property, setProperty] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // Fetch property details based on user role
  const fetchPropertyDetails = useCallback(async () => {
    if (!propertyId) {
      setError('Property ID is required');
      return;
    }

    setLoading(true);
    setError(null);
    try {
      let data;
      if (!isLoggedIn) {
        // Public user - use basic endpoint (assuming basic property is public)
        data = await propertyApi.getPublicProperties();
        data = data.find((p) => p.id === parseInt(propertyId));
      } else if (user?.role === 'ADMIN') {
        // Admin user
        data = await propertyApi.getAdminPropertyById(propertyId);
      } else if (user?.role === 'AGENT') {
        // Agent user
        data = await propertyApi.getAgentPropertyDetails(propertyId);
      } else if (user?.role === 'registered_user' || user?.role === 'USER') {
        // Registered user
        data = await propertyApi.getUserPropertyDetails(propertyId);
      } else {
        // Public user (fallback)
        data = await propertyApi.getPublicProperties();
        data = data.find((p) => p.id === parseInt(propertyId));
      }
      setProperty(data || null);
    } catch (err) {
      setError(err?.message || 'Failed to fetch property details');
      setProperty(null);
    } finally {
      setLoading(false);
    }
  }, [propertyId, isLoggedIn, user?.role]);

  return {
    property,
    loading,
    error,
    fetchPropertyDetails,
    setProperty,
    setError,
  };
};

export default usePropertyDetails;
