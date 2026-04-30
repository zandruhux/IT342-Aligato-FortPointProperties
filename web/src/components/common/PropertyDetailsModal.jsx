import React, { useState, useEffect } from 'react';
import { jwtDecode } from 'jwt-decode';
import { property as propertyAPI } from '../../api/property';

const PropertyDetailsModal = ({ isOpen, onClose, propertyId }) => {
  const [details, setDetails] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [userRole, setUserRole] = useState(null);
  const [isFavorited, setIsFavorited] = useState(false);
  const [isLoadingFavorite, setIsLoadingFavorite] = useState(false);

  useEffect(() => {
    if (!isOpen || !propertyId) return;

    const fetchDetails = async () => {
      setLoading(true);
      setError(null);
      
      try {
        const token = localStorage.getItem('accessToken');
        if (!token) {
          throw new Error("You must be logged in to view advanced details.");
        }

        // Decode token to find the role. 
        // Note: Change 'role' to match whatever your Java backend calls the claim (e.g., 'roles', 'authorities')
        const decoded = jwtDecode(token);
        const role = decoded.role || decoded.authorities; 
        setUserRole(role);

        // Check favorite status
        const isFav = await propertyAPI.checkIfFavorited(propertyId);
        setIsFavorited(isFav);

        let data;
        // Check if role is AGENT (Adjust the string to match your exact backend role name, e.g., 'ROLE_AGENT')
        if (role === 'AGENT' || role === 'ROLE_AGENT') {
          data = await propertyAPI.getAgentPropertyDetails(propertyId);
        } else {
          // Default to registered user view
          data = await propertyAPI.getUserPropertyDetails(propertyId);
        }
        
        setDetails(data);
      } catch (err) {
        setError(err.message || "Failed to load property details.");
      } finally {
        setLoading(false);
      }
    };

    fetchDetails();
  }, [isOpen, propertyId]);

  const handleFavoriteClick = async (e) => {
    e.stopPropagation();
    setIsLoadingFavorite(true);

    try {
      if (isFavorited) {
        await propertyAPI.removeFromFavorites(propertyId);
        setIsFavorited(false);
      } else {
        await propertyAPI.addToFavorites(propertyId);
        setIsFavorited(true);
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

  if (!isOpen) return null;

  // Format currency
  const formatPrice = (price) => new Intl.NumberFormat('en-PH', { style: 'currency', currency: 'PHP', minimumFractionDigits: 0 }).format(price);

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/20 backdrop-blur-sm p-4 overflow-y-auto">
      <div className="bg-white rounded-xl shadow-2xl w-full max-w-5xl max-h-[90vh] overflow-y-auto relative">
        
        {/* Close and Favorite Buttons */}
        <div className="absolute top-4 right-4 flex items-center gap-2 z-50">
          <button 
            onClick={handleFavoriteClick}
            disabled={isLoadingFavorite}
            className="bg-white rounded-full p-2 shadow-md hover:bg-gray-50 transition disabled:opacity-50"
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
          <button onClick={onClose} className="text-gray-500 hover:text-gray-900 bg-gray-100 hover:bg-gray-200 transition-colors rounded-full p-2">
            ✕
          </button>
        </div>

        <div className="p-8">
          {loading && <p className="text-center text-gray-500 py-10">Loading advanced details...</p>}
          {error && <p className="text-center text-red-500 py-10">{error}</p>}
          
          {!loading && !error && details && (
            <div>
              <h2 className="text-4xl font-bold text-gray-900 mb-2">{details.name}</h2>
              <p className="text-gray-500 mb-6 flex items-center">
                 Location: {details.location}
              </p>

              <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-8 bg-gray-50 p-6 rounded-lg">
                <div>
                  <p className="text-sm text-gray-500">Price Range</p>
                  <p className="font-semibold text-lg">
                    {details.priceRangeMin === details.priceRangeMax 
                      ? formatPrice(details.priceRangeMin) 
                      : `${formatPrice(details.priceRangeMin)} - ${formatPrice(details.priceRangeMax)}`}
                  </p>
                </div>
                <div>
                  <p className="text-sm text-gray-500">Listing Type</p>
                  <div className="flex flex-wrap gap-2">
                    {details.listingType && details.listingType.split(',').map((type, idx) => (
                      <span key={idx} className="bg-blue-100 text-blue-800 px-2 py-1 rounded text-sm font-medium">
                        {type.trim()}
                      </span>
                    ))}
                  </div>
                </div>
                <div>
                  <p className="text-sm text-gray-500">Turnover Date</p>
                  <p className="font-semibold">{details.turnoverDate}</p>
                </div>
              </div>

              {/* Photo Placeholder */}
              <div className="mb-6 p-8 bg-gradient-to-br from-gray-100 to-gray-200 rounded-lg border-2 border-gray-300 text-center">
                <p className="text-gray-600 font-semibold">Photo Gallery Coming Soon</p>
              </div>

              <div className="mb-6">
                <h3 className="text-2xl font-bold mb-3">Description</h3>
                <p className="text-gray-700 whitespace-pre-line leading-relaxed">{details.basicDescription}</p>
              </div>

              <div className="mb-6">
                <h3 className="text-2xl font-bold mb-3">Features & Amenities</h3>
                <p className="text-gray-700 mb-4">{details.amenities}</p>
                <div className="flex gap-4 mt-2">
                  {details.petFriendly && <span className="bg-green-50 border border-green-200 text-green-800 px-4 py-1.5 rounded-full text-sm font-medium">✅ Pet Friendly</span>}
                  {!details.petFriendly && <span className="bg-gray-50 border border-gray-200 text-gray-800 px-4 py-1.5 rounded-full text-sm font-medium">❌ Not Pet Friendly</span>}
                  {details.parkingAvailable && <span className="bg-blue-50 border border-blue-200 text-blue-800 px-4 py-1.5 rounded-full text-sm font-medium">✅ Parking Available</span>}
                  {!details.parkingAvailable && <span className="bg-gray-50 border border-gray-200 text-gray-800 px-4 py-1.5 rounded-full text-sm font-medium">❌ No Parking</span>}
                </div>
              </div>

              {/* AGENT EXCLUSIVE SECTION */}
              {(userRole === 'AGENT' || userRole === 'ROLE_AGENT') && (
                <div className="mt-10 border-t-2 border-dashed border-blue-200 pt-8 bg-blue-50/50 -mx-8 px-8 pb-8 rounded-b-xl">
                  <div className="flex items-center mb-6">
                    <span className="bg-blue-600 text-white text-xs font-bold px-3 py-1 rounded mr-3 uppercase tracking-wider">Agent Only</span>
                    <h3 className="text-2xl font-bold text-blue-900">Agent Toolkit</h3>
                  </div>
                  
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-8 mb-6">
                    <div>
                      <p className="text-sm font-bold text-gray-500 uppercase tracking-wider mb-1">Developer</p>
                      <p className="text-gray-900 text-lg font-semibold">{details.developer}</p>
                    </div>
                    
                    <div>
                      <p className="text-sm font-bold text-gray-500 uppercase tracking-wider mb-1">Turnover Date</p>
                      <p className="text-gray-900 text-lg font-semibold">{details.turnoverDate}</p>
                    </div>
                  </div>

                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
                    {details.brochurePdfUrl && (
                      <div className="bg-white p-4 rounded-lg border border-blue-100 hover:border-blue-300 transition">
                        <p className="text-sm font-bold text-gray-500 uppercase tracking-wider mb-2">Brochure PDF</p>
                        <a 
                          href={details.brochurePdfUrl} 
                          target="_blank" 
                          rel="noopener noreferrer" 
                          className="text-blue-600 hover:text-blue-800 font-semibold break-all underline flex items-center gap-2"
                        >
                          📄 Download Brochure
                        </a>
                      </div>
                    )}
                    {details.inventoryLink && (
                      <div className="bg-white p-4 rounded-lg border border-blue-100 hover:border-blue-300 transition">
                        <p className="text-sm font-bold text-gray-500 uppercase tracking-wider mb-2">Inventory/Floor Plan</p>
                        <a 
                          href={details.inventoryLink} 
                          target="_blank" 
                          rel="noopener noreferrer" 
                          className="text-blue-600 hover:text-blue-800 font-semibold break-all underline flex items-center gap-2"
                        >
                          🏗️ View Inventory
                        </a>
                      </div>
                    )}
                  </div>

                  {details.keySellingPoints && (
                    <div className="bg-white p-5 rounded-lg border border-blue-100 border-l-4 border-l-blue-500">
                      <p className="text-sm font-bold text-gray-500 uppercase tracking-wider mb-2">Key Selling Points</p>
                      <p className="text-gray-900 italic text-lg">"{details.keySellingPoints}"</p>
                    </div>
                  )}

                  {details.units && details.units.length > 0 && (
                    <div className="bg-white p-5 rounded-lg border border-blue-100">
                      <p className="text-sm font-bold text-gray-500 uppercase tracking-wider mb-4">Property Units ({details.units.length})</p>
                      <div className="overflow-x-auto">
                        <table className="w-full text-sm">
                          <thead className="bg-blue-50">
                            <tr>
                              <th className="px-4 py-2 text-left font-semibold text-gray-900">Type</th>
                              <th className="px-4 py-2 text-left font-semibold text-gray-900">Floor/Lot (sqm)</th>
                              <th className="px-4 py-2 text-left font-semibold text-gray-900">Price</th>
                              <th className="px-4 py-2 text-left font-semibold text-gray-900">Financing</th>
                            </tr>
                          </thead>
                          <tbody>
                            {details.units.map((unit, idx) => (
                              <tr key={idx} className="border-b border-gray-200 hover:bg-gray-50">
                                <td className="px-4 py-2 font-medium text-gray-900">{unit.unitType}</td>
                                <td className="px-4 py-2 text-gray-700">{unit.floorArea} / {unit.lotArea}</td>
                                <td className="px-4 py-2 text-gray-900 font-semibold">{formatPrice(unit.totalSellingPrice)}</td>
                                <td className="px-4 py-2">
                                  <div className="flex flex-wrap gap-1">
                                    {unit.financingTypes && unit.financingTypes.map((type, i) => (
                                      <span key={i} className="bg-blue-100 text-blue-800 px-2 py-1 text-xs rounded">{type}</span>
                                    ))}
                                  </div>
                                </td>
                              </tr>
                            ))}
                          </tbody>
                        </table>
                      </div>
                    </div>
                  )}
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default PropertyDetailsModal;