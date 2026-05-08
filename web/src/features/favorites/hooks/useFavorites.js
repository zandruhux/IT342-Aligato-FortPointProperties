import { useState, useCallback } from 'react';
import * as favoritesApi from '../api/favoritesApi';
import { useAuthContext } from '../../../shared/context/useAuthContext';

/**
 * useFavorites Hook
 * Manages favorites state and operations
 */
export const useFavorites = () => {
  const { isLoggedIn } = useAuthContext();
  const [favorites, setFavorites] = useState([]);
  const [favoriteCount, setFavoriteCount] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [favoriteIds, setFavoriteIds] = useState(new Set());

  /**
   * Fetch all user's favorite properties
   */
  const fetchFavorites = useCallback(async () => {
    if (!isLoggedIn) {
      setError('Please log in to view favorites');
      return;
    }

    setLoading(true);
    setError(null);
    try {
      const data = await favoritesApi.getAllFavorites();
      setFavorites(data || []);
      setFavoriteIds(new Set(data?.map((f) => f.propertyId ?? f.id) || []));
    } catch (err) {
      setError(err?.message || 'Failed to fetch favorites');
      setFavorites([]);
    } finally {
      setLoading(false);
    }
  }, [isLoggedIn]);

  /**
   * Fetch count of favorite properties
   */
  const fetchFavoriteCount = useCallback(async () => {
    if (!isLoggedIn) {
      return;
    }

    try {
      const count = await favoritesApi.getFavoriteCount();
      setFavoriteCount(count || 0);
    } catch (err) {
      console.error('Failed to fetch favorite count:', err);
    }
  }, [isLoggedIn]);

  /**
   * Add a property to favorites
   */
  const addFavorite = useCallback(
    async (propertyId) => {
      if (!isLoggedIn) {
        setError('Please log in to add favorites');
        return false;
      }

      setError(null);
      const previousFavoriteIds = favoriteIds;
      const previousFavoriteCount = favoriteCount;

      setFavoriteIds((prev) => new Set([...prev, propertyId]));
      setFavoriteCount((prev) => prev + 1);

      try {
        await favoritesApi.addToFavorites(propertyId);
        return true;
      } catch (err) {
        setFavoriteIds(previousFavoriteIds);
        setFavoriteCount(previousFavoriteCount);
        setError(err?.message || 'Failed to add favorite');
        return false;
      }
    },
    [isLoggedIn, favoriteIds, favoriteCount]
  );

  /**
   * Remove a property from favorites
   */
  const removeFavorite = useCallback(
    async (propertyId) => {
      if (!isLoggedIn) {
        setError('Please log in to remove favorites');
        return false;
      }

      setError(null);
      const previousFavorites = favorites;
      const previousFavoriteIds = favoriteIds;
      const previousFavoriteCount = favoriteCount;

      setFavorites((prevFavorites) => prevFavorites.filter((fav) => fav.propertyId !== propertyId));
      setFavoriteIds((prev) => {
        const newSet = new Set(prev);
        newSet.delete(propertyId);
        return newSet;
      });
      setFavoriteCount((prev) => Math.max(0, prev - 1));

      try {
        await favoritesApi.removeFromFavorites(propertyId);
        return true;
      } catch (err) {
        if (err?.message?.includes('not in favorites')) {
          return true;
        }

        setFavorites(previousFavorites);
        setFavoriteIds(previousFavoriteIds);
        setFavoriteCount(previousFavoriteCount);
        setError(err?.message || 'Failed to remove favorite');
        return false;
      }
    },
    [isLoggedIn, favorites, favoriteIds, favoriteCount]
  );

  /**
   * Toggle favorite status for a property
   */
  const toggleFavorite = useCallback(
    async (propertyId) => {
      if (!isLoggedIn) {
        setError('Please log in to manage favorites');
        return false;
      }

      const isFavorited = favoriteIds.has(propertyId);
      if (isFavorited) {
        return removeFavorite(propertyId);
      } else {
        return addFavorite(propertyId);
      }
    },
    [isLoggedIn, favoriteIds, addFavorite, removeFavorite]
  );

  /**
   * Check if a property is favorited
   */
  const isFavorited = useCallback(
    (propertyId) => {
      return favoriteIds.has(propertyId);
    },
    [favoriteIds]
  );

  return {
    favorites,
    favoriteCount,
    loading,
    error,
    favoriteIds,
    fetchFavorites,
    fetchFavoriteCount,
    addFavorite,
    removeFavorite,
    toggleFavorite,
    isFavorited,
    setFavorites,
    setError,
  };
};

export default useFavorites;
