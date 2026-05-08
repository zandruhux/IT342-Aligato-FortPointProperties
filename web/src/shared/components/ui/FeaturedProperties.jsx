import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import PropertyCard from '../../../features/properties/components/PropertyCard';
import { PropertyDetailModal } from '../../../features/properties/components';
import * as propertyApi from '../../../features/properties/api/propertyApi';
import { useAuthContext } from '../../../shared/context/AuthContext';
import { useFavorites } from '../../../features/favorites/hooks/useFavorites';

const FeaturedProperties = ({ limit = 4, showViewAll = true, title = 'Featured Properties'}) => {
  const [properties, setProperties] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedProperty, setSelectedProperty] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const navigate = useNavigate();
  const { isLoggedIn, isRegisteredUser } = useAuthContext();
  const { favoriteIds, loading: favoritesLoading, fetchFavorites, toggleFavorite } = useFavorites();
  const [pendingFavoriteIds, setPendingFavoriteIds] = useState(new Set());

  const canUseFavorites = isLoggedIn && isRegisteredUser();

  useEffect(() => {
    const fetchProperties = async () => {
      try {
        const data = await propertyApi.getPublicProperties();
        setProperties(limit ? data.slice(0, limit) : data);
      } catch (error) {
        console.error("Failed to fetch properties:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchProperties();
  }, [limit]);

  useEffect(() => {
    if (!canUseFavorites) return;
    fetchFavorites();
  }, [canUseFavorites, fetchFavorites]);

  const handleCardClick = (id) => {
    const token = localStorage.getItem('accessToken');
    if (!token) {
      alert("Please log in to view full property details.");
      navigate('/login');
      return;
    }
    
    const property = properties.find((item) => item.id === id);
    setSelectedProperty(property || { id });
    setIsModalOpen(true);
  };

  const closeModal = () => {
    setIsModalOpen(false);
    setSelectedProperty(null);
  };

  if (loading) {
    return (
      <section className="py-12 bg-white">
        <div className="max-w-7xl mx-auto px-4">
          <p className="text-center text-slate-600">Loading properties...</p>
        </div>
      </section>
    );
  }

  return (
    <section className="py-12 bg-white">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Section Header */}
        <div className="flex justify-between items-center mb-8">
          <div>
            <h2 className="text-3xl font-bold text-slate-900">{title}</h2>
            <p className="text-slate-600 mt-2">Handpicked properties curated for discerning buyers</p>
          </div>
          {showViewAll && (
            <button
              onClick={() => navigate('/properties')}
              className="px-6 py-2 rounded-lg font-semibold hover:opacity-90 transition"
              style={{ backgroundColor: '#007EB7', color: 'white' }}
            >
              View All
            </button>
          )}
        </div>

        {/* Properties Grid */}
        {properties.length > 0 ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            {properties.map(property => (
              <div
                key={property.id}
                onClick={() => handleCardClick(property.id)}
                className="cursor-pointer"
              >
                <PropertyCard
                  property={property}
                  onClick={() => handleCardClick(property.id)}
                  showFavoriteButton={canUseFavorites}
                  isFavorited={canUseFavorites ? favoriteIds.has(property.id) : false}
                  isLoadingFavorite={favoritesLoading || pendingFavoriteIds.has(property.id)}
                  onFavoriteToggle={async () => {
                    if (!canUseFavorites) return;
                    setPendingFavoriteIds((prev) => new Set(prev).add(property.id));
                    try {
                      await toggleFavorite(property.id);
                    } finally {
                      setPendingFavoriteIds((prev) => {
                        const next = new Set(prev);
                        next.delete(property.id);
                        return next;
                      });
                    }
                  }}
                />
              </div>
            ))}
          </div>
        ) : (
          <p className="text-center text-slate-600">No properties available</p>
        )}
      </div>
      <PropertyDetailModal
        property={selectedProperty}
        isOpen={isModalOpen}
        onClose={closeModal}
      />
    </section>
  );
};

export default FeaturedProperties;
