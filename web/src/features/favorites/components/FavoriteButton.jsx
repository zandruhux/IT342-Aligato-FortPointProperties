import React, { useState, useEffect } from 'react';
import { useFavorites } from '../hooks/useFavorites';
import { useAuthContext } from '../../../shared/context/AuthContext';

/**
 * FavoriteButton Component
 * Reusable heart button for adding/removing properties from favorites
 *
 * Props:
 * - propertyId: ID of the property to favorite
 * - size: Button size ('sm', 'md', 'lg') - default 'md'
 * - variant: Button style ('icon', 'button') - default 'icon'
 * - onToggle: Optional callback when favorite status changes (isFavorited: boolean)
 * - isFavoritedInitially: Initial favorite state for sync
 */
const FavoriteButton = ({
  propertyId,
  size = 'md',
  variant = 'icon',
  onToggle = null,
  isFavoritedInitially = false,
}) => {
  const { isLoggedIn } = useAuthContext();
  const { toggleFavorite, isFavorited, loading } = useFavorites();
  const [isChecking, setIsChecking] = useState(false);

  // Sync initial state
  useEffect(() => {
    if (isFavoritedInitially && propertyId) {
      // Initial state is already tracked in hook
    }
  }, [propertyId, isFavoritedInitially]);

  const handleClick = async (e) => {
    e.stopPropagation();

    if (!isLoggedIn) {
      alert('Please log in to add favorites');
      return;
    }

    setIsChecking(true);
    const success = await toggleFavorite(propertyId);
    if (success && onToggle) {
      onToggle(!isFavorited(propertyId));
    }
    setIsChecking(false);
  };

  const isCurrentlyFavorited = isFavorited(propertyId);
  const isLoading = loading || isChecking;

  // Size classes
  const sizeClasses = {
    sm: 'w-5 h-5',
    md: 'w-6 h-6',
    lg: 'w-8 h-8',
  };

  const buttonSizeClasses = {
    sm: 'p-1',
    md: 'p-2',
    lg: 'p-3',
  };

  // Icon variant (just the heart button)
  if (variant === 'icon') {
    return (
      <button
        onClick={handleClick}
        disabled={isLoading}
        className={`${buttonSizeClasses[size]} bg-white rounded-full shadow-md hover:bg-gray-50 transition disabled:opacity-50 cursor-pointer`}
        title={isCurrentlyFavorited ? 'Remove from favorites' : 'Add to favorites'}
      >
        {isCurrentlyFavorited ? (
          <svg className={`${sizeClasses[size]}`} fill="#FF0000" viewBox="0 0 24 24">
            <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
          </svg>
        ) : (
          <svg
            className={`${sizeClasses[size]}`}
            fill="none"
            stroke="#FF0000"
            strokeWidth="2"
            viewBox="0 0 24 24"
          >
            <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
          </svg>
        )}
      </button>
    );
  }

  // Button variant (with text)
  return (
    <button
      onClick={handleClick}
      disabled={isLoading}
      className={`flex items-center gap-2 px-4 py-2 rounded-lg transition disabled:opacity-50 ${
        isCurrentlyFavorited
          ? 'bg-red-100 text-red-700 hover:bg-red-200'
          : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
      }`}
      title={isCurrentlyFavorited ? 'Remove from favorites' : 'Add to favorites'}
    >
      {isCurrentlyFavorited ? (
        <svg className={`${sizeClasses[size]}`} fill="currentColor" viewBox="0 0 24 24">
          <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
        </svg>
      ) : (
        <svg
          className={`${sizeClasses[size]}`}
          fill="none"
          stroke="currentColor"
          strokeWidth="2"
          viewBox="0 0 24 24"
        >
          <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
        </svg>
      )}
      <span>{isCurrentlyFavorited ? 'Favorited' : 'Add to Favorites'}</span>
    </button>
  );
};

export default FavoriteButton;
