import { useState, useCallback } from 'react';

/**
 * useFavoritesUI Hook
 * Manages favorite button UI state (separate from API logic)
 * Handles loading states, optimistic updates, and UI feedback
 */
export const useFavoritesUI = () => {
  const [isFavorited, setIsFavorited] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);
  const [showNotification, setShowNotification] = useState(false);

  /**
   * Handle favorite toggle with optimistic update
   */
  const handleToggleFavorite = useCallback(async (onToggle) => {
    setIsLoading(true);
    setError(null);
    try {
      // Optimistic update
      setIsFavorited((prev) => !prev);

      // Call the toggle function (should be passed from parent)
      await onToggle();

      // Show notification
      setShowNotification(true);
      setTimeout(() => setShowNotification(false), 2000);
    } catch (err) {
      // Revert optimistic update on error
      setIsFavorited((prev) => !prev);
      setError(err?.message || 'Failed to update favorite');
    } finally {
      setIsLoading(false);
    }
  }, []);

  /**
   * Set favorite state (used when fetching current state from API)
   */
  const setFavoriteState = useCallback((isFav) => {
    setIsFavorited(isFav);
  }, []);

  /**
   * Clear error
   */
  const clearError = useCallback(() => {
    setError(null);
  }, []);

  return {
    isFavorited,
    isLoading,
    error,
    showNotification,
    handleToggleFavorite,
    setFavoriteState,
    clearError,
  };
};
