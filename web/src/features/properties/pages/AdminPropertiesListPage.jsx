import React, { useState, useEffect } from 'react';
import { FiPlus } from 'react-icons/fi';
import { useProperties, usePropertySearch, usePropertyDetailAccess } from '../hooks';
import { PropertyCard, PropertySearchFilter, PropertyDetailModal } from '../components';
import AdminPropertyCreateModal from '../components/admin/AdminPropertyCreateModal';
import * as propertyApi from '../api/propertyApi';
import { AdminSidebar } from '../../../shared/components/layout';

/**
 * AdminPropertiesListPage Component
 * Refactored to use vertical slicing architecture
 * Displays all properties with management capabilities
 */
export default function AdminPropertiesListPage() {
  const { properties, loading, error, fetchProperties, setProperties, setError } = useProperties();
  const { results: searchResults, search, updateFilters, clearFilters, hasSearched } = usePropertySearch();
  const {
    isDetailModalOpen,
    selectedProperty,
    openDetailModal,
    closeDetailModal,
  } = usePropertyDetailAccess();
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [createSuccess, setCreateSuccess] = useState('');
  const [isSearching, setIsSearching] = useState(false);
  const [openDetailsInEditMode, setOpenDetailsInEditMode] = useState(false);

  // Load properties on component mount
  useEffect(() => {
    fetchProperties();
  }, [fetchProperties]);

  const handleSearch = async (searchTerm, searchType) => {
    setIsSearching(true);
    try {
      if (!searchTerm.trim()) {
        clearFilters();
        return;
      }

      const filterObj = {};
      if (searchType === 'name') {
        filterObj.name = searchTerm;
      } else if (searchType === 'location') {
        filterObj.location = searchTerm;
      } else if (searchType === 'developer') {
        filterObj.developer = searchTerm;
      }

      updateFilters(filterObj);
      await search();
    } catch (err) {
      console.error('Search error:', err);
      setError('Search failed');
    } finally {
      setIsSearching(false);
    }
  };

  const handlePriceRangeChange = async (minPrice, maxPrice) => {
    updateFilters({ minPrice, maxPrice });
    await search();
  };

  const handleClearFilters = () => {
    clearFilters();
  };

  const handlePropertyClick = (propertyId) => {
    const property = displayProperties.find((p) => p.id === propertyId);
    if (property) {
      setOpenDetailsInEditMode(false);
      openDetailModal(property);
    }
  };

  const handleEditProperty = (propertyId) => {
    const property = displayProperties.find((p) => p.id === propertyId);
    if (property) {
      setOpenDetailsInEditMode(true);
      openDetailModal(property);
    }
  };

  const handleDeleteProperty = async (propertyId) => {
    if (!window.confirm('Delete this property? This action cannot be undone.')) {
      return;
    }

    try {
      await propertyApi.deleteProperty(propertyId);
      setProperties((prevProperties) => prevProperties.filter((property) => property.id !== propertyId));
      if (hasSearched) {
        clearFilters();
      }
    } catch (err) {
      setError('Failed to delete property');
      console.error('Delete error:', err);
    }
  };

  const handleCreateProperty = async (formData) => {
    try {
      const newProperty = await propertyApi.createProperty(formData);
      setProperties([...properties, newProperty]);
      setIsCreateModalOpen(false);
      setCreateSuccess('Property created successfully!');
      setTimeout(() => setCreateSuccess(''), 3000);
      // Refresh properties list
      fetchProperties();
    } catch (err) {
      setError('Failed to create property. Please try again.');
      console.error('Create error:', err);
    }
  };

  const handleDetailUpdated = (updatedProperty) => {
    setProperties((prevProperties) =>
      prevProperties.map((property) =>
        property.id === updatedProperty.id ? { ...property, ...updatedProperty } : property
      )
    );
  };

  const handleDetailDeleted = (propertyId) => {
    setProperties((prevProperties) => prevProperties.filter((property) => property.id !== propertyId));
    if (hasSearched) {
      clearFilters();
    }
    setOpenDetailsInEditMode(false);
    closeDetailModal();
  };

  const displayProperties = hasSearched ? searchResults : properties;

  return (
    <div className="flex h-full">
      {/* Sidebar */}
      <AdminSidebar />
      
      {/* Main Content */}
      <div className="flex-1 ml-64 bg-gray-50 py-12">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          {/* Header Section */}
          <div className="mb-8 flex justify-between items-start">
            <div>
              <h1 className="text-4xl font-bold text-gray-900 mb-2">Manage Properties</h1>
              <p className="text-gray-600 text-lg">View, edit, and delete all property listings</p>
            </div>
            <button
              onClick={() => setIsCreateModalOpen(true)}
              className="flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-blue-600 to-blue-700 text-white rounded-lg font-bold hover:from-blue-700 hover:to-blue-800 transition-all duration-200 shadow-lg whitespace-nowrap"
            >
              <FiPlus size={24} />
              New Property
            </button>
          </div>

          {/* Error Alert */}
          {error && (
            <div className="mb-6 p-4 bg-red-100 border border-red-400 text-red-700 rounded-lg">
              {error}
            </div>
          )}

          {/* Success Alert */}
          {createSuccess && (
            <div className="mb-6 p-4 bg-green-100 border border-green-400 text-green-700 rounded-lg">
              {createSuccess}
            </div>
          )}

          {/* Search Section */}
          <div className="mb-8">
            <PropertySearchFilter
              searchTypes={[
                { value: 'name', label: 'Property Name' },
                { value: 'location', label: 'Location' },
                { value: 'developer', label: 'Developer' },
              ]}
              onSearch={handleSearch}
              onPriceRangeChange={handlePriceRangeChange}
              onClearFilters={handleClearFilters}
              isLoading={isSearching || loading}
            />
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
                  onEdit={handleEditProperty}
                  onDelete={handleDeleteProperty}
                />
              ))}
            </div>
          ) : (
            <div className="text-center py-12">
              <p className="text-gray-500 text-lg">
                {hasSearched ? 'No properties found matching your search.' : 'No properties available.'}
              </p>
            </div>
          )}
        </div>
      </div>

      {/* Create Property Modal */}
      <AdminPropertyCreateModal
        isOpen={isCreateModalOpen}
        onClose={() => setIsCreateModalOpen(false)}
        onSubmit={handleCreateProperty}
      />

      {/* Property Detail Modal */}
      <PropertyDetailModal
        property={selectedProperty}
        isOpen={isDetailModalOpen}
        onClose={() => {
          setOpenDetailsInEditMode(false);
          closeDetailModal();
        }}
        onPropertyUpdated={handleDetailUpdated}
        onPropertyDeleted={handleDetailDeleted}
        initialEdit={openDetailsInEditMode}
      />
    </div>
  );
}
