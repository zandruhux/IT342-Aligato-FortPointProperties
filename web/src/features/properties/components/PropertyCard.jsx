import React, { memo } from 'react';
import PropertyCardBase from './PropertyCardBase';

/**
 * Presentational PropertyCard component.
 * All business logic is owned by the parent/container.
 */
function PropertyCard({
  property,
  onClick,
  onView,
  onEdit,
  onDelete,
  showFavoriteButton = false,
  isFavorited = false,
  isLoadingFavorite = false,
  onFavoriteToggle = null,
}) {
  const handleCardClick = () => {
    if (onClick) return onClick(property.id);
    if (onView) return onView(property.id);
  };

  const headerRightContent = property.units && property.units.length > 0
    ? (<span className="inline-block bg-blue-100 text-blue-800 text-xs font-semibold px-2 py-1 rounded-full">{property.units.length}u</span>)
    : (property.listingType ? (<span className="inline-block bg-green-100 text-green-800 text-xs font-semibold px-2 py-1 rounded-full">{property.listingType}</span>) : null);

  let footerContent = null;
  if (onEdit || onDelete) {
    footerContent = (
      <div className="flex gap-2">
        {onEdit && (
          <button onClick={(e) => { e.stopPropagation(); onEdit(property.id); }} className="flex-1 bg-blue-500 hover:bg-blue-600 text-white text-sm font-semibold py-2 px-3 rounded transition">Edit</button>
        )}
        {onDelete && (
          <button onClick={(e) => { e.stopPropagation(); onDelete(property.id); }} className="flex-1 bg-red-500 hover:bg-red-600 text-white text-sm font-semibold py-2 px-3 rounded transition">Delete</button>
        )}
      </div>
    );
  } else if (onView && !onEdit && !onDelete) {
    footerContent = (
      <button onClick={(e) => { e.stopPropagation(); onView(property.id); }} className="w-full bg-green-500 hover:bg-green-600 text-white text-sm font-semibold py-2 px-3 rounded transition">View Details</button>
    );
  }

  return (
    <PropertyCardBase
      property={property}
      onClick={handleCardClick}
      headerRightContent={headerRightContent}
      footerContent={footerContent}
      showFavoriteButton={showFavoriteButton}
      onFavoriteToggle={onFavoriteToggle}
      isFavorited={isFavorited}
      isLoadingFavorite={isLoadingFavorite}
    />
  );
}

export default memo(PropertyCard);
