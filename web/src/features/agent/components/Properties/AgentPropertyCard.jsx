import React from 'react';

const AgentPropertyCard = ({ property, onViewDetails }) => {
  const { id, name, basicDescription, location, priceRangeMin, priceRangeMax } = property;

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
    <div className="bg-white border border-gray-200 rounded-lg overflow-hidden shadow-md hover:shadow-lg transition-all duration-300 p-6 mb-5 hover:border-blue-300">
      <div className="flex gap-6">
        {/* Image Placeholder */}
        <div className="w-40 h-32 bg-gradient-to-br from-gray-300 to-gray-400 rounded-lg flex-shrink-0 flex items-center justify-center text-gray-600 text-sm font-medium shadow-sm">
          [Image]
        </div>

        {/* Property Details */}
        <div className="flex-1">
          <div className="flex justify-between items-start gap-4 mb-4">
            <div className="flex-1">
              <div className="flex items-center gap-2 mb-2">
                <h3 className="text-xl font-bold text-gray-900">{name}</h3>
                {property.units && property.units.length > 0 && (
                  <span className="inline-block bg-blue-100 text-blue-800 text-xs font-semibold px-2 py-1 rounded-full">
                    {property.units.length}u
                  </span>
                )}
              </div>
              <p className="text-gray-500 text-sm flex items-center mt-2 font-medium">
                <svg className="w-4 h-4 mr-2 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"></path>
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"></path>
                </svg>
                {location}
              </p>
            </div>
          </div>

          <p className="text-gray-600 text-sm line-clamp-2 leading-relaxed mb-4">{basicDescription}</p>

          {/* Price and Button Row */}
          <div className="flex justify-between items-center pt-4 border-t border-gray-200">
            <div>
              <p className="text-xs text-gray-600 font-bold uppercase tracking-wide mb-1">Price Range</p>
              <p className="text-2xl font-bold text-blue-600">{displayPrice}</p>
            </div>
            <button
              onClick={() => onViewDetails(id)}
              className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 px-8 rounded-lg transition-all duration-200 shadow-md hover:shadow-lg"
            >
              View Details
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AgentPropertyCard;
