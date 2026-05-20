import { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { useAuthContext } from '../../../shared/context/useAuthContext';
import { useFavorites } from '../../favorites/hooks/useFavorites';
import { useProperties, usePropertySearch, usePropertyDetailAccess } from '../hooks';
import { PropertyCard, PropertyDetailModal, PropertySearchFilter } from '../components';

/**
 * PropertyListPage Component
 * Displays list of public properties with search/filter capabilities
 * Accessible to all users (public and authenticated)
 */
export default function PropertyListPage() {
  const [searchParams] = useSearchParams();
  const { authReady, isLoggedIn, isRegisteredUser } = useAuthContext();
  const { favoriteIds, loading: favoritesLoading, fetchFavorites, toggleFavorite } = useFavorites();
  const [pendingFavoriteIds, setPendingFavoriteIds] = useState(new Set());
  const { properties, loading, fetchProperties } = useProperties();
  const {
    results: searchResults,
    filters,
    search,
    updateFilters,
    clearFilters,
    hasSearched,
  } = usePropertySearch();
  const {
    isDetailModalOpen,
    selectedProperty,
    openDetailModal,
    closeDetailModal,
  } = usePropertyDetailAccess();

  const canUseFavorites = isLoggedIn && isRegisteredUser();

  // Load properties on component mount
  useEffect(() => {
    if (!authReady) {
      return;
    }

    const searchTerm = searchParams.get('searchTerm') || searchParams.get('location') || '';
    const searchType = searchParams.get('searchType') || (searchTerm ? 'location' : '');

    if (searchTerm && searchType) {
      const nextFilters = {
        name: searchType === 'name' ? searchTerm : '',
        location: searchType === 'location' ? searchTerm : '',
        developer: searchType === 'developer' ? searchTerm : '',
      };
      updateFilters(nextFilters);
      search(nextFilters);
    } else {
      fetchProperties();
    }
  }, [authReady, fetchProperties, search, searchParams, updateFilters]);

  useEffect(() => {
    if (!canUseFavorites) {
      setPendingFavoriteIds(new Set());
      return;
    }

    fetchFavorites();
  }, [canUseFavorites, fetchFavorites]);

  const handleSearch = async (searchTerm, searchType) => {
    if (!searchTerm.trim()) {
      clearFilters();
      return;
    }

    const nextFilters = {
      ...filters,
      name: searchType === 'name' ? searchTerm : '',
      location: searchType === 'location' ? searchTerm : '',
      developer: searchType === 'developer' ? searchTerm : '',
    };
    updateFilters(nextFilters);
    await search(nextFilters);
  };

  const handleClearSearch = () => {
    clearFilters();
  };

  const handleSortChange = async (sortMode) => {
    const nextFilters = { ...filters, sortMode };
    updateFilters(nextFilters);
    await search(nextFilters);
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
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-gray-900 mb-2">All Properties</h1>
          <p className="text-gray-600 text-lg">
            Handpicked properties curated for discerning clients
          </p>
        </div>

        {/* Search Section */}
        <div className="mb-8">
          <PropertySearchFilter
            onSearch={handleSearch}
            onSortChange={handleSortChange}
            onClearFilters={handleClearSearch}
            sortMode={filters.sortMode}
            isLoading={loading}
          />
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
