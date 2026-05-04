import React, { useState, useEffect } from 'react';
import PropertySearchFilter from '../Properties/PropertySearchFilter';
import AgentPropertyCard from '../Properties/AgentPropertyCard';
import AgentPropertyDetailsModal from '../Properties/AgentPropertyDetailsModal';
import { property } from '../../../../api/property';

const AgentPropertiesSection = () => {
  const [properties, setProperties] = useState([]);
  const [displayedProperties, setDisplayedProperties] = useState([]);
  const [loading, setLoading] = useState(false);
  const [hasSearched, setHasSearched] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');

  // Modal State
  const [selectedPropertyId, setSelectedPropertyId] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);

  // Load all properties on mount
  useEffect(() => {
    fetchAllProperties();
  }, []);

  const fetchAllProperties = async () => {
    setLoading(true);
    try {
      const data = await property.getAllAgentProperties();
      setProperties(data);
      setDisplayedProperties(data);
      setHasSearched(false);
      setSearchQuery('');
    } catch (error) {
      console.error('Error fetching properties:', error);
      alert('Failed to load properties');
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async (searchType, searchValue) => {
    setLoading(true);
    try {
      let results = [];
      
      if (searchType === 'name') {
        results = await property.searchAgentByName(searchValue);
      } else if (searchType === 'developer') {
        results = await property.searchAgentByDeveloper(searchValue);
      }

      setDisplayedProperties(results);
      setHasSearched(true);
      setSearchQuery(`${searchType}: ${searchValue}`);
    } catch (error) {
      console.error('Error during search:', error);
      alert('Search failed. Please try again.');
      setDisplayedProperties([]);
    } finally {
      setLoading(false);
    }
  };

  const handleFilter = async (filterType, filterValue) => {
    setLoading(true);
    try {
      let results = [];
      
      if (filterType === 'listing-type') {
        // Filter properties where listingType (comma-separated) includes the filterValue
        results = properties.filter(p => 
          p.listingType && p.listingType.includes(filterValue)
        );
      }

      setDisplayedProperties(results);
      setHasSearched(true);
      setSearchQuery(`${filterType}: ${filterValue}`);
    } catch (error) {
      console.error('Error during filter:', error);
      alert('Filter failed. Please try again.');
      setDisplayedProperties([]);
    } finally {
      setLoading(false);
    }
  };

  const handleClearFilters = () => {
    fetchAllProperties();
  };

  const handleViewDetails = (propertyId) => {
    setSelectedPropertyId(propertyId);
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setSelectedPropertyId(null);
  };

  return (
    <div className="w-full">
      {/* Search and Filter Section */}
      <PropertySearchFilter
        onSearch={handleSearch}
        onFilter={handleFilter}
        onClearFilters={handleClearFilters}
        isLoading={loading}
      />

      {/* Results Section */}
      <div className="bg-white rounded-lg">
        {hasSearched && (
          <div className="mb-8 p-6 bg-gradient-to-r from-blue-50 to-blue-100 rounded-lg border-l-4 border-blue-600">
            <p className="text-gray-800 font-bold text-lg">
              <span className="text-blue-600">Search Results:</span> {searchQuery}
            </p>
            <p className="text-sm text-gray-700 mt-2 font-medium">
              Found <span className="font-bold text-blue-600">{displayedProperties.length}</span> property(ies)
            </p>
          </div>
        )}

        {/* Properties List */}
        {displayedProperties && displayedProperties.length > 0 ? (
          <div className="space-y-2">
            {displayedProperties.map((prop) => (
              <AgentPropertyCard
                key={prop.id}
                property={prop}
                onViewDetails={handleViewDetails}
              />
            ))}
          </div>
        ) : (
          <div className="text-center py-16 bg-gray-50 rounded-lg">
            <p className="text-gray-600 text-lg font-medium">
              {hasSearched
                ? 'No properties found matching your search.'
                : 'No properties available at the moment.'}
            </p>
          </div>
        )}
      </div>

      {/* Property Details Modal */}
      <AgentPropertyDetailsModal
        isOpen={isModalOpen}
        onClose={handleCloseModal}
        propertyId={selectedPropertyId}
      />
    </div>
  );
};

export default AgentPropertiesSection;
