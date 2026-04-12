import { useState, useEffect } from 'react';
import { FiX } from 'react-icons/fi';

export default function AdminPropertyDetailsModal({ 
  isOpen, 
  onClose, 
  property,
  onUpdate,
  isLoading,
  mode = 'view' // 'view' or 'edit'
}) {
  const [isEditMode, setIsEditMode] = useState(mode === 'edit');
  const [formData, setFormData] = useState(property || {});
  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (property) {
      setFormData(property);
    }
  }, [property]);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
    if (errors[name]) {
      setErrors(prev => ({
        ...prev,
        [name]: undefined
      }));
    }
  };

  const validateForm = () => {
    const newErrors = {};
    
    if (!formData.propertyName?.trim()) newErrors.propertyName = 'Property name is required';
    if (!formData.developerName?.trim()) newErrors.developerName = 'Developer name is required';
    if (!formData.priceRangeMin) newErrors.priceRangeMin = 'Minimum price is required';
    if (!formData.priceRangeMax) newErrors.priceRangeMax = 'Maximum price is required';
    if (formData.priceRangeMin && formData.priceRangeMax && parseFloat(formData.priceRangeMin) > parseFloat(formData.priceRangeMax)) {
      newErrors.priceRangeMax = 'Maximum price must be greater than minimum price';
    }
    if (!formData.location?.trim()) newErrors.location = 'Location is required';
    if (!formData.propertyType?.trim()) newErrors.propertyType = 'Property type is required';
    if (!formData.unitType?.trim()) newErrors.unitType = 'Unit type is required';
    if (!formData.turnoverDate?.trim()) newErrors.turnoverDate = 'Turnover date is required';

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSave = async () => {
    if (validateForm()) {
      onUpdate(formData);
    }
  };

  const formatPrice = (price) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 0
    }).format(price);
  };

  if (!isOpen || !property) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-30 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-lg shadow-2xl max-w-4xl w-full max-h-[90vh] overflow-y-auto">
        {/* Header */}
        <div className="sticky top-0 bg-gradient-to-r from-blue-600 to-blue-700 px-10 py-7 flex justify-between items-center shadow-md z-10">
          <div>
            <h2 className="text-3xl font-bold text-white">
              {isEditMode ? 'Edit Property' : 'Property Details'}
            </h2>
            <p className="text-blue-100 text-sm mt-1">
              {isEditMode ? 'Update property information' : formData.propertyName}
            </p>
          </div>
          <button
            onClick={onClose}
            className="text-white hover:bg-blue-800 p-2 rounded-lg transition-colors duration-200"
          >
            <FiX size={28} />
          </button>
        </div>

        {/* Content */}
        <div className="p-10 space-y-10">
          {/* Row 1: Basic Info */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label className="block text-sm font-bold text-slate-900 mb-2">Property Name *</label>
              <input
                type="text"
                name="propertyName"
                value={formData.propertyName || ''}
                onChange={handleChange}
                disabled={!isEditMode}
                className={`w-full px-4 py-3 border-2 rounded-lg font-medium transition-colors duration-200 ${
                  isEditMode
                    ? errors.propertyName
                      ? 'border-red-500 focus:ring-2 focus:ring-red-500 focus:border-transparent'
                      : 'border-slate-300 focus:border-blue-500 focus:ring-2 focus:ring-blue-500 focus:ring-opacity-20'
                    : 'border-slate-200 bg-slate-50 text-slate-900 cursor-not-allowed'
                } outline-none`}
              />
              {errors.propertyName && <p className="text-red-700 text-sm font-bold mt-1">{errors.propertyName}</p>}
            </div>
            <div>
              <label className="block text-sm font-bold text-slate-900 mb-2">Developer Name *</label>
              <input
                type="text"
                name="developerName"
                value={formData.developerName || ''}
                onChange={handleChange}
                disabled={!isEditMode}
                className={`w-full px-4 py-3 border-2 rounded-lg font-medium transition-colors duration-200 ${
                  isEditMode
                    ? errors.developerName
                      ? 'border-red-500 focus:ring-2 focus:ring-red-500 focus:border-transparent'
                      : 'border-slate-300 focus:border-blue-500 focus:ring-2 focus:ring-blue-500 focus:ring-opacity-20'
                    : 'border-slate-200 bg-slate-50 text-slate-700 cursor-not-allowed'
                } outline-none`}
              />
              {errors.developerName && <p className="text-red-700 text-sm font-bold mt-1">{errors.developerName}</p>}
            </div>
          </div>

          {/* Row 2: Description */}
          <div>
            <label className="block text-sm font-bold text-slate-900 mb-2">Description</label>
            <textarea
              name="description"
              value={formData.description || ''}
              onChange={handleChange}
              disabled={!isEditMode}
              className={`w-full px-4 py-3 border-2 rounded-lg font-medium resize-none transition-colors duration-200 ${
                isEditMode
                  ? 'border-slate-300 focus:border-blue-500 focus:ring-2 focus:ring-blue-500 focus:ring-opacity-20'
                  : 'border-slate-200 bg-slate-50 text-slate-700 cursor-not-allowed'
              } outline-none`}
              rows="3"
            />
          </div>

          {/* Row 3: Price Range */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label className="block text-sm font-bold text-slate-900 mb-2">Price Range Min *</label>
              <input
                type="number"
                name="priceRangeMin"
                value={formData.priceRangeMin || ''}
                onChange={handleChange}
                disabled={!isEditMode}
                className={`w-full px-4 py-3 border-2 rounded-lg font-medium transition-colors duration-200 ${
                  isEditMode
                    ? errors.priceRangeMin
                      ? 'border-red-500 focus:ring-2 focus:ring-red-500 focus:border-transparent'
                      : 'border-slate-300 focus:border-blue-500 focus:ring-2 focus:ring-blue-500 focus:ring-opacity-20'
                    : 'border-slate-200 bg-slate-50 text-slate-700 cursor-not-allowed'
                } outline-none`}
                step="0.01"
              />
              {errors.priceRangeMin && <p className="text-red-700 text-sm font-bold mt-1">{errors.priceRangeMin}</p>}
            </div>
            <div>
              <label className="block text-sm font-bold text-slate-900 mb-2">Price Range Max *</label>
              <input
                type="number"
                name="priceRangeMax"
                value={formData.priceRangeMax || ''}
                onChange={handleChange}
                disabled={!isEditMode}
                className={`w-full px-4 py-3 border-2 rounded-lg font-medium transition-colors duration-200 ${
                  isEditMode
                    ? errors.priceRangeMax
                      ? 'border-red-500 focus:ring-2 focus:ring-red-500 focus:border-transparent'
                      : 'border-slate-300 focus:border-blue-500 focus:ring-2 focus:ring-blue-500 focus:ring-opacity-20'
                    : 'border-slate-200 bg-slate-50 text-slate-700 cursor-not-allowed'
                } outline-none`}
                step="0.01"
              />
              {errors.priceRangeMax && <p className="text-red-700 text-sm font-bold mt-1">{errors.priceRangeMax}</p>}
            </div>
          </div>

          {/* Row 4: Location & Location */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label className="block text-sm font-bold text-slate-900 mb-2">Location *</label>
              <input
                type="text"
                name="location"
                value={formData.location || ''}
                onChange={handleChange}
                disabled={!isEditMode}
                className={`w-full px-4 py-3 border-2 rounded-lg font-medium transition-colors duration-200 ${
                  isEditMode
                    ? errors.location
                      ? 'border-red-500 focus:ring-2 focus:ring-red-500 focus:border-transparent'
                      : 'border-slate-300 focus:border-blue-500 focus:ring-2 focus:ring-blue-500 focus:ring-opacity-20'
                    : 'border-slate-200 bg-slate-50 text-slate-700 cursor-not-allowed'
                } outline-none`}
              />
              {errors.location && <p className="text-red-700 text-sm font-bold mt-1">{errors.location}</p>}
            </div>
            <div>
              <label className="block text-sm font-bold text-slate-900 mb-2">Property Type *</label>
              <select
                name="propertyType"
                value={formData.propertyType || ''}
                onChange={handleChange}
                disabled={!isEditMode}
                className={`w-full px-4 py-3 border-2 rounded-lg font-medium transition-colors duration-200 ${
                  isEditMode
                    ? errors.propertyType
                      ? 'border-red-500 focus:ring-2 focus:ring-red-500 focus:border-transparent'
                      : 'border-slate-300 focus:border-blue-500 focus:ring-2 focus:ring-blue-500 focus:ring-opacity-20'
                    : 'border-slate-200 bg-slate-50 text-slate-700 cursor-not-allowed'
                } outline-none`}
              >
                <option value="">Select property type</option>
                <option value="Condo">Condo</option>
                <option value="House">House</option>
                <option value="Townhouse">Townhouse</option>
                <option value="Apartment">Apartment</option>
                <option value="Land">Land</option>
              </select>
              {errors.propertyType && <p className="text-red-700 text-sm font-bold mt-1">{errors.propertyType}</p>}
            </div>
          </div>

          {/* Row 5: Unit Type & Listing Type */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label className="block text-sm font-bold text-slate-900 mb-2">Unit Type *</label>
              <select
                name="unitType"
                value={formData.unitType || ''}
                onChange={handleChange}
                disabled={!isEditMode}
                className={`w-full px-4 py-3 border-2 rounded-lg font-medium transition-colors duration-200 ${
                  isEditMode
                    ? errors.unitType
                      ? 'border-red-500 focus:ring-2 focus:ring-red-500 focus:border-transparent'
                      : 'border-slate-300 focus:border-blue-500 focus:ring-2 focus:ring-blue-500 focus:ring-opacity-20'
                    : 'border-slate-200 bg-slate-50 text-slate-700 cursor-not-allowed'
                } outline-none`}
              >
                <option value="">Select unit type</option>
                <option value="Studio">Studio</option>
                <option value="1-Bedroom">1-Bedroom</option>
                <option value="2-Bedroom">2-Bedroom</option>
                <option value="3-Bedroom">3-Bedroom</option>
                <option value="4-Bedroom">4-Bedroom</option>
                <option value="5-Bedroom">5-Bedroom</option>
              </select>
              {errors.unitType && <p className="text-red-700 text-sm font-bold mt-1">{errors.unitType}</p>}
            </div>
            <div>
              <label className="block text-sm font-bold text-slate-900 mb-2">Listing Type *</label>
              <select
                name="listingType"
                value={formData.listingType || ''}
                onChange={handleChange}
                disabled={!isEditMode}
                className={`w-full px-4 py-3 border-2 rounded-lg font-medium transition-colors duration-200 ${
                  isEditMode
                    ? 'border-slate-300 focus:border-blue-500 focus:ring-2 focus:ring-blue-500 focus:ring-opacity-20'
                    : 'border-slate-200 bg-slate-50 text-slate-700 cursor-not-allowed'
                } outline-none`}
              >
                <option value="Pre-Selling">Pre-Selling</option>
                <option value="Ready-For-Occupancy">Ready-For-Occupancy</option>
              </select>
            </div>
          </div>

          {/* Row 6: Turnover Date */}
          <div>
            <label className="block text-sm font-bold text-slate-900 mb-2">Turnover Date (YYYY-MM) *</label>
            <input
              type="text"
              name="turnoverDate"
              value={formData.turnoverDate || ''}
              onChange={handleChange}
              disabled={!isEditMode}
              placeholder="2025-12"
              className={`w-full px-4 py-3 border-2 rounded-lg font-medium transition-colors duration-200 ${
                isEditMode
                  ? errors.turnoverDate
                    ? 'border-red-500 focus:ring-2 focus:ring-red-500 focus:border-transparent'
                    : 'border-slate-300 focus:border-blue-500 focus:ring-2 focus:ring-blue-500 focus:ring-opacity-20'
                  : 'border-slate-200 bg-slate-50 text-slate-700 cursor-not-allowed'
              } outline-none`}
            />
            {errors.turnoverDate && <p className="text-red-700 text-sm font-bold mt-1">{errors.turnoverDate}</p>}
          </div>

          {/* Row 7: Amenities & Checkboxes */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label className="block text-sm font-bold text-slate-900 mb-2">Amenities</label>
              <textarea
                name="amenities"
                value={formData.amenities || ''}
                onChange={handleChange}
                disabled={!isEditMode}
                className={`w-full px-4 py-3 border-2 rounded-lg font-medium resize-none transition-colors duration-200 ${
                  isEditMode
                    ? 'border-slate-300 focus:border-blue-500 focus:ring-2 focus:ring-blue-500 focus:ring-opacity-20'
                    : 'border-slate-200 bg-slate-50 text-slate-700 cursor-not-allowed'
                } outline-none`}
                rows="3"
              />
            </div>
            <div className="space-y-4">
              <div className="flex items-center">
                <input
                  type="checkbox"
                  id="petFriendly"
                  name="petFriendly"
                  checked={formData.petFriendly || false}
                  onChange={handleChange}
                  disabled={!isEditMode}
                  className={`w-5 h-5 rounded cursor-pointer ${isEditMode ? 'text-blue-600' : 'text-slate-400'}`}
                />
                <label htmlFor="petFriendly" className="ml-3 text-sm font-medium text-slate-700 cursor-pointer">
                  Pet Friendly
                </label>
              </div>
              <div className="flex items-center">
                <input
                  type="checkbox"
                  id="parkingAvailable"
                  name="parkingAvailable"
                  checked={formData.parkingAvailable || false}
                  onChange={handleChange}
                  disabled={!isEditMode}
                  className={`w-5 h-5 rounded cursor-pointer ${isEditMode ? 'text-blue-600' : 'text-slate-400'}`}
                />
                <label htmlFor="parkingAvailable" className="ml-3 text-sm font-medium text-slate-700 cursor-pointer">
                  Parking Available
                </label>
              </div>
            </div>
          </div>

          {/* Row 8: Additional Info */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label className="block text-sm font-bold text-slate-900 mb-2">Price Computations</label>
              <textarea
                name="priceComputations"
                value={formData.priceComputations || ''}
                onChange={handleChange}
                disabled={!isEditMode}
                className={`w-full px-4 py-3 border-2 rounded-lg font-medium resize-none transition-colors duration-200 ${
                  isEditMode
                    ? 'border-slate-300 focus:border-blue-500 focus:ring-2 focus:ring-blue-500 focus:ring-opacity-20'
                    : 'border-slate-200 bg-slate-50 text-slate-700 cursor-not-allowed'
                } outline-none`}
                rows="3"
              />
            </div>
            <div>
              <label className="block text-sm font-bold text-slate-900 mb-2">Developer Links</label>
              <textarea
                name="developerLinks"
                value={formData.developerLinks || ''}
                onChange={handleChange}
                disabled={!isEditMode}
                className={`w-full px-4 py-3 border-2 rounded-lg font-medium resize-none transition-colors duration-200 ${
                  isEditMode
                    ? 'border-slate-300 focus:border-blue-500 focus:ring-2 focus:ring-blue-500 focus:ring-opacity-20'
                    : 'border-slate-200 bg-slate-50 text-slate-700 cursor-not-allowed'
                } outline-none`}
                rows="3"
              />
            </div>
          </div>

          {/* Row 9: Pitch Ready Phrases */}
          <div>
            <label className="block text-sm font-bold text-slate-900 mb-2">Pitch Ready Phrases</label>
            <textarea
              name="pitchReadyPhrases"
              value={formData.pitchReadyPhrases || ''}
              onChange={handleChange}
              disabled={!isEditMode}
              className={`w-full px-4 py-3 border-2 rounded-lg font-medium resize-none transition-colors duration-200 ${
                isEditMode
                  ? 'border-slate-300 focus:border-blue-500 focus:ring-2 focus:ring-blue-500 focus:ring-opacity-20'
                  : 'border-slate-200 bg-slate-50 text-slate-700 cursor-not-allowed'
              } outline-none`}
              rows="3"
            />
          </div>

          {/* Metadata Section (View Mode Only) */}
          {!isEditMode && (
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6 p-6 bg-slate-50 rounded-lg">
              <div>
                <p className="text-xs text-slate-700 uppercase tracking-wide font-bold">Created By</p>
                <p className="text-sm text-slate-900 font-semibold">{formData.createdBy || 'N/A'}</p>
              </div>
              <div>
                <p className="text-xs text-slate-700 uppercase tracking-wide font-bold">Created At</p>
                <p className="text-sm text-slate-900 font-semibold">
                  {formData.createdAt ? new Date(formData.createdAt).toLocaleDateString() : 'N/A'}
                </p>
              </div>
              <div>
                <p className="text-xs text-slate-700 uppercase tracking-wide font-bold">Updated At</p>
                <p className="text-sm text-slate-900 font-semibold">
                  {formData.updatedAt ? new Date(formData.updatedAt).toLocaleDateString() : 'N/A'}
                </p>
              </div>
            </div>
          )}
        </div>

        {/* Footer */}
        <div className="sticky bottom-0 bg-slate-50 px-10 py-6 flex justify-between gap-4 border-t border-slate-200 shadow-md">
          <button
            onClick={() => setIsEditMode(!isEditMode)}
            disabled={isLoading}
            className={`px-6 py-3 border-2 rounded-lg font-semibold transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed ${
              isEditMode
                ? 'border-slate-300 text-slate-900 hover:bg-slate-100'
                : 'border-blue-600 text-blue-600 hover:bg-blue-50'
            }`}
          >
            {isEditMode ? 'Cancel' : 'Edit'}
          </button>
          <div className="flex gap-4">
            <button
              onClick={onClose}
              disabled={isLoading}
              className="px-6 py-3 border-2 border-slate-300 text-slate-900 rounded-lg font-semibold hover:bg-slate-100 transition-colors duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              Close
            </button>
            {isEditMode && (
              <button
                onClick={handleSave}
                disabled={isLoading}
                className="px-6 py-3 bg-gradient-to-r from-blue-600 to-blue-700 text-white rounded-lg font-semibold hover:from-blue-700 hover:to-blue-800 transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed shadow-lg"
              >
                {isLoading ? 'Saving...' : 'Save Changes'}
              </button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
