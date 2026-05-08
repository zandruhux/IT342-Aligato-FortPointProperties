import { useState, useEffect } from 'react';
import { FiX, FiPlus, FiEdit2, FiTrash2 } from 'react-icons/fi';

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

  // Unit management state
  const [unitForm, setUnitForm] = useState({
    unitType: '',
    floorArea: '',
    lotArea: '',
    reservationFee: '',
    equityPeriodMonths: '',
    monthlyEquity: '',
    totalSellingPrice: '',
    financingTypes: []
  });
  const [editingUnitIndex, setEditingUnitIndex] = useState(null);
  const [unitErrors, setUnitErrors] = useState({});
  const financingOptions = ['Cash Only', 'Bank Financing', 'In-House'];

  useEffect(() => {
    if (property) {
      // Parse listingType from comma-separated string to array
      const listingTypeArray = property.listingType 
        ? property.listingType.split(',').map(t => t.trim())
        : [];
      
      setFormData({
        ...property,
        listingType: listingTypeArray,
        units: property.units || []
      });
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

  const handleListingTypeChange = (type) => {
    setFormData(prev => ({
      ...prev,
      listingType: prev.listingType.includes(type)
        ? prev.listingType.filter(t => t !== type)
        : [...prev.listingType, type]
    }));
    if (errors.listingType) {
      setErrors(prev => ({
        ...prev,
        listingType: undefined
      }));
    }
  };

  // Unit management functions
  const handleUnitChange = (e) => {
    const { name, value, type, checked } = e.target;
    setUnitForm(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
    if (unitErrors[name]) {
      setUnitErrors(prev => ({
        ...prev,
        [name]: undefined
      }));
    }
  };

  const handleFinancingTypeChange = (type) => {
    setUnitForm(prev => ({
      ...prev,
      financingTypes: prev.financingTypes.includes(type)
        ? prev.financingTypes.filter(t => t !== type)
        : [...prev.financingTypes, type]
    }));
  };

  const validateUnit = () => {
    const newErrors = {};
    if (!unitForm.unitType.trim()) newErrors.unitType = 'Unit type is required';
    if (!unitForm.floorArea || parseFloat(unitForm.floorArea) <= 0) newErrors.floorArea = 'Floor area must be greater than 0';
    if (!unitForm.lotArea || parseFloat(unitForm.lotArea) <= 0) newErrors.lotArea = 'Lot area must be greater than 0';
    if (!unitForm.reservationFee || parseFloat(unitForm.reservationFee) < 0) newErrors.reservationFee = 'Reservation fee is required';
    if (!unitForm.equityPeriodMonths || parseInt(unitForm.equityPeriodMonths) <= 0) newErrors.equityPeriodMonths = 'Equity period must be greater than 0';
    if (!unitForm.monthlyEquity || parseFloat(unitForm.monthlyEquity) < 0) newErrors.monthlyEquity = 'Monthly equity is required';
    if (!unitForm.totalSellingPrice || parseFloat(unitForm.totalSellingPrice) <= 0) newErrors.totalSellingPrice = 'Total selling price must be greater than 0';
    if (unitForm.financingTypes.length === 0) newErrors.financingTypes = 'Select at least one financing type';
    
    setUnitErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const addUnit = () => {
    if (validateUnit()) {
      setFormData(prev => ({
        ...prev,
        units: editingUnitIndex !== null
          ? prev.units.map((u, i) => i === editingUnitIndex ? unitForm : u)
          : [...prev.units, { ...unitForm }]
      }));
      resetUnitForm();
    }
  };

  const resetUnitForm = () => {
    setUnitForm({
      unitType: '',
      floorArea: '',
      lotArea: '',
      reservationFee: '',
      equityPeriodMonths: '',
      monthlyEquity: '',
      totalSellingPrice: '',
      financingTypes: []
    });
    setEditingUnitIndex(null);
    setUnitErrors({});
  };

  const editUnit = (index) => {
    setUnitForm(formData.units[index]);
    setEditingUnitIndex(index);
  };

  const deleteUnit = (index) => {
    setFormData(prev => ({
      ...prev,
      units: prev.units.filter((_, i) => i !== index)
    }));
  };

  const validateForm = () => {
    const newErrors = {};
    
    if (!formData.name?.trim()) newErrors.name = 'Property name is required';
    if (!formData.developer?.trim()) newErrors.developer = 'Developer name is required';
    if (!formData.priceRangeMin) newErrors.priceRangeMin = 'Minimum price is required';
    if (!formData.priceRangeMax) newErrors.priceRangeMax = 'Maximum price is required';
    if (formData.priceRangeMin && formData.priceRangeMax && parseFloat(formData.priceRangeMin) > parseFloat(formData.priceRangeMax)) {
      newErrors.priceRangeMax = 'Maximum price must be greater than minimum price';
    }
    if (!formData.location?.trim()) newErrors.location = 'Location is required';
    if (!Array.isArray(formData.listingType) || formData.listingType.length === 0) newErrors.listingType = 'Select at least one listing type';
    if (!formData.turnoverDate?.trim()) newErrors.turnoverDate = 'Turnover date is required';

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSave = async () => {
    if (validateForm()) {
      onUpdate(formData);
    }
  };

  if (!isOpen || !property) return null;

  return (
    <div className="fixed inset-0 bg-transparent backdrop-brightness-30 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-lg shadow-2xl max-w-6xl w-full max-h-[90vh] overflow-y-auto">
        {/* Header */}
        <div className="sticky top-0 bg-gradient-to-r from-blue-600 to-blue-700 px-10 py-7 flex justify-between items-center shadow-md z-10">
          <div>
            <h2 className="text-3xl font-bold text-white">
              {isEditMode ? 'Edit Property' : 'Property Details'}
            </h2>
            <p className="text-blue-100 text-sm mt-1">
              {isEditMode ? 'Update property information and units' : formData.name}
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
                name="name"
                value={formData.name || ''}
                onChange={handleChange}
                disabled={!isEditMode}
                className={`w-full px-4 py-3 border-2 rounded-lg font-medium transition-colors duration-200 ${
                  isEditMode
                    ? errors.name
                      ? 'border-red-500 focus:ring-2 focus:ring-red-500 focus:border-transparent'
                      : 'border-slate-300 focus:border-blue-500 focus:ring-2 focus:ring-blue-500 focus:ring-opacity-20'
                    : 'border-slate-200 bg-slate-50 text-slate-900 cursor-not-allowed'
                } outline-none`}
              />
              {errors.name && <p className="text-red-700 text-sm font-bold mt-1">{errors.name}</p>}
            </div>
            <div>
              <label className="block text-sm font-bold text-slate-900 mb-2">Developer Name *</label>
              <input
                type="text"
                name="developer"
                value={formData.developer || ''}
                onChange={handleChange}
                disabled={!isEditMode}
                className={`w-full px-4 py-3 border-2 rounded-lg font-medium transition-colors duration-200 ${
                  isEditMode
                    ? errors.developer
                      ? 'border-red-500 focus:ring-2 focus:ring-red-500 focus:border-transparent'
                      : 'border-slate-300 focus:border-blue-500 focus:ring-2 focus:ring-blue-500 focus:ring-opacity-20'
                    : 'border-slate-200 bg-slate-50 text-slate-700 cursor-not-allowed'
                } outline-none`}
              />
              {errors.developer && <p className="text-red-700 text-sm font-bold mt-1">{errors.developer}</p>}
            </div>
          </div>

          {/* Row 2: Description */}
          <div>
            <label className="block text-sm font-bold text-slate-900 mb-2">Basic Description</label>
            <textarea
              name="basicDescription"
              value={formData.basicDescription || ''}
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
              <label className="block text-sm font-bold text-slate-900 mb-2">Listing Type *</label>
              {isEditMode ? (
                <div className={`flex flex-wrap gap-3 p-3 border-2 rounded-lg transition-colors duration-200 ${
                  errors.listingType
                    ? 'border-red-500 bg-red-50'
                    : 'border-slate-300 bg-white'
                }`}>
                  {['Pre-Selling', 'RFO', 'Rent-To-Own'].map((type) => (
                    <label key={type} className="flex items-center gap-2 cursor-pointer">
                      <input
                        type="checkbox"
                        checked={Array.isArray(formData.listingType) && formData.listingType.includes(type)}
                        onChange={() => handleListingTypeChange(type)}
                        className="w-4 h-4 text-blue-600 rounded cursor-pointer"
                      />
                      <span className="text-sm font-medium text-slate-700">{type}</span>
                    </label>
                  ))}
                </div>
              ) : (
                <div className="px-4 py-3 border-2 border-slate-200 bg-slate-50 text-slate-700 rounded-lg font-medium">
                  {Array.isArray(formData.listingType) ? formData.listingType.join(', ') : formData.listingType || 'N/A'}
                </div>
              )}
              {errors.listingType && <p className="text-red-700 text-sm font-bold mt-1">{errors.listingType}</p>}
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

          {/* Row 8: Key Selling Points */}
          <div>
            <label className="block text-sm font-bold text-slate-900 mb-2">Key Selling Points</label>
            <textarea
              name="keySellingPoints"
              value={formData.keySellingPoints || ''}
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

          {/* Row 9: URL Fields */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label className="block text-sm font-bold text-slate-900 mb-2">Brochure PDF URL</label>
              <input
                type="url"
                name="brochurePdfUrl"
                value={formData.brochurePdfUrl || ''}
                onChange={handleChange}
                disabled={!isEditMode}
                placeholder="https://example.com/brochure.pdf"
                className={`w-full px-4 py-3 border-2 rounded-lg font-medium transition-colors duration-200 ${
                  isEditMode
                    ? 'border-slate-300 focus:border-blue-500 focus:ring-2 focus:ring-blue-500 focus:ring-opacity-20'
                    : 'border-slate-200 bg-slate-50 text-slate-700 cursor-not-allowed'
                } outline-none`}
              />
            </div>
            <div>
              <label className="block text-sm font-bold text-slate-900 mb-2">Inventory/Floor Plan Link</label>
              <input
                type="url"
                name="inventoryLink"
                value={formData.inventoryLink || ''}
                onChange={handleChange}
                disabled={!isEditMode}
                placeholder="https://example.com/inventory"
                className={`w-full px-4 py-3 border-2 rounded-lg font-medium transition-colors duration-200 ${
                  isEditMode
                    ? 'border-slate-300 focus:border-blue-500 focus:ring-2 focus:ring-blue-500 focus:ring-opacity-20'
                    : 'border-slate-200 bg-slate-50 text-slate-700 cursor-not-allowed'
                } outline-none`}
              />
            </div>
          </div>

          {/* Units Management Section (Only in Edit Mode) */}
          {isEditMode && (
            <div className="border-t-2 border-slate-200 pt-8">
              <h3 className="text-2xl font-bold text-slate-900 mb-6 flex items-center gap-2">
                <FiPlus className="text-blue-600" />
                Property Units (Optional)
              </h3>

              {/* Unit Form */}
              <div className="bg-slate-50 p-6 rounded-lg mb-6 border border-slate-300">
                <h4 className="text-lg font-semibold text-slate-800 mb-4">
                  {editingUnitIndex !== null ? 'Edit Unit' : 'Add New Unit'}
                </h4>
                
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
                  <div>
                    <label className="block text-sm font-semibold text-slate-700 mb-1">Unit Type *</label>
                    <input
                      type="text"
                      name="unitType"
                      value={unitForm.unitType}
                      onChange={handleUnitChange}
                      placeholder="e.g., 1BR, 2BR, Studio"
                      className={`w-full px-3 py-2 border-2 rounded-lg font-medium text-sm transition-colors ${
                        unitErrors.unitType ? 'border-red-500' : 'border-slate-300 focus:border-blue-500'
                      } outline-none`}
                    />
                    {unitErrors.unitType && <p className="text-red-600 text-xs font-semibold mt-1">{unitErrors.unitType}</p>}
                  </div>
                  <div>
                    <label className="block text-sm font-semibold text-slate-700 mb-1">Floor Area (sqm) *</label>
                    <input
                      type="number"
                      name="floorArea"
                      value={unitForm.floorArea}
                      onChange={handleUnitChange}
                      placeholder="0.00"
                      className={`w-full px-3 py-2 border-2 rounded-lg font-medium text-sm transition-colors ${
                        unitErrors.floorArea ? 'border-red-500' : 'border-slate-300 focus:border-blue-500'
                      } outline-none`}
                    />
                    {unitErrors.floorArea && <p className="text-red-600 text-xs font-semibold mt-1">{unitErrors.floorArea}</p>}
                  </div>
                  <div>
                    <label className="block text-sm font-semibold text-slate-700 mb-1">Lot Area (sqm) *</label>
                    <input
                      type="number"
                      name="lotArea"
                      value={unitForm.lotArea}
                      onChange={handleUnitChange}
                      placeholder="0.00"
                      className={`w-full px-3 py-2 border-2 rounded-lg font-medium text-sm transition-colors ${
                        unitErrors.lotArea ? 'border-red-500' : 'border-slate-300 focus:border-blue-500'
                      } outline-none`}
                    />
                    {unitErrors.lotArea && <p className="text-red-600 text-xs font-semibold mt-1">{unitErrors.lotArea}</p>}
                  </div>
                  <div>
                    <label className="block text-sm font-semibold text-slate-700 mb-1">Reservation Fee *</label>
                    <input
                      type="number"
                      name="reservationFee"
                      value={unitForm.reservationFee}
                      onChange={handleUnitChange}
                      placeholder="0.00"
                      className={`w-full px-3 py-2 border-2 rounded-lg font-medium text-sm transition-colors ${
                        unitErrors.reservationFee ? 'border-red-500' : 'border-slate-300 focus:border-blue-500'
                      } outline-none`}
                    />
                    {unitErrors.reservationFee && <p className="text-red-600 text-xs font-semibold mt-1">{unitErrors.reservationFee}</p>}
                  </div>
                  <div>
                    <label className="block text-sm font-semibold text-slate-700 mb-1">Equity Period (months) *</label>
                    <input
                      type="number"
                      name="equityPeriodMonths"
                      value={unitForm.equityPeriodMonths}
                      onChange={handleUnitChange}
                      placeholder="0"
                      className={`w-full px-3 py-2 border-2 rounded-lg font-medium text-sm transition-colors ${
                        unitErrors.equityPeriodMonths ? 'border-red-500' : 'border-slate-300 focus:border-blue-500'
                      } outline-none`}
                    />
                    {unitErrors.equityPeriodMonths && <p className="text-red-600 text-xs font-semibold mt-1">{unitErrors.equityPeriodMonths}</p>}
                  </div>
                  <div>
                    <label className="block text-sm font-semibold text-slate-700 mb-1">Monthly Equity *</label>
                    <input
                      type="number"
                      name="monthlyEquity"
                      value={unitForm.monthlyEquity}
                      onChange={handleUnitChange}
                      placeholder="0.00"
                      className={`w-full px-3 py-2 border-2 rounded-lg font-medium text-sm transition-colors ${
                        unitErrors.monthlyEquity ? 'border-red-500' : 'border-slate-300 focus:border-blue-500'
                      } outline-none`}
                    />
                    {unitErrors.monthlyEquity && <p className="text-red-600 text-xs font-semibold mt-1">{unitErrors.monthlyEquity}</p>}
                  </div>
                </div>

                <div className="mb-4">
                  <label className="block text-sm font-semibold text-slate-700 mb-1">Total Selling Price *</label>
                  <input
                    type="number"
                    name="totalSellingPrice"
                    value={unitForm.totalSellingPrice}
                    onChange={handleUnitChange}
                    placeholder="0.00"
                    className={`w-full px-3 py-2 border-2 rounded-lg font-medium text-sm transition-colors ${
                      unitErrors.totalSellingPrice ? 'border-red-500' : 'border-slate-300 focus:border-blue-500'
                    } outline-none`}
                  />
                  {unitErrors.totalSellingPrice && <p className="text-red-600 text-xs font-semibold mt-1">{unitErrors.totalSellingPrice}</p>}
                </div>

                <div className="mb-4">
                  <label className="block text-sm font-semibold text-slate-700 mb-2">Financing Types *</label>
                  <div className="flex flex-wrap gap-3">
                    {financingOptions.map((type) => (
                      <label key={type} className="flex items-center gap-2 cursor-pointer">
                        <input
                          type="checkbox"
                          checked={unitForm.financingTypes.includes(type)}
                          onChange={() => handleFinancingTypeChange(type)}
                          className="w-4 h-4 text-blue-600 rounded cursor-pointer"
                        />
                        <span className="text-sm font-medium text-slate-700">{type}</span>
                      </label>
                    ))}
                  </div>
                  {unitErrors.financingTypes && <p className="text-red-600 text-xs font-semibold mt-2">{unitErrors.financingTypes}</p>}
                </div>

                <div className="flex gap-3">
                  <button
                    type="button"
                    onClick={addUnit}
                    className="px-4 py-2 bg-blue-600 text-white rounded-lg font-semibold hover:bg-blue-700 transition-colors duration-200 text-sm"
                  >
                    {editingUnitIndex !== null ? 'Update Unit' : 'Add Unit'}
                  </button>
                  {editingUnitIndex !== null && (
                    <button
                      type="button"
                      onClick={resetUnitForm}
                      className="px-4 py-2 border-2 border-slate-300 text-slate-700 rounded-lg font-semibold hover:bg-slate-100 transition-colors duration-200 text-sm"
                    >
                      Cancel
                    </button>
                  )}
                </div>
              </div>

              {/* Units Table */}
              {formData.units && formData.units.length > 0 && (
                <div className="overflow-x-auto">
                  <table className="w-full text-sm">
                    <thead className="bg-slate-200">
                      <tr>
                        <th className="px-4 py-3 text-left font-semibold text-slate-900">Type</th>
                        <th className="px-4 py-3 text-left font-semibold text-slate-900">Floor/Lot (sqm)</th>
                        <th className="px-4 py-3 text-left font-semibold text-slate-900">Pricing</th>
                        <th className="px-4 py-3 text-left font-semibold text-slate-900">Financing</th>
                        <th className="px-4 py-3 text-left font-semibold text-slate-900">Actions</th>
                      </tr>
                    </thead>
                    <tbody>
                      {formData.units.map((unit, index) => (
                        <tr key={index} className="border-b border-slate-200 hover:bg-slate-100">
                          <td className="px-4 py-3 font-medium text-slate-900">{unit.unitType}</td>
                          <td className="px-4 py-3 text-slate-700">{unit.floorArea} / {unit.lotArea}</td>
                          <td className="px-4 py-3 text-slate-700">₱{parseFloat(unit.totalSellingPrice).toLocaleString()}</td>
                          <td className="px-4 py-3 text-slate-700 text-xs">
                            <div className="flex flex-wrap gap-1">
                              {unit.financingTypes && unit.financingTypes.map((type, i) => (
                                <span key={i} className="bg-slate-300 px-2 py-1 rounded">{type}</span>
                              ))}
                            </div>
                          </td>
                          <td className="px-4 py-3 flex gap-2">
                            <button
                              type="button"
                              onClick={() => editUnit(index)}
                              className="text-blue-600 hover:text-blue-800 transition-colors"
                              title="Edit"
                            >
                              <FiEdit2 size={18} />
                            </button>
                            <button
                              type="button"
                              onClick={() => deleteUnit(index)}
                              className="text-red-600 hover:text-red-800 transition-colors"
                              title="Delete"
                            >
                              <FiTrash2 size={18} />
                            </button>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          )}



          {/* Units Display (View Mode Only) */}
          {!isEditMode && formData.units && formData.units.length > 0 && (
            <div className="border-t-2 border-slate-200 pt-6">
              <h3 className="text-lg font-bold text-slate-900 mb-4">Property Units ({formData.units.length})</h3>
              <div className="overflow-x-auto">
                <table className="w-full text-sm">
                  <thead className="bg-slate-200">
                    <tr>
                      <th className="px-4 py-3 text-left font-semibold text-slate-900">Type</th>
                      <th className="px-4 py-3 text-left font-semibold text-slate-900">Floor/Lot (sqm)</th>
                      <th className="px-4 py-3 text-left font-semibold text-slate-900">Pricing</th>
                      <th className="px-4 py-3 text-left font-semibold text-slate-900">Financing</th>
                    </tr>
                  </thead>
                  <tbody>
                    {formData.units.map((unit, index) => (
                      <tr key={index} className="border-b border-slate-200 hover:bg-slate-50">
                        <td className="px-4 py-3 font-medium text-slate-900">{unit.unitType}</td>
                        <td className="px-4 py-3 text-slate-700">{unit.floorArea} / {unit.lotArea}</td>
                        <td className="px-4 py-3 text-slate-700">₱{parseFloat(unit.totalSellingPrice).toLocaleString()}</td>
                        <td className="px-4 py-3 text-slate-700 text-xs">
                          <div className="flex flex-wrap gap-1">
                            {unit.financingTypes && unit.financingTypes.map((type, i) => (
                              <span key={i} className="bg-slate-200 px-2 py-1 rounded">{type}</span>
                            ))}
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}

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
