import React, { useState, useEffect } from 'react';
import { jwtDecode } from 'jwt-decode';
import { property } from '../../api/property';

const PropertyDetailsModal = ({ isOpen, onClose, propertyId }) => {
  const [details, setDetails] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [userRole, setUserRole] = useState(null);

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

        let data;
        // Check if role is AGENT (Adjust the string to match your exact backend role name, e.g., 'ROLE_AGENT')
        if (role === 'AGENT' || role === 'ROLE_AGENT') {
          data = await property.getAgentPropertyDetails(propertyId);
        } else {
          // Default to registered user view
          data = await property.getUserPropertyDetails(propertyId);
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

  if (!isOpen) return null;

  // Format currency
  const formatPrice = (price) => new Intl.NumberFormat('en-PH', { style: 'currency', currency: 'PHP', minimumFractionDigits: 0 }).format(price);

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/20 backdrop-blur-sm p-4 overflow-y-auto">
      <div className="bg-white rounded-xl shadow-2xl w-full max-w-5xl max-h-[90vh] overflow-y-auto relative">
        
        {/* Close Button */}
        <button onClick={onClose} className="absolute top-4 right-4 text-gray-500 hover:text-gray-900 bg-gray-100 hover:bg-gray-200 transition-colors rounded-full p-2">
          ✕
        </button>

        <div className="p-8">
          {loading && <p className="text-center text-gray-500 py-10">Loading advanced details...</p>}
          {error && <p className="text-center text-red-500 py-10">{error}</p>}
          
          {!loading && !error && details && (
            <div>
              <h2 className="text-4xl font-bold text-gray-900 mb-2">{details.propertyName}</h2>
              <p className="text-gray-500 mb-6 flex items-center">
                 Location: {details.location}
              </p>

              <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8 bg-gray-50 p-6 rounded-lg">
                <div>
                  <p className="text-sm text-gray-500">Price Range</p>
                  <p className="font-semibold text-lg">
                    {details.priceRangeMin === details.priceRangeMax 
                      ? formatPrice(details.priceRangeMin) 
                      : `${formatPrice(details.priceRangeMin)} - ${formatPrice(details.priceRangeMax)}`}
                  </p>
                </div>
                <div>
                  <p className="text-sm text-gray-500">Type</p>
                  <p className="font-semibold">{details.propertyType} - {details.unitType}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-500">Listing Type</p>
                  <p className="font-semibold">{details.listingType}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-500">Turnover Date</p>
                  <p className="font-semibold">{details.turnoverDate}</p>
                </div>
              </div>

              <div className="mb-6">
                <h3 className="text-2xl font-bold mb-3">Description</h3>
                <p className="text-gray-700 whitespace-pre-line leading-relaxed">{details.description}</p>
              </div>

              <div className="mb-6">
                <h3 className="text-2xl font-bold mb-3">Features & Amenities</h3>
                <p className="text-gray-700 mb-4">{details.amenities}</p>
                <div className="flex gap-4 mt-2">
                  {details.petFriendly && <span className="bg-green-50 border border-green-200 text-green-800 px-4 py-1.5 rounded-full text-sm font-medium">Pet Friendly</span>}
                  {details.parkingAvailable && <span className="bg-blue-50 border border-blue-200 text-blue-800 px-4 py-1.5 rounded-full text-sm font-medium">Parking Available</span>}
                </div>
              </div>

              {/* AGENT EXCLUSIVE SECTION */}
              {(userRole === 'AGENT' || userRole === 'ROLE_AGENT') && (
                <div className="mt-10 border-t-2 border-dashed border-blue-200 pt-8 bg-blue-50/50 -mx-8 px-8 pb-8 rounded-b-xl">
                  <div className="flex items-center mb-6">
                    <span className="bg-blue-600 text-white text-xs font-bold px-3 py-1 rounded mr-3 uppercase tracking-wider">Agent Only</span>
                    <h3 className="text-2xl font-bold text-blue-900">Agent Toolkit</h3>
                  </div>
                  
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                    <div>
                      <div className="mb-5">
                        <p className="text-sm font-bold text-gray-500 uppercase tracking-wider mb-1">Developer</p>
                        <p className="text-gray-900 text-lg">{details.developerName}</p>
                      </div>
                      
                      <div>
                        <p className="text-sm font-bold text-gray-500 uppercase tracking-wider mb-1">Developer Links</p>
                        <p className="text-blue-600 break-all">{details.developerLinks}</p>
                      </div>
                    </div>
                    
                    <div>
                      <p className="text-sm font-bold text-gray-500 uppercase tracking-wider mb-1">Price Computations</p>
                      <div className="bg-white p-4 rounded-lg border border-blue-100">
                        <p className="text-gray-900 whitespace-pre-line text-sm font-mono">{details.priceComputations}</p>
                      </div>
                    </div>

                    <div className="col-span-1 md:col-span-2">
                      <p className="text-sm font-bold text-gray-500 uppercase tracking-wider mb-2">Pitch Ready Phrases</p>
                      <div className="bg-white p-5 rounded-lg border border-blue-100 border-l-4 border-l-blue-500">
                        <p className="text-gray-900 italic text-lg">"{details.pitchReadyPhrases}"</p>
                      </div>
                    </div>
                  </div>
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