import React from 'react';


const PropertyCard = ({ property, onClick }) => {
  const { propertyName, description, location, priceRangeMin, priceRangeMax } = property;

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
      onClick={() => onClick(property.id)}
      >
        {/* Image Placeholder */}
        <div className="h-56 bg-gray-200 relative">
            <span className="absolute top-3 left-3 bg-blue-600 text-white text-xs font-semibold px-3 py-1 rounded-full">
            Featured
            </span>
            <div className="flex items-center justify-center w-full h-full text-gray-500 font-medium">
            [Image Placeholder]
            </div>
        </div>

        {/* Card Content */}
        <div className="p-5 flex-1 flex flex-col">
            <div className="mb-2">
            <h3 className="text-xl font-bold text-gray-900">{displayPrice}</h3>
            </div>
            
            <h4 className="text-[17px] font-semibold text-gray-800 mb-1 line-clamp-1">
            {propertyName}
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
                {description}
            </p>
            </div>
        </div>
        </div>
  );
};

export default PropertyCard;