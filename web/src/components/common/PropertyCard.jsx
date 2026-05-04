import React, { useState, useEffect } from 'react';
import { property as propertyFavorite } from '../../api/property';

const PropertyCard = ({ property, onClick, isFavoritedInitially = false, onFavoriteChange }) => {
  const { name, basicDescription, location, priceRangeMin, priceRangeMax, id } = property;
  const [isFavorited, setIsFavorited] = useState(isFavoritedInitially);
  const [isLoadingFavorite, setIsLoadingFavorite] = useState(false);
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    setIsAuthenticated(!!token);
  }, []);

  // Check favorite status when component mounts or ID changes
  useEffect(() => {
    if (isAuthenticated && id) {
      checkFavoriteStatus();
    }
  }, [id, isAuthenticated]);

  const checkFavoriteStatus = async () => {
    try {
      const isFav = await propertyFavorite.checkIfFavorited(id);
      setIsFavorited(isFav);
    } catch (error) {
      console.error('Error checking favorite status:', error);
    }
  };

  const handleFavoriteClick = async (e) => {
    e.stopPropagation();

    if (!isAuthenticated) {
      alert('Please log in to add favorites');
      return;
    }

    setIsLoadingFavorite(true);
    try {
      if (isFavorited) {
        await propertyFavorite.removeFromFavorites(id);
        setIsFavorited(false);
        // Notify parent if callback exists
        if (onFavoriteChange) {
          onFavoriteChange(id, false);
        }
      } else {
        await propertyFavorite.addToFavorites(id);
        setIsFavorited(true);
        // Notify parent if callback exists
        if (onFavoriteChange) {
          onFavoriteChange(id, true);
        }
      }
    } catch (error) {
      // Handle 409 Conflict (already favorited) gracefully
      if (error.response?.status === 409) {
        setIsFavorited(true);
        return;
      }
      console.error('Error updating favorite:', error);
      alert('Failed to update favorite');
    } finally {
      setIsLoadingFavorite(false);
    }
  };

  // Format price to PHP currency
  const formatPrice = (price) => {
    return new Intl.NumberFormat('en-PH', {
      style: 'currency',
      currency: 'PHP',
      minimumFractionDigits: 0,
    }).format(price);
  };

  const displayPrice = priceRangeMin === priceRangeMax 
    ? formatPrice(priceRangeMin) 
    : `${formatPrice(priceRangeMin)} - ${formatPrice(priceRangeMax)}`;

  return (
    <div // Pass the ID up when clicked
      className="border border-gray-100 rounded-xl overflow-hidden shadow-sm hover:shadow-lg transition-all duration-300 bg-white flex flex-col cursor-pointer transform hover:-translate-y-1" 
      onClick={() => onClick(id)}
      >
        {/* Property Image - Placeholder */}
        <div className="h-56 bg-gradient-to-br from-gray-100 to-gray-200 relative overflow-hidden flex items-center justify-center">
          <div className="text-center">
            <p className="text-gray-500 font-medium text-sm">Photo Coming Soon</p>
          </div>

          <span className="absolute top-3 left-3 bg-blue-600 text-white text-xs font-semibold px-3 py-1 rounded-full">
            Featured
            </span>
            
            {/* Heart Favorite Button */}
            {isAuthenticated && (
              <button
                onClick={handleFavoriteClick}
                disabled={isLoadingFavorite}
                className="absolute top-3 right-3 bg-white rounded-full p-2 shadow-md hover:bg-gray-50 transition disabled:opacity-50"
              >
                {isFavorited ? (
                  <svg className="w-6 h-6" fill="#FF0000" viewBox="0 0 24 24">
                    <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
                  </svg>
                ) : (
                  <svg className="w-6 h-6" fill="none" stroke="#FF0000" strokeWidth="2" viewBox="0 0 24 24">
                    <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
                  </svg>
                )}
              </button>
            )}
            
            <div className="flex items-center justify-center w-full h-full text-gray-500 font-medium">
            [Image Placeholder]
            </div>
        </div>

        {/* Card Content */}
        <div className="p-5 flex-1 flex flex-col">
            <div className="mb-2 flex items-center justify-between">
              <h3 className="text-xl font-bold text-gray-900">{displayPrice}</h3>
              {property.units && property.units.length > 0 && (
                <span className="inline-block bg-red-100 text-red-800 text-xs font-semibold px-2 py-1 rounded-full">
                  {property.units.length}u
                </span>
              )}
            </div>
            
            <h4 className="text-[17px] font-semibold text-gray-800 mb-1 line-clamp-1">
            {name}
            </h4>
            
            <p className="text-sm text-gray-500 mb-4 flex items-center">
            {/* Location Icon SVG */}
            <svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"></path>
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"></path>
            </svg>
            {location}
            </p>

            {/* Description replaces the bed/bath stats from the image */}
            <div className="mt-auto pt-4 border-t border-gray-100">
            <p className="text-sm text-gray-500 line-clamp-2">
                {basicDescription}
            </p>
            </div>
        </div>
        </div>
  );
};

export default PropertyCard;