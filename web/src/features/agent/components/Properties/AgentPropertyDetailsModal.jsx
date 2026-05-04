import React, { useState, useEffect } from 'react';
import { FiX } from 'react-icons/fi';
import { property } from '../../../../api/property';

const AgentPropertyDetailsModal = ({ isOpen, onClose, propertyId }) => {
  const [propertyDetails, setPropertyDetails] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchPropertyDetails = async () => {
      setLoading(true);
      setError(null);
      try {
        const data = await property.getAgentPropertyDetails(propertyId);
        const listingTypeArray = data?.listingType
          ? data.listingType.split(',').map((t) => t.trim())
          : [];
        setPropertyDetails({ ...data, listingType: listingTypeArray, units: data.units || [] });
      } catch (err) {
        console.error('Error fetching property details:', err);
        setError('Failed to load property details');
      } finally {
        setLoading(false);
      }
    };

    if (isOpen && propertyId) fetchPropertyDetails();
  }, [isOpen, propertyId]);

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-transparent backdrop-brightness-30 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-lg shadow-2xl max-w-6xl w-full max-h-[90vh] overflow-y-auto">
        {/* Header */}
        <div className="sticky top-0 bg-gradient-to-r from-blue-600 to-blue-700 px-10 py-7 flex justify-between items-center shadow-md z-10">
          <div>
            <h2 className="text-3xl font-bold text-white">Property Details</h2>
            <p className="text-blue-100 text-sm mt-1">View property information</p>
          </div>
          <button onClick={onClose} className="text-white hover:bg-blue-800 p-2 rounded-lg transition-colors duration-200">
            <FiX size={28} />
          </button>
        </div>

        {/* Content */}
        <div className="p-10 space-y-10">
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
            <>
              {/* Basic Information */}
              <div>
                <h3 className="text-2xl font-bold text-slate-900 mb-4">Basic Information</h3>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div>
                    <p className="text-xs text-slate-700 uppercase tracking-wide font-bold">Property Name</p>
                    <p className="text-lg text-slate-900 font-semibold">{propertyDetails.name}</p>
                  </div>
                  <div>
                    <p className="text-xs text-slate-700 uppercase tracking-wide font-bold">Location</p>
                    <p className="text-lg text-slate-900 font-semibold">{propertyDetails.location}</p>
                  </div>
                  <div>
                    <p className="text-xs text-slate-700 uppercase tracking-wide font-bold">Developer</p>
                    <p className="text-lg text-slate-900 font-semibold">{propertyDetails.developer || 'N/A'}</p>
                  </div>
                </div>

                <div className="mt-6">
                  <p className="text-xs text-slate-700 uppercase tracking-wide font-bold">Basic Description</p>
                  <p className="text-gray-700 leading-relaxed text-base mt-2">{propertyDetails.basicDescription}</p>
                </div>
              </div>

              {/* Pricing */}
              <div>
                <h3 className="text-2xl font-bold text-slate-900 mb-4">Pricing Information</h3>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div className="bg-blue-50 p-6 rounded-lg border border-blue-200">
                    <p className="text-xs text-slate-700 uppercase tracking-wide font-bold">Minimum Price</p>
                    <p className="text-3xl font-bold text-blue-600">₱{propertyDetails.priceRangeMin ? parseFloat(propertyDetails.priceRangeMin).toLocaleString() : 'N/A'}</p>
                  </div>
                  <div className="bg-blue-50 p-6 rounded-lg border border-blue-200">
                    <p className="text-xs text-slate-700 uppercase tracking-wide font-bold">Maximum Price</p>
                    <p className="text-3xl font-bold text-blue-600">₱{propertyDetails.priceRangeMax ? parseFloat(propertyDetails.priceRangeMax).toLocaleString() : 'N/A'}</p>
                  </div>
                </div>
              </div>

              {/* Property Details */}
              <div>
                <h3 className="text-2xl font-bold text-slate-900 mb-4">Property Details</h3>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div className="bg-gray-50 p-6 rounded-lg">
                    <p className="text-xs text-slate-700 uppercase tracking-wide font-bold">Listing Type</p>
                    <p className="text-slate-900 font-semibold">{Array.isArray(propertyDetails.listingType) ? propertyDetails.listingType.join(', ') : (propertyDetails.listingType || 'N/A')}</p>
                  </div>
                  <div className="bg-gray-50 p-6 rounded-lg">
                    <p className="text-xs text-slate-700 uppercase tracking-wide font-bold">Turnover Date</p>
                    <p className="text-slate-900 font-semibold">{propertyDetails.turnoverDate || 'N/A'}</p>
                  </div>
                  <div className="bg-gray-50 p-6 rounded-lg">
                    <p className="text-xs text-slate-700 uppercase tracking-wide font-bold">Pet Friendly</p>
                    <p className="text-slate-900 font-semibold">{propertyDetails.petFriendly ? '✅ Yes' : '❌ No'}</p>
                  </div>
                  <div className="bg-gray-50 p-6 rounded-lg">
                    <p className="text-xs text-slate-700 uppercase tracking-wide font-bold">Parking Available</p>
                    <p className="text-slate-900 font-semibold">{propertyDetails.parkingAvailable ? '✅ Yes' : '❌ No'}</p>
                  </div>
                </div>
              </div>

              {/* Photo Placeholder */}
              <div>
                <h3 className="text-2xl font-bold text-slate-900 mb-4">Property Photos</h3>
                <div className="p-8 bg-gradient-to-br from-gray-100 to-gray-200 rounded-lg border-2 border-gray-300 text-center">
                  <p className="text-gray-600 font-semibold">Photo Gallery Coming Soon</p>
                </div>
              </div>

              {/* Amenities */}
              {propertyDetails.amenities && (
                <div>
                  <h3 className="text-2xl font-bold text-slate-900 mb-4">Amenities</h3>
                  <p className="text-gray-700 leading-relaxed text-base">{propertyDetails.amenities}</p>
                </div>
              )}

              {/* Units Display */}
              {propertyDetails.units && propertyDetails.units.length > 0 && (
                <div>
                  <h3 className="text-2xl font-bold text-slate-900 mb-4">Property Units ({propertyDetails.units.length})</h3>
                  <div className="overflow-x-auto">
                    <table className="w-full text-sm">
                      <thead className="bg-slate-200">
                        <tr>
                          <th className="px-4 py-3 text-left font-semibold text-slate-900">Type</th>
                          <th className="px-4 py-3 text-left font-semibold text-slate-900">Floor/Lot (sqm)</th>
                          <th className="px-4 py-3 text-left font-semibold text-slate-900">Pricing</th>
                          <th className="px-4 py-3 text-left font-semibold text-slate-900">Financing</th>
                        </tr>
                      </thead>
                      <tbody>
                        {propertyDetails.units.map((unit, idx) => (
                          <tr key={idx} className="border-b border-slate-200 hover:bg-slate-50">
                            <td className="px-4 py-3 font-medium text-slate-900">{unit.unitType}</td>
                            <td className="px-4 py-3 text-slate-700">{unit.floorArea} / {unit.lotArea}</td>
                            <td className="px-4 py-3 text-slate-700">₱{unit.totalSellingPrice ? parseFloat(unit.totalSellingPrice).toLocaleString() : 'N/A'}</td>
                            <td className="px-4 py-3 text-slate-700 text-xs">
                              <div className="flex flex-wrap gap-1">
                                {unit.financingTypes && unit.financingTypes.map((t, i) => (
                                  <span key={i} className="bg-slate-200 px-2 py-1 rounded">{t}</span>
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

              {/* Agent Tools */}
              <div className="bg-gradient-to-br from-blue-50 to-blue-100 p-8 rounded-lg border-2 border-blue-200">
                <h3 className="text-2xl font-bold text-slate-900 mb-4">Agent Tools & Resources</h3>
                <div className="space-y-4">
                  {propertyDetails.keySellingPoints && (
                    <div>
                      <p className="text-sm text-slate-700 font-bold uppercase tracking-wide mb-2">Key Selling Points</p>
                      <p className="text-gray-700 bg-white p-4 rounded border border-blue-300 leading-relaxed">{propertyDetails.keySellingPoints}</p>
                    </div>
                  )}
                  {propertyDetails.brochurePdfUrl && (
                    <div>
                      <p className="text-sm text-slate-700 font-bold uppercase tracking-wide mb-2">Brochure</p>
                      <a href={propertyDetails.brochurePdfUrl} target="_blank" rel="noopener noreferrer" className="text-blue-600 font-semibold hover:text-blue-800 underline text-sm break-all bg-white p-4 rounded border border-blue-300 block">{propertyDetails.brochurePdfUrl}</a>
                    </div>
                  )}
                  {propertyDetails.inventoryLink && (
                    <div>
                      <p className="text-sm text-slate-700 font-bold uppercase tracking-wide mb-2">Inventory & Floor Plans</p>
                      <a href={propertyDetails.inventoryLink} target="_blank" rel="noopener noreferrer" className="text-blue-600 font-semibold hover:text-blue-800 underline text-sm break-all bg-white p-4 rounded border border-blue-300 block">{propertyDetails.inventoryLink}</a>
                    </div>
                  )}
                </div>
              </div>

              {/* Metadata */}
              <div className="grid grid-cols-1 md:grid-cols-3 gap-6 p-6 bg-slate-50 rounded-lg">
                <div>
                  <p className="text-xs text-slate-700 uppercase tracking-wide font-bold">Created By</p>
                  <p className="text-sm text-slate-900 font-semibold">{propertyDetails.createdBy || 'N/A'}</p>
                </div>
                <div>
                  <p className="text-xs text-slate-700 uppercase tracking-wide font-bold">Created At</p>
                  <p className="text-sm text-slate-900 font-semibold">{propertyDetails.createdAt ? new Date(propertyDetails.createdAt).toLocaleDateString() : 'N/A'}</p>
                </div>
                <div>
                  <p className="text-xs text-slate-700 uppercase tracking-wide font-bold">Updated At</p>
                  <p className="text-sm text-slate-900 font-semibold">{propertyDetails.updatedAt ? new Date(propertyDetails.updatedAt).toLocaleDateString() : 'N/A'}</p>
                </div>
              </div>
            </>
          )}
        </div>

        {/* Footer */}
        <div className="sticky bottom-0 bg-slate-50 px-10 py-6 flex justify-end gap-4 border-t border-slate-200 shadow-md">
          <button onClick={onClose} className="bg-gray-600 hover:bg-gray-700 text-white font-bold py-3 px-8 rounded-lg transition-all duration-200 shadow-md">Close</button>
        </div>
      </div>
    </div>
  );
};

export default AgentPropertyDetailsModal;
