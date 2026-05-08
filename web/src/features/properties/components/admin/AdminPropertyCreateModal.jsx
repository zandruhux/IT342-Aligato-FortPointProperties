import { useState } from 'react';
import { FiX, FiPlus, FiEdit2, FiTrash2 } from 'react-icons/fi';

export default function AdminPropertyCreateModal({ isOpen, onClose, onSubmit, isLoading }) {
  const [formData, setFormData] = useState({
    name: '',
    basicDescription: '',
    developer: '',
    priceRangeMin: '',
    priceRangeMax: '',
    location: '',
    listingType: ['Pre-Selling'],
    petFriendly: false,
    parkingAvailable: false,
    turnoverDate: '',
    amenities: '',
    keySellingPoints: '',
    brochurePdfUrl: '',
    inventoryLink: '',
    units: []
  });

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
  const [errors, setErrors] = useState({});
  const [unitErrors, setUnitErrors] = useState({});
  const listingTypeOptions = ['Pre-Selling', 'RFO', 'Rent-To-Own'];
  const financingOptions = ['Cash Only', 'Bank Financing', 'In-House'];

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: undefined }));
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
        setErrors(prev => ({ ...prev, listingType: undefined }));
    }
  };

  const handleUnitChange = (e) => {
    const { name, value, type, checked } = e.target;
    setUnitForm(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
    if (unitErrors[name]) {
      setUnitErrors(prev => ({ ...prev, [name]: undefined }));
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
    if (!unitForm.unitType.trim()) newErrors.unitType = 'Required';
    if (!unitForm.floorArea || parseFloat(unitForm.floorArea) <= 0) newErrors.floorArea = 'Required';
    if (!unitForm.lotArea || parseFloat(unitForm.lotArea) <= 0) newErrors.lotArea = 'Required';
    if (!unitForm.reservationFee || parseFloat(unitForm.reservationFee) < 0) newErrors.reservationFee = 'Required';
    if (!unitForm.equityPeriodMonths || parseInt(unitForm.equityPeriodMonths) <= 0) newErrors.equityPeriodMonths = 'Required';
    if (!unitForm.monthlyEquity || parseFloat(unitForm.monthlyEquity) < 0) newErrors.monthlyEquity = 'Required';
    if (!unitForm.totalSellingPrice || parseFloat(unitForm.totalSellingPrice) <= 0) newErrors.totalSellingPrice = 'Required';
    if (unitForm.financingTypes.length === 0) newErrors.financingTypes = 'Select 1';
    
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
      unitType: '', floorArea: '', lotArea: '', reservationFee: '',
      equityPeriodMonths: '', monthlyEquity: '', totalSellingPrice: '',
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
    if (!formData.name.trim()) newErrors.name = 'Property name is required';
    if (!formData.developer.trim()) newErrors.developer = 'Developer name is required';
    if (!formData.priceRangeMin) newErrors.priceRangeMin = 'Required';
    if (!formData.priceRangeMax) newErrors.priceRangeMax = 'Required';
    if (!formData.location.trim()) newErrors.location = 'Location is required';
    if (formData.listingType.length === 0) newErrors.listingType = 'Required';
    if (!formData.turnoverDate.trim()) newErrors.turnoverDate = 'Required';

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (validateForm()) onSubmit(formData);
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-transparent backdrop-brightness-30 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-lg shadow-2xl max-w-6xl w-full max-h-[90vh] overflow-y-auto">
        {/* Header - Now Blue */}
        <div className="sticky top-0 bg-gradient-to-r from-blue-600 to-blue-700 px-10 py-7 flex justify-between items-center shadow-md z-10">
          <div>
            <h2 className="text-3xl font-bold text-white">Create New Property</h2>
            <p className="text-blue-100 text-sm mt-1">Populate the property details and unit inventory</p>
          </div>
          <button onClick={onClose} className="text-white hover:bg-blue-800 p-2 rounded-lg transition-colors">
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
                value={formData.name}
                onChange={handleChange}
                className={`w-full px-4 py-3 border-2 rounded-lg font-medium transition-all ${
                  errors.name ? 'border-red-500 focus:ring-red-500' : 'border-slate-300 focus:border-blue-500 focus:ring-blue-500/20'
                } outline-none focus:ring-2`}
              />
            </div>
            <div>
              <label className="block text-sm font-bold text-slate-900 mb-2">Developer Name *</label>
              <input
                type="text"
                name="developer"
                value={formData.developer}
                onChange={handleChange}
                className={`w-full px-4 py-3 border-2 rounded-lg font-medium transition-all ${
                  errors.developer ? 'border-red-500 focus:ring-red-500' : 'border-slate-300 focus:border-blue-500 focus:ring-blue-500/20'
                } outline-none focus:ring-2`}
              />
            </div>
          </div>

          {/* Row 2: Description */}
          <div>
            <label className="block text-sm font-bold text-slate-900 mb-2">Basic Description</label>
            <textarea
              name="basicDescription"
              value={formData.basicDescription}
              onChange={handleChange}
              rows="3"
              className="w-full px-4 py-3 border-2 border-slate-300 rounded-lg font-medium focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20 outline-none transition-all resize-none"
            />
          </div>

          {/* Row 3: Pricing and Location */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div>
              <label className="block text-sm font-bold text-slate-900 mb-2">Price Min *</label>
              <input type="number" name="priceRangeMin" value={formData.priceRangeMin} onChange={handleChange} className="w-full px-4 py-3 border-2 border-slate-300 rounded-lg outline-none focus:border-blue-500" />
            </div>
            <div>
              <label className="block text-sm font-bold text-slate-900 mb-2">Price Max *</label>
              <input type="number" name="priceRangeMax" value={formData.priceRangeMax} onChange={handleChange} className="w-full px-4 py-3 border-2 border-slate-300 rounded-lg outline-none focus:border-blue-500" />
            </div>
            <div>
              <label className="block text-sm font-bold text-slate-900 mb-2">Location *</label>
              <input type="text" name="location" value={formData.location} onChange={handleChange} className="w-full px-4 py-3 border-2 border-slate-300 rounded-lg outline-none focus:border-blue-500" />
            </div>
          </div>

          {/* Row 4: Listing Type & Dates */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label className="block text-sm font-bold text-slate-900 mb-2">Listing Type *</label>
              <div className="flex flex-wrap gap-3 p-3 border-2 border-slate-300 rounded-lg bg-white">
                {listingTypeOptions.map((type) => (
                  <label key={type} className="flex items-center gap-2 cursor-pointer">
                    <input
                      type="checkbox"
                      checked={formData.listingType.includes(type)}
                      onChange={() => handleListingTypeChange(type)}
                      className="w-4 h-4 text-blue-600 rounded"
                    />
                    <span className="text-sm font-medium text-slate-700">{type}</span>
                  </label>
                ))}
              </div>
            </div>
            <div>
              <label className="block text-sm font-bold text-slate-900 mb-2">Turnover Date (YYYY-MM) *</label>
              <input type="text" name="turnoverDate" value={formData.turnoverDate} onChange={handleChange} placeholder="2025-12" className="w-full px-4 py-3 border-2 border-slate-300 rounded-lg outline-none focus:border-blue-500" />
            </div>
          </div>

          {/* Row 5: Details & Checkboxes */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label className="block text-sm font-bold text-slate-900 mb-2">Amenities</label>
              <textarea name="amenities" value={formData.amenities} onChange={handleChange} rows="3" className="w-full px-4 py-3 border-2 border-slate-300 rounded-lg outline-none focus:border-blue-500 resize-none" />
            </div>
            <div className="space-y-4 pt-8">
              <div className="flex items-center">
                <input type="checkbox" id="petFriendly" name="petFriendly" checked={formData.petFriendly} onChange={handleChange} className="w-5 h-5 text-blue-600 rounded cursor-pointer" />
                <label htmlFor="petFriendly" className="ml-3 text-sm font-medium text-slate-700">Pet Friendly</label>
              </div>
              <div className="flex items-center">
                <input type="checkbox" id="parkingAvailable" name="parkingAvailable" checked={formData.parkingAvailable} onChange={handleChange} className="w-5 h-5 text-blue-600 rounded cursor-pointer" />
                <label htmlFor="parkingAvailable" className="ml-3 text-sm font-medium text-slate-700">Parking Available</label>
              </div>
            </div>
          </div>

          {/* Units Management Section - Table-like 3-column Grid */}
          <div className="border-t-2 border-slate-200 pt-8">
            <h3 className="text-2xl font-bold text-slate-900 mb-6 flex items-center gap-2">
              <FiPlus className="text-blue-600" />
              Property Units (Inventory)
            </h3>

            <div className="bg-slate-50 p-6 rounded-lg mb-6 border border-slate-300">
              <h4 className="text-lg font-bold text-slate-800 mb-4">{editingUnitIndex !== null ? 'Edit Unit' : 'Add New Unit'}</h4>
              
              {/* Unit Form Grid */}
              <div className="grid grid-cols-1 md:grid-cols-3 gap-x-6 gap-y-4 mb-4">
                <div>
                  <label className="block text-xs font-bold text-slate-500 uppercase mb-1">Unit Type *</label>
                  <input type="text" name="unitType" value={unitForm.unitType} onChange={handleUnitChange} className="w-full px-3 py-2 border-2 border-slate-300 rounded-lg text-sm outline-none focus:border-blue-500" />
                </div>
                <div>
                  <label className="block text-xs font-bold text-slate-500 uppercase mb-1">Floor Area (sqm) *</label>
                  <input type="number" name="floorArea" value={unitForm.floorArea} onChange={handleUnitChange} className="w-full px-3 py-2 border-2 border-slate-300 rounded-lg text-sm outline-none focus:border-blue-500" />
                </div>
                <div>
                  <label className="block text-xs font-bold text-slate-500 uppercase mb-1">Lot Area (sqm) *</label>
                  <input type="number" name="lotArea" value={unitForm.lotArea} onChange={handleUnitChange} className="w-full px-3 py-2 border-2 border-slate-300 rounded-lg text-sm outline-none focus:border-blue-500" />
                </div>
                <div>
                  <label className="block text-xs font-bold text-slate-500 uppercase mb-1">Reservation Fee *</label>
                  <input type="number" name="reservationFee" value={unitForm.reservationFee} onChange={handleUnitChange} className="w-full px-3 py-2 border-2 border-slate-300 rounded-lg text-sm outline-none focus:border-blue-500" />
                </div>
                <div>
                  <label className="block text-xs font-bold text-slate-500 uppercase mb-1">Equity Months *</label>
                  <input type="number" name="equityPeriodMonths" value={unitForm.equityPeriodMonths} onChange={handleUnitChange} className="w-full px-3 py-2 border-2 border-slate-300 rounded-lg text-sm outline-none focus:border-blue-500" />
                </div>
                <div>
                  <label className="block text-xs font-bold text-slate-500 uppercase mb-1">Monthly Equity *</label>
                  <input type="number" name="monthlyEquity" value={unitForm.monthlyEquity} onChange={handleUnitChange} className="w-full px-3 py-2 border-2 border-slate-300 rounded-lg text-sm outline-none focus:border-blue-500" />
                </div>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-4">
                <div>
                  <label className="block text-xs font-bold text-slate-500 uppercase mb-1">Total Selling Price *</label>
                  <input type="number" name="totalSellingPrice" value={unitForm.totalSellingPrice} onChange={handleUnitChange} className="w-full px-3 py-2 border-2 border-slate-300 rounded-lg text-sm outline-none focus:border-blue-500" />
                </div>
                <div>
                  <label className="block text-xs font-bold text-slate-500 uppercase mb-1">Financing Options *</label>
                  <div className="flex gap-4 mt-2">
                    {financingOptions.map(f => (
                      <label key={f} className="flex items-center gap-1 cursor-pointer">
                        <input type="checkbox" checked={unitForm.financingTypes.includes(f)} onChange={() => handleFinancingTypeChange(f)} className="text-blue-600" />
                        <span className="text-xs font-medium text-slate-700">{f}</span>
                      </label>
                    ))}
                  </div>
                </div>
              </div>

              <div className="flex gap-3">
                <button type="button" onClick={addUnit} className="px-5 py-2 bg-blue-600 text-white rounded-lg font-bold hover:bg-blue-700 transition-colors text-sm">
                  {editingUnitIndex !== null ? 'Update Unit' : 'Add to Inventory'}
                </button>
                {editingUnitIndex !== null && <button type="button" onClick={resetUnitForm} className="px-5 py-2 border-2 border-slate-300 text-slate-600 rounded-lg text-sm">Cancel</button>}
              </div>
            </div>

            {/* Units Table Display */}
            {formData.units.length > 0 && (
              <div className="overflow-x-auto border-2 border-slate-200 rounded-lg">
                <table className="w-full text-sm">
                  <thead className="bg-slate-100 border-b-2 border-slate-200">
                    <tr>
                      <th className="px-4 py-3 text-left font-bold text-slate-900">Unit Type</th>
                      <th className="px-4 py-3 text-left font-bold text-slate-900">Area (F/L)</th>
                      <th className="px-4 py-3 text-left font-bold text-slate-900">Total Price</th>
                      <th className="px-4 py-3 text-left font-bold text-slate-900">Actions</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-200">
                    {formData.units.map((unit, index) => (
                      <tr key={index} className="hover:bg-slate-50 transition-colors">
                        <td className="px-4 py-3 font-semibold text-slate-900">{unit.unitType}</td>
                        <td className="px-4 py-3 text-slate-600">{unit.floorArea} / {unit.lotArea} sqm</td>
                        <td className="px-4 py-3 font-bold text-blue-700">₱{parseFloat(unit.totalSellingPrice).toLocaleString()}</td>
                        <td className="px-4 py-3 flex gap-3">
                          <button onClick={() => editUnit(index)} className="text-blue-600 hover:text-blue-800"><FiEdit2 size={18} /></button>
                          <button onClick={() => deleteUnit(index)} className="text-red-600 hover:text-red-800"><FiTrash2 size={18} /></button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </div>

        {/* Footer */}
        <div className="sticky bottom-0 bg-slate-50 px-10 py-6 flex justify-end gap-4 border-t border-slate-200 shadow-md">
          <button onClick={onClose} disabled={isLoading} className="px-8 py-3 border-2 border-slate-300 text-slate-900 rounded-lg font-bold hover:bg-slate-100 transition-colors">
            Cancel
          </button>
          <button onClick={handleSubmit} disabled={isLoading} className="px-8 py-3 bg-gradient-to-r from-blue-600 to-blue-700 text-white rounded-lg font-bold hover:from-blue-700 hover:to-blue-800 transition-all shadow-lg">
            {isLoading ? 'Processing...' : 'Create Property'}
          </button>
        </div>
      </div>
    </div>
  );
}
