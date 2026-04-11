import React, { useState, useEffect } from 'react';
import PropertyCard from '../../components/common/PropertyCard';
import PropertyDetailsModal from '../../components/common/PropertyDetailsModal';
import { property } from '../../api/property';

export default function FavoritePage() {
  const [favorites, setFavorites] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedPropertyId, setSelectedPropertyId] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);

  useEffect(() => {
    fetchFavorites();
  }, []);

  const fetchFavorites = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await property.getAllFavorites();
      setFavorites(data);
    } catch (err) {
      console.error('Error fetching favorites:', err);
      setError('Failed to load your favorites. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handlePropertyClick = (propertyId) => {
    setSelectedPropertyId(propertyId);
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setSelectedPropertyId(null);
    // Refresh favorites to update favorite status
    fetchFavorites();
  };

  return (
    <div className="bg-gray-50 py-12">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Header Section */}
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-gray-900 mb-2">My Favorites</h1>
          <p className="text-gray-600 text-lg">
            {favorites.length} {favorites.length === 1 ? 'property' : 'properties'} saved to your favorites
          </p>
        </div>

        {/* Content */}
        {loading ? (
          <div className="text-center py-12">
            <p className="text-gray-500 text-lg">Loading your favorites...</p>
          </div>
        ) : error ? (
          <div className="text-center py-12">
            <p className="text-red-500 text-lg mb-4">{error}</p>
            <button
              onClick={fetchFavorites}
              className="text-white px-6 py-2 rounded-lg font-semibold hover:opacity-90 transition"
              style={{ backgroundColor: '#007EB7' }}
            >
              Try Again
            </button>
          </div>
        ) : favorites.length === 0 ? (
          <div className="text-center py-16">
            <div className="mb-4">
              <svg
                className="w-16 h-16 mx-auto text-gray-300"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth="1.5"
                  d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"
                ></path>
              </svg>
            </div>
            <p className="text-gray-500 text-lg mb-6">You haven't added any favorites yet.</p>
            <a
              href="/properties"
              className="inline-block text-white px-8 py-3 rounded-lg font-semibold hover:opacity-90 transition"
              style={{ backgroundColor: '#007EB7' }}
            >
              Browse Properties
            </a>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {favorites.map((fav) => (
              <PropertyCard
                key={fav.propertyId}
                property={{
                  id: fav.propertyId,
                  propertyName: fav.propertyName,
                  description: fav.description,
                  location: fav.location,
                  priceRangeMin: fav.priceRangeMin,
                  priceRangeMax: fav.priceRangeMax,
                }}
                onClick={handlePropertyClick}
              />
            ))}
          </div>
        )}
      </div>

      {/* Property Details Modal */}
      <PropertyDetailsModal
        isOpen={isModalOpen}
        onClose={handleCloseModal}
        propertyId={selectedPropertyId}
      />
    </div>
  );
}
