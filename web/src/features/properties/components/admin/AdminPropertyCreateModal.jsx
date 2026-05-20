import { useEffect, useMemo, useState } from 'react';
import { FiX, FiPlus, FiEdit2, FiTrash2, FiImage } from 'react-icons/fi';
import { LISTING_TYPES, FINANCING_TYPES } from '../../../../shared/utils/constants';
import * as propertyApi from '../../api/propertyApi';

export default function AdminPropertyCreateModal({ isOpen, onClose, onSubmit, isLoading }) {
  const [formData, setFormData] = useState({
    name: '',
    basicDescription: '',
    developer: '',
    location: '',
    listingTypes: ['PRE_SELLING'],
    financingTypes: [],
    petFriendly: false,
    parkingAvailable: false,
    hasPromo: false,
    featured: false,
    visible: true,
    turnoverDate: '',
    amenityIds: [],
    customAmenities: [],
    photos: [],
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
    totalSellingPrice: ''
  });

  const [availableAmenities, setAvailableAmenities] = useState([]);
  const [editingUnitIndex, setEditingUnitIndex] = useState(null);
  const [errors, setErrors] = useState({});
  const [unitErrors, setUnitErrors] = useState({});
  const [selectedPhotoFiles, setSelectedPhotoFiles] = useState([]);
  const [photoPreviewUrls, setPhotoPreviewUrls] = useState([]);
  const [amenitySearch, setAmenitySearch] = useState('');
  const [isUploadingPhotos, setIsUploadingPhotos] = useState(false);

  useEffect(() => {
    if (!isOpen) return;
    propertyApi.getAmenities(false)
      .then((data) => setAvailableAmenities(Array.isArray(data) ? data : []))
      .catch(() => setAvailableAmenities([]));
  }, [isOpen]);

  useEffect(() => {
    const urls = selectedPhotoFiles.map((file) => URL.createObjectURL(file));
    setPhotoPreviewUrls(urls);

    return () => {
      urls.forEach((url) => URL.revokeObjectURL(url));
    };
  }, [selectedPhotoFiles]);

  const topAmenities = useMemo(() => {
    return [...availableAmenities]
      .sort((a, b) => {
        const countDiff = (b.usageCount || 0) - (a.usageCount || 0);
        if (countDiff !== 0) return countDiff;
        return String(a.name || '').localeCompare(String(b.name || ''));
      })
      .slice(0, 5);
  }, [availableAmenities]);

  const amenitySearchMatches = useMemo(() => {
    const term = amenitySearch.trim().toLowerCase();
    if (!term) return [];

    return availableAmenities
      .filter((amenity) => !formData.amenityIds.includes(amenity.id))
      .filter((amenity) => String(amenity.name || '').toLowerCase().includes(term))
      .slice(0, 6);
  }, [amenitySearch, availableAmenities, formData.amenityIds]);

  const customAmenityNames = Array.isArray(formData.customAmenities) ? formData.customAmenities : [];
  const searchedAmenityExists = availableAmenities.some(
    (amenity) => String(amenity.name || '').toLowerCase() === amenitySearch.trim().toLowerCase()
  ) || customAmenityNames.some(
    (amenity) => amenity.toLowerCase() === amenitySearch.trim().toLowerCase()
  );

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
      listingTypes: prev.listingTypes.includes(type)
        ? prev.listingTypes.filter(t => t !== type)
        : [...prev.listingTypes, type]
    }));
    if (errors.listingTypes) {
        setErrors(prev => ({ ...prev, listingTypes: undefined }));
    }
  };

  const handleFinancingTypeChange = (type) => {
    setFormData(prev => ({
      ...prev,
      financingTypes: prev.financingTypes.includes(type)
        ? prev.financingTypes.filter(t => t !== type)
        : [...prev.financingTypes, type]
    }));
  };

  const handleAmenityChange = (amenityId) => {
    setFormData(prev => ({
      ...prev,
      amenityIds: prev.amenityIds.includes(amenityId)
        ? prev.amenityIds.filter(id => id !== amenityId)
        : [...prev.amenityIds, amenityId]
    }));
  };

  const addAmenity = (amenityId) => {
    setFormData(prev => prev.amenityIds.includes(amenityId)
      ? prev
      : { ...prev, amenityIds: [...prev.amenityIds, amenityId] });
    setAmenitySearch('');
  };

  const addCustomAmenity = (amenityName) => {
    const normalizedName = amenityName.trim();
    if (!normalizedName) return;

    const alreadyExists = availableAmenities.some(
      (amenity) => String(amenity.name || '').toLowerCase() === normalizedName.toLowerCase()
    ) || customAmenityNames.some(
      (amenity) => amenity.toLowerCase() === normalizedName.toLowerCase()
    );

    if (alreadyExists) {
      setAmenitySearch('');
      return;
    }

    setFormData(prev => ({
      ...prev,
      customAmenities: [...(Array.isArray(prev.customAmenities) ? prev.customAmenities : []), normalizedName],
    }));
    setAmenitySearch('');
  };

  const removeCustomAmenity = (amenityName) => {
    setFormData(prev => ({
      ...prev,
      customAmenities: (Array.isArray(prev.customAmenities) ? prev.customAmenities : [])
        .filter((item) => item !== amenityName),
    }));
  };

  const handlePhotoFileChange = (e) => {
    const files = Array.from(e.target.files || []);
    setSelectedPhotoFiles(files);
    setFormData(prev => ({
      ...prev,
      photos: []
    }));
  };

  const removeSelectedPhoto = (indexToRemove) => {
    setSelectedPhotoFiles(prev => prev.filter((_, index) => index !== indexToRemove));
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

  const validateUnit = () => {
    const newErrors = {};
    if (!unitForm.unitType.trim()) newErrors.unitType = 'Required';
    if (!unitForm.floorArea || parseFloat(unitForm.floorArea) <= 0) newErrors.floorArea = 'Required';
    if (!unitForm.lotArea || parseFloat(unitForm.lotArea) <= 0) newErrors.lotArea = 'Required';
    if (!unitForm.reservationFee || parseFloat(unitForm.reservationFee) < 0) newErrors.reservationFee = 'Required';
    if (!unitForm.equityPeriodMonths || parseInt(unitForm.equityPeriodMonths) <= 0) newErrors.equityPeriodMonths = 'Required';
    if (!unitForm.monthlyEquity || parseFloat(unitForm.monthlyEquity) < 0) newErrors.monthlyEquity = 'Required';
    if (!unitForm.totalSellingPrice || parseFloat(unitForm.totalSellingPrice) <= 0) newErrors.totalSellingPrice = 'Required';
    
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
    if (!formData.location.trim()) newErrors.location = 'Location is required';
    if (formData.listingTypes.length === 0) newErrors.listingTypes = 'Required';
    if (formData.financingTypes.length === 0) newErrors.financingTypes = 'Required';
    if (!formData.turnoverDate.trim()) newErrors.turnoverDate = 'Required';

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (validateForm()) {
      setIsUploadingPhotos(true);
      try {
        setErrors(prev => ({ ...prev, photos: undefined, submit: undefined }));
        const uploadedPhotos = [];
        for (const [index, file] of selectedPhotoFiles.entries()) {
          uploadedPhotos.push(await propertyApi.uploadPropertyPhoto(file, index));
        }

        await onSubmit({
          ...formData,
          photos: uploadedPhotos,
          customAmenities: formData.customAmenities
            ? (Array.isArray(formData.customAmenities)
              ? formData.customAmenities
              : formData.customAmenities.split(',').map((item) => item.trim()).filter(Boolean))
            : [],
          units: formData.units.map((unit) => ({
            ...unit,
            floorArea: unit.floorArea === '' ? null : Number(unit.floorArea),
            lotArea: unit.lotArea === '' ? null : Number(unit.lotArea),
            reservationFee: Number(unit.reservationFee),
            equityPeriodMonths: Number(unit.equityPeriodMonths),
            monthlyEquity: Number(unit.monthlyEquity),
            totalSellingPrice: Number(unit.totalSellingPrice),
          })),
        });
      } catch (err) {
        setErrors(prev => ({
          ...prev,
          submit: err?.message || 'Failed to create property',
        }));
      } finally {
        setIsUploadingPhotos(false);
      }
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-transparent backdrop-brightness-30 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-lg shadow-2xl max-w-6xl w-full max-h-[90vh] overflow-y-auto">
        <div className="sticky top-0 bg-gradient-to-r from-blue-600 to-blue-700 px-10 py-7 flex justify-between items-center shadow-md z-10">
          <div>
            <h2 className="text-3xl font-bold text-white">Create New Property</h2>
            <p className="text-blue-100 text-sm mt-1">Populate the property details and unit inventory</p>
          </div>
          <button onClick={onClose} className="text-white hover:bg-blue-800 p-2 rounded-lg transition-colors">
            <FiX size={28} />
          </button>
        </div>

        <div className="p-10 space-y-10">
          {errors.submit && (
            <div className="bg-red-50 border-l-4 border-red-600 p-4 rounded">
              <p className="text-red-800 font-bold">{errors.submit}</p>
            </div>
          )}




                    <div>
            <label className="block text-sm font-bold text-slate-900 mb-2">Property Photos</label>
            <div className="border-2 border-dashed border-slate-300 rounded-lg p-5 bg-slate-50">
              <label className="inline-flex items-center gap-2 px-5 py-2 bg-white border-2 border-slate-300 rounded-lg font-bold text-slate-700 hover:bg-slate-100 cursor-pointer transition-colors">
                <FiImage size={18} />
                Select Photos
                <input
                  type="file"
                  accept="image/png,image/jpeg,image/webp"
                  multiple
                  onChange={handlePhotoFileChange}
                  className="hidden"
                />
              </label>
              <p className="text-sm text-slate-500 mt-3">Accepted formats: JPG, PNG, WEBP. The first photo becomes the cover image.</p>
              {errors.photos && <p className="text-sm text-red-600 font-semibold mt-2">{errors.photos}</p>}

              {selectedPhotoFiles.length > 0 && (
                <div className="mt-5 grid grid-cols-1 md:grid-cols-2 gap-4">
                  {selectedPhotoFiles.map((file, index) => (
                    <div key={`${file.name}-${index}`} className="overflow-hidden rounded-lg border border-slate-200 bg-white">
                      <div className="aspect-[16/9] bg-slate-100">
                        <img
                          src={photoPreviewUrls[index]}
                          alt={`Selected photo ${index + 1}`}
                          className="h-full w-full object-cover"
                          loading="lazy"
                          decoding="async"
                        />
                      </div>
                      <div className="flex items-center justify-between gap-3 px-4 py-3">
                        <div className="min-w-0">
                          <p className="text-sm font-semibold text-slate-900 truncate">{file.name}</p>
                          <p className="text-xs text-slate-500">{(file.size / 1024 / 1024).toFixed(2)} MB</p>
                        </div>
                        <button
                          type="button"
                          onClick={() => removeSelectedPhoto(index)}
                          className="text-red-600 hover:text-red-800"
                          aria-label={`Remove ${file.name}`}
                        >
                          <FiX size={18} />
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>

          
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

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label className="block text-sm font-bold text-slate-900 mb-2">Location *</label>
              <input type="text" name="location" value={formData.location} onChange={handleChange} className="w-full px-4 py-3 border-2 border-slate-300 rounded-lg outline-none focus:border-blue-500" />
            </div>
            <div>
              <label className="block text-sm font-bold text-slate-900 mb-2">Turnover Date (YYYY-MM) *</label>
              <input type="text" name="turnoverDate" value={formData.turnoverDate} onChange={handleChange} placeholder="2025-12" className="w-full px-4 py-3 border-2 border-slate-300 rounded-lg outline-none focus:border-blue-500" />
            </div>
          </div>

          <div>
            <label className="block text-sm font-bold text-slate-900 mb-2">Key Selling Points</label>
            <textarea
              name="keySellingPoints"
              value={formData.keySellingPoints}
              onChange={handleChange}
              rows="4"
              placeholder="Example: Near schools, high rental demand, strong capital appreciation"
              className="w-full px-4 py-3 border-2 border-slate-300 rounded-lg font-medium focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20 outline-none transition-all resize-none"
            />
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label className="block text-sm font-bold text-slate-900 mb-2">Brochure PDF URL</label>
              <input
                type="url"
                name="brochurePdfUrl"
                value={formData.brochurePdfUrl}
                onChange={handleChange}
                placeholder="https://..."
                className="w-full px-4 py-3 border-2 border-slate-300 rounded-lg outline-none focus:border-blue-500"
              />
            </div>
            <div>
              <label className="block text-sm font-bold text-slate-900 mb-2">Inventory Link</label>
              <input
                type="url"
                name="inventoryLink"
                value={formData.inventoryLink}
                onChange={handleChange}
                placeholder="https://..."
                className="w-full px-4 py-3 border-2 border-slate-300 rounded-lg outline-none focus:border-blue-500"
              />
            </div>
          </div>



          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label className="block text-sm font-bold text-slate-900 mb-2">Listing Type *</label>
              <div className="flex flex-wrap gap-3 p-3 border-2 border-slate-300 rounded-lg bg-white">
                {LISTING_TYPES.map((type) => (
                  <label key={type.value} className="flex items-center gap-2 cursor-pointer">
                    <input
                      type="checkbox"
                      checked={formData.listingTypes.includes(type.value)}
                      onChange={() => handleListingTypeChange(type.value)}
                      className="w-4 h-4 text-blue-600 rounded"
                    />
                    <span className="text-sm font-medium text-slate-700">{type.label}</span>
                  </label>
                ))}
              </div>
            </div>
            <div>
              <label className="block text-sm font-bold text-slate-900 mb-2">Financing Types *</label>
              <div className="flex flex-wrap gap-3 p-3 border-2 border-slate-300 rounded-lg bg-white">
                {FINANCING_TYPES.map((type) => (
                  <label key={type.value} className="flex items-center gap-2 cursor-pointer">
                    <input type="checkbox" checked={formData.financingTypes.includes(type.value)} onChange={() => handleFinancingTypeChange(type.value)} className="w-4 h-4 text-blue-600 rounded" />
                    <span className="text-sm font-medium text-slate-700">{type.label}</span>
                  </label>
                ))}
              </div>
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label className="block text-sm font-bold text-slate-900 mb-2">Top Amenities</label>
              <div className="min-h-20 flex flex-wrap gap-3 p-3 border-2 border-slate-300 rounded-lg bg-white">
                {topAmenities.length > 0 ? (
                  topAmenities.map((amenity) => (
                    <label key={amenity.id} className="flex items-center gap-2 cursor-pointer rounded-full border border-slate-200 px-3 py-2">
                      <input type="checkbox" checked={formData.amenityIds.includes(amenity.id)} onChange={() => handleAmenityChange(amenity.id)} className="w-4 h-4 text-blue-600 rounded" />
                      <span className="text-sm font-medium text-slate-700">{amenity.name}</span>
                      <span className="text-xs font-semibold text-slate-400">{amenity.usageCount || 0}</span>
                    </label>
                  ))
                ) : (
                  <p className="text-sm font-medium text-slate-500">No amenities available yet.</p>
                )}
              </div>

              <label className="block text-sm font-bold text-slate-900 mb-2 mt-4">Find or Add Amenity</label>
              <div className="relative">
                <input
                  type="text"
                  value={amenitySearch}
                  onChange={(event) => setAmenitySearch(event.target.value)}
                  onKeyDown={(event) => {
                    if (event.key === 'Enter') {
                      event.preventDefault();
                      if (amenitySearchMatches.length > 0) {
                        addAmenity(amenitySearchMatches[0].id);
                      } else {
                        addCustomAmenity(amenitySearch);
                      }
                    }
                  }}
                  placeholder="Search amenities or type a new one"
                  className="w-full px-4 py-3 border-2 border-slate-300 rounded-lg outline-none focus:border-blue-500"
                />
                {amenitySearch.trim() && (
                  <div className="absolute left-0 right-0 z-20 mt-2 overflow-hidden rounded-lg border border-slate-200 bg-white shadow-lg">
                    {amenitySearchMatches.map((amenity) => (
                      <button
                        key={amenity.id}
                        type="button"
                        onClick={() => addAmenity(amenity.id)}
                        className="flex w-full items-center justify-between px-4 py-3 text-left text-sm font-semibold text-slate-700 hover:bg-slate-50"
                      >
                        <span>{amenity.name}</span>
                        <span className="text-xs text-slate-400">{amenity.usageCount || 0} uses</span>
                      </button>
                    ))}
                    {!searchedAmenityExists && (
                      <button
                        type="button"
                        onClick={() => addCustomAmenity(amenitySearch)}
                        className="w-full px-4 py-3 text-left text-sm font-semibold text-blue-700 hover:bg-blue-50"
                      >
                        Add "{amenitySearch.trim()}" as custom amenity
                      </button>
                    )}
                  </div>
                )}
              </div>

              {(formData.amenityIds.length > 0 || customAmenityNames.length > 0) && (
                <div className="mt-4 flex flex-wrap gap-2">
                  {formData.amenityIds.map((amenityId) => {
                    const amenity = availableAmenities.find((item) => item.id === amenityId);
                    return (
                      <button
                        key={amenityId}
                        type="button"
                        onClick={() => handleAmenityChange(amenityId)}
                        className="rounded-full bg-blue-50 px-3 py-1 text-sm font-semibold text-blue-700 hover:bg-blue-100"
                      >
                        {amenity?.name || 'Amenity'} x
                      </button>
                    );
                  })}
                  {customAmenityNames.map((amenityName) => (
                    <button
                      key={amenityName}
                      type="button"
                      onClick={() => removeCustomAmenity(amenityName)}
                      className="rounded-full bg-slate-100 px-3 py-1 text-sm font-semibold text-slate-700 hover:bg-slate-200"
                    >
                      {amenityName} x
                    </button>
                  ))}
                </div>
              )}
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
              <div className="flex items-center">
                <input type="checkbox" id="hasPromo" name="hasPromo" checked={formData.hasPromo} onChange={handleChange} className="w-5 h-5 text-blue-600 rounded cursor-pointer" />
                <label htmlFor="hasPromo" className="ml-3 text-sm font-medium text-slate-700">Ongoing Promo</label>
              </div>
              <div className="flex items-center">
                <input type="checkbox" id="featured" name="featured" checked={formData.featured} onChange={handleChange} className="w-5 h-5 text-blue-600 rounded cursor-pointer" />
                <label htmlFor="featured" className="ml-3 text-sm font-medium text-slate-700">Featured (Admin only)</label>
              </div>
              <div className="flex items-center">
                <input type="checkbox" id="visible" name="visible" checked={formData.visible} onChange={handleChange} className="w-5 h-5 text-blue-600 rounded cursor-pointer" />
                <label htmlFor="visible" className="ml-3 text-sm font-medium text-slate-700">Visible Publicly</label>
              </div>
            </div>
          </div>

          <div className="border-t-2 border-slate-200 pt-8">
            <h3 className="text-2xl font-bold text-slate-900 mb-6 flex items-center gap-2">
              <FiPlus className="text-blue-600" />
              Property Units (Inventory)
            </h3>

            <div className="bg-slate-50 p-6 rounded-lg mb-6 border border-slate-300">
              <h4 className="text-lg font-bold text-slate-800 mb-4">{editingUnitIndex !== null ? 'Edit Unit' : 'Add New Unit'}</h4>
              
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
              </div>

              <div className="flex gap-3">
                <button type="button" onClick={addUnit} className="px-5 py-2 bg-blue-600 text-white rounded-lg font-bold hover:bg-blue-700 transition-colors text-sm">
                  {editingUnitIndex !== null ? 'Update Unit' : 'Add to Inventory'}
                </button>
                {editingUnitIndex !== null && <button type="button" onClick={resetUnitForm} className="px-5 py-2 border-2 border-slate-300 text-slate-600 rounded-lg text-sm">Cancel</button>}
              </div>
            </div>

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
                        <td className="px-4 py-3 font-bold text-blue-700">PHP {parseFloat(unit.totalSellingPrice).toLocaleString()}</td>
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

        <div className="sticky bottom-0 bg-slate-50 px-10 py-6 flex justify-end gap-4 border-t border-slate-200 shadow-md">
          <button onClick={onClose} disabled={isLoading || isUploadingPhotos} className="px-8 py-3 border-2 border-slate-300 text-slate-900 rounded-lg font-bold hover:bg-slate-100 transition-colors">
            Cancel
          </button>
          <button onClick={handleSubmit} disabled={isLoading || isUploadingPhotos} className="px-8 py-3 bg-gradient-to-r from-blue-600 to-blue-700 text-white rounded-lg font-bold hover:from-blue-700 hover:to-blue-800 transition-all shadow-lg">
            {isUploadingPhotos ? 'Uploading Photos...' : isLoading ? 'Processing...' : 'Create Property'}
          </button>
        </div>
      </div>
    </div>
  );
}
