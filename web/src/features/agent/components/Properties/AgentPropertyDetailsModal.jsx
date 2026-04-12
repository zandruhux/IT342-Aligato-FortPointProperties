import React, { useState, useEffect } from 'react';
import { property } from '../../../../api/property';

const AgentPropertyDetailsModal = ({ isOpen, onClose, propertyId }) => {
  const [propertyDetails, setPropertyDetails] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (isOpen && propertyId) {
      fetchPropertyDetails();
    }
  }, [isOpen, propertyId]);

  const fetchPropertyDetails = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await property.getAgentPropertyDetails(propertyId);
      setPropertyDetails(data);
    } catch (err) {
      console.error('Error fetching property details:', err);
      setError('Failed to load property details');
    } finally {
      setLoading(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-30 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-lg max-w-4xl w-full max-h-[92vh] overflow-y-auto shadow-2xl">
        {/* Header */}
        <div className="sticky top-0 bg-gradient-to-r from-blue-600 to-blue-700 text-white px-10 py-7 flex justify-between items-center shadow-md">
          <h2 className="text-2xl font-bold">Property Details</h2>
          <button
            onClick={onClose}
            className="text-white hover:bg-blue-800 p-2 rounded transition-colors"
          >
            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12"></path>
            </svg>
          </button>
        </div>

        {/* Content */}
        <div className="p-10">
          {loading && (
            <div className="text-center py-16">
              <p className="text-gray-600 font-medium text-lg">Loading property details...</p>
            </div>
          )}

          {error && (
            <div className="bg-red-50 border-l-4 border-red-500 text-red-700 px-6 py-4 rounded mb-6 font-medium">
              {error}
            </div>
          )}

          {propertyDetails && (
            <div className="space-y-10">
              {/* Basic Information */}
              <section>
                <h3 className="text-2xl font-bold text-gray-900 mb-6 pb-3 border-b-2 border-blue-600">
                  Basic Information
                </h3>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                  <div>
                    <p className="text-sm text-gray-600 font-bold uppercase tracking-wide mb-2">Property Name</p>
                    <p className="text-lg text-gray-900 font-semibold">{propertyDetails.propertyName}</p>
                  </div>
                  <div>
                    <p className="text-sm text-gray-600 font-bold uppercase tracking-wide mb-2">Location</p>
                    <p className="text-lg text-gray-900 font-semibold">{propertyDetails.location}</p>
                  </div>
                  <div>
                    <p className="text-sm text-gray-600 font-bold uppercase tracking-wide mb-2">Developer Name</p>
                    <p className="text-lg text-gray-900 font-semibold">{propertyDetails.developerName || 'N/A'}</p>
                  </div>
                  <div>
                    <p className="text-sm text-gray-600 font-bold uppercase tracking-wide mb-2">Property Type</p>
                    <p className="text-lg text-gray-900 font-semibold">{propertyDetails.propertyType || 'N/A'}</p>
                  </div>
                </div>
                <div className="mt-8">
                  <p className="text-sm text-gray-600 font-bold uppercase tracking-wide mb-3">Description</p>
                  <p className="text-gray-700 leading-relaxed text-base">{propertyDetails.description}</p>
                </div>
              </section>

              {/* Pricing Information */}
              <section>
                <h3 className="text-2xl font-bold text-gray-900 mb-6 pb-3 border-b-2 border-blue-600">
                  Pricing Information
                </h3>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                  <div className="bg-blue-50 p-8 rounded-lg border border-blue-200">
                    <p className="text-sm text-gray-600 font-bold uppercase tracking-wide mb-3">Minimum Price</p>
                    <p className="text-3xl font-bold text-blue-600">
                      ₱{propertyDetails.priceRangeMin?.toLocaleString()}
                    </p>
                  </div>
                  <div className="bg-blue-50 p-8 rounded-lg border border-blue-200">
                    <p className="text-sm text-gray-600 font-bold uppercase tracking-wide mb-3">Maximum Price</p>
                    <p className="text-3xl font-bold text-blue-600">
                      ₱{propertyDetails.priceRangeMax?.toLocaleString()}
                    </p>
                  </div>
                </div>
              </section>

              {/* Property Details */}
              <section>
                <h3 className="text-2xl font-bold text-gray-900 mb-6 pb-3 border-b-2 border-blue-600">
                  Property Details
                </h3>
                <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                  <div className="bg-gray-50 p-6 rounded-lg">
                    <p className="text-sm text-gray-600 font-bold uppercase tracking-wide mb-2">Unit Type</p>
                    <p className="text-gray-900 font-semibold">{propertyDetails.unitType || 'N/A'}</p>
                  </div>
                  <div className="bg-gray-50 p-6 rounded-lg">
                    <p className="text-sm text-gray-600 font-bold uppercase tracking-wide mb-2">Listing Type</p>
                    <p className="text-gray-900 font-semibold">{propertyDetails.listingType || 'N/A'}</p>
                  </div>
                  <div className="bg-gray-50 p-6 rounded-lg">
                    <p className="text-sm text-gray-600 font-bold uppercase tracking-wide mb-2">Turnover Date</p>
                    <p className="text-gray-900 font-semibold">{propertyDetails.turnoverDate || 'N/A'}</p>
                  </div>
                  <div className="bg-gray-50 p-6 rounded-lg">
                    <p className="text-sm text-gray-600 font-bold uppercase tracking-wide mb-2">Pet Friendly</p>
                    <p className="text-gray-900 font-semibold">
                      {propertyDetails.petFriendly ? '✅ Yes' : '❌ No'}
                    </p>
                  </div>
                  <div className="bg-gray-50 p-6 rounded-lg">
                    <p className="text-sm text-gray-600 font-bold uppercase tracking-wide mb-2">Parking Available</p>
                    <p className="text-gray-900 font-semibold">
                      {propertyDetails.parkingAvailable ? '✅ Yes' : '❌ No'}
                    </p>
                  </div>
                </div>
              </section>

              {/* Amenities */}
              {propertyDetails.amenities && (
                <section>
                  <h3 className="text-2xl font-bold text-gray-900 mb-6 pb-3 border-b-2 border-blue-600">
                    Amenities
                  </h3>
                  <p className="text-gray-700 leading-relaxed text-base">{propertyDetails.amenities}</p>
                </section>
              )}

              {/* Agent-Specific Information */}
              <section className="bg-gradient-to-br from-blue-50 to-blue-100 p-10 rounded-lg border-2 border-blue-200">
                <h3 className="text-2xl font-bold text-gray-900 mb-8">Agent Information</h3>
                <div className="space-y-6">
                  {propertyDetails.priceComputations && (
                    <div>
                      <p className="text-sm text-gray-700 font-bold uppercase tracking-wide mb-3">Price Computations</p>
                      <p className="text-gray-700 bg-white p-5 rounded border border-blue-300 leading-relaxed">
                        {propertyDetails.priceComputations}
                      </p>
                    </div>
                  )}
                  {propertyDetails.pitchReadyPhrases && (
                    <div>
                      <p className="text-sm text-gray-700 font-bold uppercase tracking-wide mb-3">Pitch Ready Phrases</p>
                      <p className="text-gray-700 bg-white p-5 rounded border border-blue-300 leading-relaxed">
                        {propertyDetails.pitchReadyPhrases}
                      </p>
                    </div>
                  )}
                  {propertyDetails.developerLinks && (
                    <div>
                      <p className="text-sm text-gray-700 font-bold uppercase tracking-wide mb-3">Developer Links</p>
                      <a 
                        href={propertyDetails.developerLinks} 
                        target="_blank" 
                        rel="noopener noreferrer"
                        className="text-blue-600 font-semibold hover:text-blue-800 underline text-sm break-all bg-white p-5 rounded border border-blue-300 block"
                      >
                        {propertyDetails.developerLinks}
                      </a>
                    </div>
                  )}
                </div>
              </section>
            </div>
          )}
        </div>

        {/* Footer */}
        <div className="sticky bottom-0 bg-gray-50 border-t-2 border-gray-200 px-10 py-6 flex justify-end">
          <button
            onClick={onClose}
            className="bg-gray-600 hover:bg-gray-700 text-white font-bold py-3 px-8 rounded-lg transition-all duration-200 shadow-md hover:shadow-lg"
          >
            Close
          </button>
        </div>
      </div>
    </div>
  );
};

export default AgentPropertyDetailsModal;
