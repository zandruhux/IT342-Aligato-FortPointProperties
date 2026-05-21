import { useState, useCallback, useEffect, useRef } from 'react';
import * as profileApi from '../api/profileApi';
import { useAuthContext } from '../../../shared/context/useAuthContext';

/**
 * useProfile Hook
 * Manages user profile state and operations
 */
export const useProfile = () => {
  const { user, updateUser } = useAuthContext();
  const [profile, setProfile] = useState(user || null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const userRef = useRef(user);

  useEffect(() => {
    // Avoid making fetchProfile depend on the whole user object and refetching in a loop.
    userRef.current = user;
  }, [user]);

  // Fetch profile from backend
  const fetchProfile = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await profileApi.getProfile();
      setProfile(data);
      updateUser?.(data);
    } catch (err) {
      setError(err?.message || 'Failed to fetch profile');
      // Fall back to context user
      setProfile(userRef.current);
    } finally {
      setLoading(false);
    }
  }, [updateUser]);

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
        updateUser?.(updated);
        return updated;
      } catch (err) {
        setError(err?.message || 'Failed to update profile');
        throw err;
      } finally {
        setLoading(false);
      }
    },
    [updateUser]
  );

  const uploadProfileImage = useCallback(
    async (file) => {
      setLoading(true);
      setError(null);
      try {
        const updated = await profileApi.uploadProfileImage(file);
        setProfile(updated);
        updateUser?.(updated);
        return updated;
      } catch (err) {
        setError(err?.message || 'Failed to upload profile image');
        throw err;
      } finally {
        setLoading(false);
      }
    },
    [updateUser]
  );

  const removeProfileImage = useCallback(
    async () => {
      setLoading(true);
      setError(null);
      try {
        const updated = await profileApi.removeProfileImage();
        setProfile(updated);
        updateUser?.(updated);
        return updated;
      } catch (err) {
        setError(err?.message || 'Failed to remove profile image');
        throw err;
      } finally {
        setLoading(false);
      }
    },
    [updateUser]
  );

  return {
    profile,
    loading,
    error,
    fetchProfile,
    updateProfile,
    uploadProfileImage,
    removeProfileImage,
    setProfile,
    setError,
  };
};

export default useProfile;
