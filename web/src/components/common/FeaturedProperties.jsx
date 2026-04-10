import React, { useState, useEffect } from 'react';
import PropertyCard from './PropertyCard';
import { property } from '../../api/property';
import { useNavigate } from 'react-router-dom';
import PropertyDetailsModal from './PropertyDetailsModal';

const FeaturedProperties = ({ limit = 4, showViewAll = true, title = 'Featured Properties'}) => {
  const [properties, setProperties] = useState([]);
  const [loading, setLoading] = useState(true);

  //Modal State
  const [selectedProperty, setSelectedProperty] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const navigate = useNavigate();


  useEffect(() => {
    const fetchProperties = async () => {
      try {
        const data = await property.getAllProperties();
        setProperties(limit ? data.slice(0, limit) : data);
      } catch (error) {
        console.error("Failed to fetch properties:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchProperties();
  }, []);

  const handleCardClick = (id) => {
    const token = localStorage.getItem('accessToken');
    if (!token) {
      // If public user clicks, either redirect to login or show an alert
      alert("Please log in to view full property details.");
      navigate('/login');
      return;
    }
    
    setSelectedProperty(id);
    setIsModalOpen(true);
  };

  const closeModal = () => {
    setIsModalOpen(false);
    setSelectedProperty(null);
  };

  if (loading) {
    return <div className="py-16 text-center text-gray-500">Loading featured properties...</div>;
  }

  return (
    <section className="py-12 bg-white">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex flex-col sm:flex-row sm:justify-between sm:items-end mb-8 gap-4">
          <div>
            <h2 className="text-3xl font-bold text-gray-900">{title}</h2>
            <p className="text-gray-500 mt-2">Handpicked selections for discerning buyers</p>
          </div>
          {showViewAll &&(
            <a href="/properties" className="text-blue-600 hover:text-blue-800 font-medium text-sm flex items-center transition-colors">
              View All Properties
              <span className="ml-1 text-lg leading-none">›</span>
            </a>
          )}
          
        </div>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
          {properties.map((property, index) => (
            <PropertyCard 
                key={index} 
                property={property} 
                onClick={handleCardClick}
                />
          ))}
        </div>
      </div>
      <PropertyDetailsModal
        isOpen={isModalOpen}
        onClose={closeModal}
        propertyId={selectedProperty}
      />
    </section>
  );
};

export default FeaturedProperties;