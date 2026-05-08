import React from 'react';

/**
 * PropertyCardBase Component
 * Shared UI logic and styling for all property card variants
 * 
 * Props:
 * - property: Property object with name, location, price details
 * - onClick: Callback when card is clicked
 * - headerRightContent: JSX to render on the right of price header
 * - footerContent: JSX to render in the footer section
 * - showFavoriteButton: Boolean to show/hide favorite button
 * - onFavoriteToggle: Callback for favorite button click
 * - isFavorited: Boolean indicating if property is favorited
 * - isLoadingFavorite: Boolean indicating loading state
 */
const PropertyCardBase = ({
  property,
  onClick,
  headerRightContent = null,
  footerContent = null,
  showFavoriteButton = false,
  onFavoriteToggle = null,
  isFavorited = false,
  isLoadingFavorite = false,
}) => {
  const { name, basicDescription, location, priceRangeMin, priceRangeMax, id } = property;

  // Format price to PHP currency
  const formatPrice = (price) => {
    return new Intl.NumberFormat('en-PH', {
      style: 'currency',
      currency: 'PHP',
      minimumFractionDigits: 0,
    }).format(price);
  };

  const displayPrice =
    priceRangeMin === priceRangeMax
      ? formatPrice(priceRangeMin)
      : `${formatPrice(priceRangeMin)} - ${formatPrice(priceRangeMax)}`;

  const handleCardClick = () => {
    if (onClick) {
      onClick(id);
    }
  };

  const handleFavoriteClick = (e) => {
    e.stopPropagation();
    if (onFavoriteToggle) {
      onFavoriteToggle();
    }
  };

  return (
    <div
      className="border border-gray-100 rounded-xl overflow-hidden shadow-sm hover:shadow-lg transition-all duration-300 bg-white flex flex-col cursor-pointer transform hover:-translate-y-1"
      onClick={handleCardClick}
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
        {showFavoriteButton && (
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
        {/* Price and Header Right Content */}
        <div className="mb-2 flex items-center justify-between">
          <h3 className="text-xl font-bold text-gray-900">{displayPrice}</h3>
          {headerRightContent}
        </div>

        {/* Property Name */}
        <h4 className="text-[17px] font-semibold text-gray-800 mb-1 line-clamp-1">
          {name}
        </h4>

        {/* Location */}
        <p className="text-sm text-gray-500 mb-4 flex items-center">
          <svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth="2"
              d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"
            ></path>
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth="2"
              d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"
            ></path>
          </svg>
          {location}
        </p>

        {/* Footer Content */}
        <div className="mt-auto pt-4 border-t border-gray-100">
          {footerContent || (
            <p className="text-sm text-gray-500 line-clamp-2">{basicDescription}</p>
          )}
        </div>
      </div>
    </div>
  );
};

export default PropertyCardBase;
