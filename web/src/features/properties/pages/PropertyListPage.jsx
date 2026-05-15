import React, { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { useAuthContext } from '../../../shared/context/useAuthContext';
import { useFavorites } from '../../favorites/hooks/useFavorites';
import { useProperties, usePropertySearch, usePropertyDetailAccess } from '../hooks';
import { PropertyCard, PropertyDetailModal } from '../components';

/**
 * PropertyListPage Component
 * Displays list of public properties with search/filter capabilities
 * Accessible to all users (public and authenticated)
 */
export default function PropertyListPage() {
  const [searchParams] = useSearchParams();
  const { isLoggedIn, isRegisteredUser } = useAuthContext();
  const { favoriteIds, loading: favoritesLoading, fetchFavorites, toggleFavorite } = useFavorites();
  const [pendingFavoriteIds, setPendingFavoriteIds] = useState(new Set());
  const { properties, loading, fetchProperties } = useProperties();
  const { results: searchResults, searchByLocation, clearFilters, hasSearched } = usePropertySearch();
  const {
    isDetailModalOpen,
    selectedProperty,
    openDetailModal,
    closeDetailModal,
  } = usePropertyDetailAccess();

  const canUseFavorites = isLoggedIn && isRegisteredUser();

  // Load properties on component mount
  useEffect(() => {
    const locationQuery = searchParams.get('location');
    if (locationQuery) {
      searchByLocation(locationQuery);
    } else {
      fetchProperties();
    }
  }, [fetchProperties, searchByLocation, searchParams]);

  useEffect(() => {
    if (!canUseFavorites) {
      setPendingFavoriteIds(new Set());
      return;
    }

    fetchFavorites();
  }, [canUseFavorites, fetchFavorites]);

  const handleSearch = async (location) => {
    if (!location.trim()) {
      clearFilters();
      return;
    }
    await searchByLocation(location);
  };

  const handleClearSearch = () => {
    clearFilters();
  };

  const handlePropertyClick = (propertyId) => {
    const property = displayProperties.find((p) => p.id === propertyId);
    if (property) {
      openDetailModal(property);
    }
  };

  const handleFavoriteToggle = async (propertyId) => {
    if (!canUseFavorites) {
      return;
    }

    setPendingFavoriteIds((prev) => new Set(prev).add(propertyId));
    try {
      await toggleFavorite(propertyId);
    } finally {
      setPendingFavoriteIds((prev) => {
        const next = new Set(prev);
        next.delete(propertyId);
        return next;
      });
    }
  };

  const displayProperties = hasSearched ? searchResults : properties;

  return (
    <div className="bg-gray-50 py-12">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Header Section */}
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-gray-900 mb-2">All Properties</h1>
          <p className="text-gray-600 text-lg">
            Handpicked properties curated for discerning clients
          </p>
        </div>

        {/* Search Section */}
        <div className="mb-8 bg-white rounded-lg shadow p-6">
          <div className="flex gap-4">
            <input
              type="text"
              placeholder="Search by location..."
              className="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              onKeyPress={(e) => {
                if (e.key === 'Enter') {
                  handleSearch(e.target.value);
                }
              }}
            />
            <button
              onClick={() => {
                const input = document.querySelector('input[placeholder="Search by location..."]');
                handleSearch(input?.value || '');
              }}
              className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition"
            >
              Search
            </button>
            {hasSearched && (
              <button
                onClick={handleClearSearch}
                className="px-6 py-2 bg-gray-300 text-gray-800 rounded-lg hover:bg-gray-400 transition"
              >
                Clear
              </button>
            )}
          </div>
        </div>

        {/* Properties Grid */}
        {loading ? (
          <div className="text-center py-12">
            <p className="text-gray-500 text-lg">Loading properties...</p>
          </div>
        ) : hasSearched ? (
          <div>
            <h2 className="text-2xl font-bold mb-6 text-gray-900">Search Results</h2>
            {displayProperties && displayProperties.length > 0 ? (
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {displayProperties.map((property) => (
                  <PropertyCard
                    key={property.id}
                    property={property}
                    onClick={handlePropertyClick}
                    showFavoriteButton={canUseFavorites}
                    isFavorited={canUseFavorites ? favoriteIds.has(property.id) : false}
                    isLoadingFavorite={favoritesLoading || pendingFavoriteIds.has(property.id)}
                    onFavoriteToggle={() => handleFavoriteToggle(property.id)}
                  />
                ))}
              </div>
            ) : (
              <div className="text-center py-12">
                <p className="text-gray-500 text-lg">
                  No properties found matching your search.
                </p>
              </div>
            )}
          </div>
        ) : (
          <div>
            <h2 className="text-2xl font-bold mb-6 text-gray-900">Featured Properties</h2>
            {displayProperties && displayProperties.length > 0 ? (
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {displayProperties.map((property) => (
                  <PropertyCard
                    key={property.id}
                    property={property}
                    onClick={handlePropertyClick}
                    showFavoriteButton={canUseFavorites}
                    isFavorited={canUseFavorites ? favoriteIds.has(property.id) : false}
                    isLoadingFavorite={favoritesLoading || pendingFavoriteIds.has(property.id)}
                    onFavoriteToggle={() => handleFavoriteToggle(property.id)}
                  />
                ))}
              </div>
            ) : (
              <div className="text-center py-12">
                <p className="text-gray-500 text-lg">No properties available at the moment.</p>
              </div>
            )}
          </div>
        )}
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
