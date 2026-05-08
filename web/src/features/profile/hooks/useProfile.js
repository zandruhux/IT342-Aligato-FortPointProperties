import { useState, useCallback, useEffect } from 'react';
import * as profileApi from '../api/profileApi';
import { useAuthContext } from '../../../shared/context/AuthContext';

/**
 * useProfile Hook
 * Manages user profile state and operations
 */
export const useProfile = () => {
  const { user } = useAuthContext();
  const [profile, setProfile] = useState(user || null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // Fetch profile from backend
  const fetchProfile = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await profileApi.getProfile();
      setProfile(data);
    } catch (err) {
      setError(err?.message || 'Failed to fetch profile');
      // Fall back to context user
      setProfile(user);
    } finally {
      setLoading(false);
    }
  }, [user]);

  // Initialize profile from context
  useEffect(() => {
    if (user && !profile) {
      setProfile(user);
    }
  }, [user, profile]);

  // Update profile
  const updateProfile = useCallback(
    async (profileData) => {
      setLoading(true);
      setError(null);
      try {
        const updated = await profileApi.updateProfile(profileData);
        setProfile(updated);
        return updated;
      } catch (err) {
        setError(err?.message || 'Failed to update profile');
        throw err;
      } finally {
        setLoading(false);
      }
    },
    []
  );

  return {
    profile,
    loading,
    error,
    fetchProfile,
    updateProfile,
    setProfile,
    setError,
  };
};

export default useProfile;
