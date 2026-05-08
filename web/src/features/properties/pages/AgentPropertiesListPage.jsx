import React, { useEffect } from 'react';
import { useProperties, usePropertySearch, usePropertyDetailAccess } from '../hooks';
import { PropertyCard, PropertySearchFilter, PropertyDetailModal } from '../components';
import { AgentSidebar } from '../../../shared/components/layout';

/**
 * AgentPropertiesListPage Component
 * Refactored to use vertical slicing architecture
 * Displays properties assigned to the agent
 */
export default function AgentPropertiesListPage() {
  const { properties, loading, error, fetchProperties } = useProperties();
  const {
    results: filterResults,
    search,
    updateFilters,
    clearFilters,
    hasSearched,
    searchByName,
    searchByLocation,
    searchByDeveloper,
    setPriceRange,
  } = usePropertySearch();
  const {
    isDetailModalOpen,
    selectedProperty,
    openDetailModal,
    closeDetailModal,
  } = usePropertyDetailAccess();

  // Load properties on component mount
  useEffect(() => {
    fetchProperties();
  }, [fetchProperties]);

  const handleFilterByListingType = async (listingType) => {
    if (!listingType) {
      clearFilters();
      return;
    }
    updateFilters({ listingType });
    await search();
  };

  const handlePropertyClick = (propertyId) => {
    const property = displayProperties.find((p) => p.id === propertyId);
    if (property) {
      openDetailModal(property);
    }
  };

  const handleViewDetails = (propertyId) => {
    const property = displayProperties.find((p) => p.id === propertyId);
    if (property) {
      openDetailModal(property);
    }
  };

  const displayProperties = hasSearched ? filterResults : properties;

  return (
    <div className="flex h-full">
      {/* Sidebar */}
      <AgentSidebar />
      
      {/* Main Content */}
      <div className="flex-1 ml-56 bg-gray-50 py-12">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          {/* Header Section */}
          <div className="mb-8">
            <h1 className="text-4xl font-bold text-gray-900 mb-2">My Properties</h1>
            <p className="text-gray-600 text-lg">
              Admin-created listings assigned to you. Use the selling points and target market
              information when talking with clients.
            </p>
          </div>

          {/* Error Alert */}
          {error && (
            <div className="mb-6 p-4 bg-red-100 border border-red-400 text-red-700 rounded-lg">
              {error}
            </div>
          )}

          {/* Filter Section */}
          <div className="mb-8">
            <PropertySearchFilter
              searchTypes={[
                { value: 'name', label: 'Property Name' },
                { value: 'location', label: 'Location' },
                { value: 'developer', label: 'Developer' },
              ]}
              onSearch={(term, type) => {
                if (type === 'name') searchByName(term);
                else if (type === 'location') searchByLocation(term);
                else if (type === 'developer') searchByDeveloper(term);
              }}
              onPriceRangeChange={(min, max) => setPriceRange(min, max)}
              onClearFilters={() => {
                clearFilters();
                fetchProperties();
              }}
              isLoading={loading}
            />

            <div className="mt-4 bg-white rounded-lg shadow p-6">
              <div className="flex gap-4 items-center">
                <label className="font-semibold text-gray-700">Filter by Listing Type:</label>
                <select
                  onChange={(e) => handleFilterByListingType(e.target.value)}
                  className="px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500"
                >
                  <option value="">All Listings</option>
                  <option value="Pre-Selling">Pre-Selling</option>
                  <option value="RFO">Ready for Occupancy (RFO)</option>
                  <option value="Resale">Resale</option>
                </select>
                {hasSearched && (
                  <button
                    onClick={() => handleFilterByListingType('')}
                    className="px-4 py-2 bg-gray-300 text-gray-800 rounded-lg hover:bg-gray-400 transition"
                  >
                    Clear Filter
                  </button>
                )}
              </div>
            </div>
          </div>

          {/* Properties Grid */}
          {loading ? (
            <div className="text-center py-12">
              <p className="text-gray-500 text-lg">Loading properties...</p>
            </div>
          ) : displayProperties && displayProperties.length > 0 ? (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {displayProperties.map((property) => (
                <PropertyCard
                  key={property.id}
                  property={property}
                  onClick={handlePropertyClick}
                  onView={handleViewDetails}
                />
              ))}
            </div>
          ) : (
            <div className="text-center py-12">
              <p className="text-gray-500 text-lg">
                {hasSearched
                  ? 'No properties found with the selected listing type.'
                  : 'No properties available.'}
              </p>
            </div>
          )}
        </div>
      </div>

      {/* Property Detail Modal */}
      <PropertyDetailModal
        property={selectedProperty}
        isOpen={isDetailModalOpen}
        onClose={closeDetailModal}
      />
    </div>
  );
}
