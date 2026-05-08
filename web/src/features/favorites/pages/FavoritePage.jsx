import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuthContext } from '../../../shared/context/useAuthContext';
import { useFavorites } from '../hooks/useFavorites';
import { PropertyCard } from '../../properties/components';

/**
 * FavoritePage Component
 * Displays all properties added to favorites by the current user
 */
export default function FavoritePage() {
  const navigate = useNavigate();
  const { isLoggedIn } = useAuthContext();
  const { favorites, loading, error, fetchFavorites, removeFavorite } = useFavorites();

  // Redirect to login if not authenticated
  useEffect(() => {
    if (!isLoggedIn) {
      navigate('/login');
      return;
    }
    fetchFavorites();
  }, [isLoggedIn, navigate, fetchFavorites]);

  if (!isLoggedIn) {
    return null;
  }

  //Mapper Function
  const mapFavoriteToProperty = (fav) => ({
    id: fav.propertyId, // This maps the correct property ID for the card
    priceRangeMin: fav.priceRangeMin,
    priceRangeMax: fav.priceRangeMax,
    name: fav.propertyName,
    location: fav.location,
    basicDescription: fav.description,
    isFavorite: true // Optional: Force the heart to be filled on this page
  });

  const handlePropertyClick = (propertyId) => {
    console.log('Viewing property:', propertyId);
    // Can implement navigation to property details
  };

  const handleRemoveFavorite = async (propertyId) => {
    const success = await removeFavorite(propertyId);
    if (success) {
      console.log(`Removed property ${propertyId} from favorites`);
    }
  };

  return (
    <div className="bg-gray-50 py-12">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Header Section */}
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-gray-900 mb-2">My Favorites</h1>
          <p className="text-gray-600 text-lg">
            Properties you've saved for later reference
          </p>
        </div>

        {/* Error Alert */}
        {error && (
          <div className="mb-6 p-4 bg-red-100 border border-red-400 text-red-700 rounded-lg">
            {error}
          </div>
        )}

        {/* Favorites Grid */}
        {loading ? (
          <div className="text-center py-12">
            <p className="text-gray-500 text-lg">Loading your favorites...</p>
          </div>
        ) : favorites && favorites.length > 0 ? (
          <div>
            <p className="text-gray-600 mb-6">
              You have <span className="font-semibold">{favorites.length}</span> favorite
              {favorites.length !== 1 ? ' properties' : ' property'}
            </p>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {favorites.map((property) => (
                <div key={property.id} className="relative">
                  <PropertyCard
                      property={mapFavoriteToProperty(property)}
                      onClick={handlePropertyClick}
                      showFavoriteButton
                      isFavorited
                      onFavoriteToggle={() => handleRemoveFavorite(property.propertyId)}
                  />
                </div>
              ))}
            </div>
          </div>
        ) : (
          <div className="text-center py-12">
            <svg
              className="w-16 h-16 mx-auto mb-4 text-gray-400"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth="2"
                d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"
              ></path>
            </svg>
            <p className="text-gray-500 text-lg mb-4">No favorites yet</p>
            <p className="text-gray-400 mb-6">
              Add properties to your favorites to save them for later
            </p>
            <button
              onClick={() => navigate('/properties')}
              className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition"
            >
              Browse Properties
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
