import { useState, useCallback, useEffect, useRef } from 'react';
import * as profileApi from '../api';
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

  const areProfilesEqual = (a, b) => (
    (a?.id || '') === (b?.id || '')
    && (a?.email || '') === (b?.email || '')
    && (a?.firstname || '') === (b?.firstname || '')
    && (a?.lastname || '') === (b?.lastname || '')
    && (a?.phoneNumber || '') === (b?.phoneNumber || '')
    && (a?.role || '') === (b?.role || '')
    && (a?.profileImageUrl || '') === (b?.profileImageUrl || '')
  );

  const commitProfile = useCallback((nextProfile) => {
    if (!nextProfile) {
      return;
    }

    setProfile((current) => (areProfilesEqual(current, nextProfile) ? current : nextProfile));

    if (!areProfilesEqual(userRef.current, nextProfile)) {
      updateUser?.(nextProfile);
    }
  }, [updateUser]);

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
      commitProfile(data);
    } catch (err) {
      setError(err?.message || 'Failed to fetch profile');
      // Fall back to context user
      setProfile(userRef.current);
    } finally {
      setLoading(false);
    }
  }, [commitProfile]);

  // Initialize profile from context
  useEffect(() => {
    if (user && !profile) {
      commitProfile(user);
    }
  }, [user, profile, commitProfile]);

  // Update profile
  const updateProfile = useCallback(
    async (profileData) => {
      setLoading(true);
      setError(null);
      try {
        const updated = await profileApi.updateProfile(profileData);
        commitProfile(updated);
        return updated;
      } catch (err) {
        setError(err?.message || 'Failed to update profile');
        throw err;
      } finally {
        setLoading(false);
      }
    },
    [commitProfile]
  );

  const uploadProfileImage = useCallback(
    async (file) => {
      setLoading(true);
      setError(null);
      try {
        const updated = await profileApi.uploadProfileImage(file);
        commitProfile(updated);
        return updated;
      } catch (err) {
        setError(err?.message || 'Failed to upload profile image');
        throw err;
      } finally {
        setLoading(false);
      }
    },
    [commitProfile]
  );

  const removeProfileImage = useCallback(
    async () => {
      setLoading(true);
      setError(null);
      try {
        const updated = await profileApi.removeProfileImage();
        commitProfile(updated);
        return updated;
      } catch (err) {
        setError(err?.message || 'Failed to remove profile image');
        throw err;
      } finally {
        setLoading(false);
      }
    },
    [commitProfile]
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
