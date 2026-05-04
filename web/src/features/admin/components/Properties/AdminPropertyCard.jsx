import { FiEdit2, FiTrash2, FiEye } from 'react-icons/fi';

export default function AdminPropertyCard({ 
  property, 
  onView, 
  onEdit, 
  onDelete 
}) {
  const formatPrice = (price) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 0
    }).format(price);
  };

  return (
    <div className="bg-white rounded-lg shadow-md hover:shadow-lg transition-shadow duration-200 border border-slate-200 overflow-hidden">
      <div className="flex gap-6 p-6">
        {/* Image Section */}
        <div className="w-40 h-32 bg-gradient-to-br from-slate-400 to-slate-500 rounded-lg flex items-center justify-center flex-shrink-0 shadow-sm">
          <span className="text-white text-sm font-semibold">No Image</span>
        </div>

        {/* Details Section */}
        <div className="flex-1 flex flex-col justify-between">
          {/* Header */}
          <div>
            <div className="flex items-center justify-between mb-2">
              <h3 className="text-lg font-bold text-slate-900">{property.name}</h3>
              {property.units && property.units.length > 0 && (
                <span className="inline-block bg-red-100 text-red-800 text-xs font-semibold px-3 py-1 rounded-full">
                  {property.units.length} unit{property.units.length !== 1 ? 's' : ''}
                </span>
              )}
            </div>
            <div className="grid grid-cols-3 gap-4 mb-3">
              <div>
                <p className="text-xs text-slate-600 uppercase tracking-wide font-bold">Location</p>
                <p className="text-sm text-slate-900 font-bold">{property.location}</p>
              </div>
              <div>
                <p className="text-xs text-slate-600 uppercase tracking-wide font-bold">Developer</p>
                <p className="text-sm text-slate-900 font-bold">{property.developer}</p>
              </div>
              <div>
                <p className="text-xs text-slate-600 uppercase tracking-wide font-bold">Listing Type</p>
                <p className="text-sm text-slate-900 font-bold">{property.listingType}</p>
              </div>
            </div>
            <p className="text-sm text-slate-700 font-medium line-clamp-2">{property.basicDescription}</p>
          </div>

          {/* Footer with Price and Buttons */}
          <div className="flex justify-between items-center pt-4 border-t border-slate-200 mt-4">
            <div className="flex flex-col">
              <p className="text-xs text-slate-600 uppercase tracking-wide font-bold">Price Range</p>
              <p className="text-lg font-bold text-slate-900">
                {formatPrice(property.priceRangeMin)} - {formatPrice(property.priceRangeMax)}
              </p>
            </div>
            
            {/* Action Buttons */}
            <div className="flex gap-3">
              <button
                onClick={() => onView(property.id)}
                className="flex items-center gap-2 px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-colors duration-200 font-medium shadow-sm"
              >
                <FiEye size={18} />
                View
              </button>
              <button
                onClick={() => onEdit(property.id)}
                className="flex items-center gap-2 px-4 py-2 bg-amber-600 hover:bg-amber-700 text-white rounded-lg transition-colors duration-200 font-medium shadow-sm"
              >
                <FiEdit2 size={18} />
                Edit
              </button>
              <button
                onClick={() => onDelete(property.id)}
                className="flex items-center gap-2 px-4 py-2 bg-red-600 hover:bg-red-700 text-white rounded-lg transition-colors duration-200 font-medium shadow-sm"
              >
                <FiTrash2 size={18} />
                Delete
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
