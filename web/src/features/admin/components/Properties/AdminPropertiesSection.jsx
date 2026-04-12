import { useState, useEffect } from 'react';
import { property } from '../../../../api/property';
import AdminPropertyCard from './AdminPropertyCard';
import PropertySearchFilter from './PropertySearchFilter';
import AdminPropertyCreateModal from './AdminPropertyCreateModal';
import AdminPropertyDetailsModal from './AdminPropertyDetailsModal';
import { FiPlus, FiAlertCircle } from 'react-icons/fi';

export default function AdminPropertiesSection() {
  const [properties, setProperties] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  // Modal states
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [isDetailsModalOpen, setIsDetailsModalOpen] = useState(false);
  const [detailsMode, setDetailsMode] = useState('view'); // 'view' or 'edit'
  const [selectedProperty, setSelectedProperty] = useState(null);

  // Fetch all properties on mount
  useEffect(() => {
    fetchAllProperties();
  }, []);

  const fetchAllProperties = async () => {
    setIsLoading(true);
    setError('');
    try {
      const data = await property.getAllAdminProperties();
      setProperties(Array.isArray(data) ? data : []);
    } catch (err) {
      setError('Failed to fetch properties. Please try again.');
      console.error('Error fetching properties:', err);
    } finally {
      setIsLoading(false);
    }
  };

  const handleCreateProperty = async (formData) => {
    setIsLoading(true);
    setError('');
    try {
      const newProperty = await property.createProperty(formData);
      setProperties([...properties, newProperty]);
      setIsCreateModalOpen(false);
      setSuccess('Property created successfully!');
      setTimeout(() => setSuccess(''), 3000);
    } catch (err) {
      setError('Failed to create property. Please try again.');
      console.error('Error creating property:', err);
    } finally {
      setIsLoading(false);
    }
  };

  const handleViewProperty = async (propertyId) => {
    setIsLoading(true);
    setError('');
    try {
      const data = await property.getAdminPropertyById(propertyId);
      setSelectedProperty(data);
      setDetailsMode('view');
      setIsDetailsModalOpen(true);
    } catch (err) {
      setError('Failed to load property details. Please try again.');
      console.error('Error loading property:', err);
    } finally {
      setIsLoading(false);
    }
  };

  const handleEditProperty = async (propertyId) => {
    setIsLoading(true);
    setError('');
    try {
      const data = await property.getAdminPropertyById(propertyId);
      setSelectedProperty(data);
      setDetailsMode('edit');
      setIsDetailsModalOpen(true);
    } catch (err) {
      setError('Failed to load property details. Please try again.');
      console.error('Error loading property:', err);
    } finally {
      setIsLoading(false);
    }
  };

  const handleUpdateProperty = async (updatedData) => {
    setIsLoading(true);
    setError('');
    try {
      const updated = await property.updateProperty(selectedProperty.id, updatedData);
      setProperties(properties.map(p => p.id === selectedProperty.id ? updated : p));
      setIsDetailsModalOpen(false);
      setSelectedProperty(null);
      setSuccess('Property updated successfully!');
      setTimeout(() => setSuccess(''), 3000);
    } catch (err) {
      setError('Failed to update property. Please try again.');
      console.error('Error updating property:', err);
    } finally {
      setIsLoading(false);
    }
  };

  const handleDeleteProperty = async (propertyId) => {
    if (!window.confirm('Are you sure you want to delete this property? This action cannot be undone.')) {
      return;
    }

    setIsLoading(true);
    setError('');
    try {
      await property.deleteProperty(propertyId);
      setProperties(properties.filter(p => p.id !== propertyId));
      setSuccess('Property deleted successfully!');
      setTimeout(() => setSuccess(''), 3000);
    } catch (err) {
      setError('Failed to delete property. Please try again.');
      console.error('Error deleting property:', err);
    } finally {
      setIsLoading(false);
    }
  };

  const handleSearch = async (searchTerm, searchBy) => {
    setIsLoading(true);
    setError('');
    try {
      let results = [];
      if (searchBy === 'name') {
        results = await property.searchPropertyByName(searchTerm);
      } else if (searchBy === 'developer') {
        results = await property.searchPropertyByDeveloper(searchTerm);
      } else if (searchBy === 'location') {
        results = await property.searchPropertyByLocation(searchTerm);
      }
      setProperties(Array.isArray(results) ? results : []);
    } catch (err) {
      setError('Search failed. Please try again.');
      console.error('Error searching properties:', err);
    } finally {
      setIsLoading(false);
    }
  };

  const handleFilter = (listingType) => {
    const filtered = properties.filter(p => p.listingType === listingType);
    setProperties(filtered);
  };

  const handleClearFilters = () => {
    fetchAllProperties();
  };

  return (
    <div className="space-y-8">
      {/* Success Message */}
      {success && (
        <div className="bg-green-50 border-l-4 border-green-600 p-4 rounded flex items-center gap-3">
          <div className="w-2 h-2 bg-green-600 rounded-full"></div>
          <p className="text-green-800 font-bold">{success}</p>
        </div>
      )}

      {/* Error Message */}
      {error && (
        <div className="bg-red-50 border-l-4 border-red-600 p-4 rounded flex items-center gap-3">
          <FiAlertCircle size={20} className="text-red-600 flex-shrink-0" />
          <p className="text-red-800 font-bold">{error}</p>
        </div>
      )}

      {/* Create Button & Title */}
      <div className="flex justify-between items-center">
        <div>
          <h2 className="text-3xl font-bold text-slate-900">Properties Management</h2>
          <p className="text-slate-600 mt-1">Manage and upload property listings - Total: {properties.length}</p>
        </div>
        <button
          onClick={() => setIsCreateModalOpen(true)}
          className="flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-blue-600 to-blue-700 text-white rounded-lg font-bold hover:from-blue-700 hover:to-blue-800 transition-all duration-200 shadow-lg"
        >
          <FiPlus size={24} />
          New Property
        </button>
      </div>

      {/* Search & Filter */}
      <PropertySearchFilter
        onSearch={handleSearch}
        onFilter={handleFilter}
        onClearFilters={handleClearFilters}
        isLoading={isLoading}
      />

      {/* Properties List */}
      {isLoading && properties.length === 0 ? (
        <div className="text-center py-12">
          <div className="inline-block">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
            <p className="text-slate-600 mt-4 font-medium">Loading properties...</p>
          </div>
        </div>
      ) : properties.length === 0 ? (
        <div className="bg-slate-50 rounded-lg border-2 border-dashed border-slate-300 p-12 text-center">
          <p className="text-slate-600 font-medium text-lg">No properties found</p>
          <p className="text-slate-500 mt-2">Create a new property to get started</p>
        </div>
      ) : (
        <div className="space-y-4">
          {properties.map(prop => (
            <AdminPropertyCard
              key={prop.id}
              property={prop}
              onView={handleViewProperty}
              onEdit={handleEditProperty}
              onDelete={handleDeleteProperty}
            />
          ))}
        </div>
      )}

      {/* Modals */}
      <AdminPropertyCreateModal
        isOpen={isCreateModalOpen}
        onClose={() => setIsCreateModalOpen(false)}
        onSubmit={handleCreateProperty}
        isLoading={isLoading}
      />

      <AdminPropertyDetailsModal
        isOpen={isDetailsModalOpen}
        onClose={() => {
          setIsDetailsModalOpen(false);
          setSelectedProperty(null);
        }}
        property={selectedProperty}
        onUpdate={handleUpdateProperty}
        isLoading={isLoading}
        mode={detailsMode}
      />
    </div>
  );
}
