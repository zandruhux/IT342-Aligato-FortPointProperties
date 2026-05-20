import { useEffect, useMemo, useState } from 'react';
import {
  FiCheckCircle,
  FiDollarSign,
  FiEdit2,
  FiExternalLink,
  FiFileText,
  FiHome,
  FiImage,
  FiMapPin,
  FiTrash2,
  FiUser,
  FiX,
} from 'react-icons/fi';
import { useAuthContext } from '../../../shared/context/useAuthContext';
import { financingTypeLabel, formatPrice, formatPriceRange, getDetailViewPermissions, listingTypeLabel, normalizeAmenities, normalizeListingTypes } from '../../../shared/utils/propertyHelpers';
import { FINANCING_TYPES, LISTING_TYPES } from '../../../shared/utils/constants';
import * as propertyApi from '../api/propertyApi';

const isRegisteredUserRole = (role) => role === 'registered_user' || role === 'USER';
const isAdminOrAgentRole = (role) => role === 'ADMIN' || role === 'AGENT';

const asList = (value) => {
  if (!value) return [];
  if (Array.isArray(value)) return value.filter(Boolean);
  return String(value)
    .split(',')
    .map((item) => item.trim())
    .filter(Boolean);
};

const formatBoolean = (value) => (value ? 'Yes' : 'No');

const toEditState = (property = {}) => ({
  name: property.name || '',
  basicDescription: property.basicDescription || '',
  developer: property.developer || '',
  location: property.location || '',
  listingTypes: normalizeListingTypes(property),
  financingTypes: asList(property.financingTypes),
  petFriendly: !!property.petFriendly,
  parkingAvailable: !!property.parkingAvailable,
  hasPromo: !!property.hasPromo,
  featured: !!property.featured,
  visible: property.visible !== false,
  turnoverDate: property.turnoverDate || '',
  amenityIds: normalizeAmenities(property).map((amenity) => amenity.id).filter(Boolean),
  customAmenities: '',
  keySellingPoints: property.keySellingPoints || '',
  brochurePdfUrl: property.brochurePdfUrl || '',
  inventoryLink: property.inventoryLink || '',
  photos: Array.isArray(property.photos)
    ? property.photos
      .map((photo, index) => ({
        photoUrl: photo.photoUrl || photo.url || photo,
        displayOrder: photo.displayOrder ?? index,
      }))
      .filter((photo) => photo.photoUrl)
    : [],
  units: Array.isArray(property.units)
    ? property.units.map((unit) => ({ ...unit }))
    : [],
});

const toUpdatePayload = (formData) => {
  return {
    ...formData,
    customAmenities: asList(formData.customAmenities),
    units: (formData.units || []).map((unit) => ({
      unitType: unit.unitType,
      floorArea: unit.floorArea === '' || unit.floorArea === null || unit.floorArea === undefined ? null : Number(unit.floorArea),
      lotArea: unit.lotArea === '' || unit.lotArea === null || unit.lotArea === undefined ? null : Number(unit.lotArea),
      reservationFee: Number(unit.reservationFee),
      equityPeriodMonths: Number(unit.equityPeriodMonths),
      monthlyEquity: Number(unit.monthlyEquity),
      totalSellingPrice: Number(unit.totalSellingPrice),
    })),
    photos: (formData.photos || []).map((photo, index) => ({
      photoUrl: photo.photoUrl,
      displayOrder: index,
    })),
  };
};

const withFreshCoverPhoto = (property = {}) => {
  const orderedPhotos = Array.isArray(property.photos)
    ? [...property.photos].sort((a, b) => (a.displayOrder ?? 0) - (b.displayOrder ?? 0))
    : [];

  return {
    ...property,
    photos: orderedPhotos,
    coverPhotoUrl: orderedPhotos[0]?.photoUrl || orderedPhotos[0]?.url || property.coverPhotoUrl,
  };
};

const emptyUnitForm = {
  unitType: '',
  floorArea: '',
  lotArea: '',
  reservationFee: '',
  equityPeriodMonths: '',
  monthlyEquity: '',
  totalSellingPrice: '',
};

const toUnitForm = (unit = {}) => ({
  unitType: unit.unitType || '',
  floorArea: unit.floorArea ?? '',
  lotArea: unit.lotArea ?? '',
  reservationFee: unit.reservationFee ?? '',
  equityPeriodMonths: unit.equityPeriodMonths ?? '',
  monthlyEquity: unit.monthlyEquity ?? '',
  totalSellingPrice: unit.totalSellingPrice ?? '',
});

const toUnitPayload = (unitForm) => ({
  unitType: unitForm.unitType,
  floorArea: unitForm.floorArea === '' ? null : Number(unitForm.floorArea),
  lotArea: unitForm.lotArea === '' ? null : Number(unitForm.lotArea),
  reservationFee: Number(unitForm.reservationFee),
  equityPeriodMonths: Number(unitForm.equityPeriodMonths),
  monthlyEquity: Number(unitForm.monthlyEquity),
  totalSellingPrice: Number(unitForm.totalSellingPrice),
});

const DetailItem = ({ icon: Icon, label, value }) => (
  <div className="rounded-lg border border-slate-200 bg-white p-4">
    <div className="flex items-start gap-3">
      {Icon && <Icon className="mt-1 shrink-0 text-blue-600" size={18} />}
      <div className="min-w-0">
        <p className="text-xs font-bold uppercase tracking-wide text-slate-500">{label}</p>
        <p className="mt-1 break-words text-sm font-semibold text-slate-900">{value || 'N/A'}</p>
      </div>
    </div>
  </div>
);

const TextSection = ({ title, children, subtle = false }) => {
  if (!children) return null;

  return (
    <section>
      <h3 className="mb-3 text-base font-bold text-slate-900">{title}</h3>
      <div className={`rounded-lg border p-4 text-sm leading-relaxed ${subtle ? 'border-blue-100 bg-blue-50 text-blue-950' : 'border-slate-200 bg-white text-slate-700'}`}>
        {children}
      </div>
    </section>
  );
};

const FormField = ({ label, children, span = false }) => (
  <label className={`block ${span ? 'md:col-span-2' : ''}`}>
    <span className="mb-1 block text-xs font-bold uppercase tracking-wide text-slate-600">{label}</span>
    {children}
  </label>
);

const inputClass = 'w-full rounded-lg border border-slate-300 px-3 py-2 text-sm font-medium text-slate-900 outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100';

const LinkField = ({ label, href }) => {
  if (!href) return null;

  return (
    <a
      href={href}
      target="_blank"
      rel="noopener noreferrer"
      className="flex items-center justify-between gap-3 rounded-lg border border-blue-200 bg-blue-50 p-4 text-sm font-semibold text-blue-700 hover:bg-blue-100"
    >
      <span className="min-w-0 break-all">
        <span className="block text-xs uppercase tracking-wide text-blue-900">{label}</span>
        {href}
      </span>
      <FiExternalLink className="shrink-0" size={18} />
    </a>
  );
};

const PhotoGallery = ({ photos = [], onPhotoClick }) => {
  const normalizedPhotos = photos
    .map((photo, index) => ({
      photoUrl: photo.photoUrl || photo.url || photo,
      displayOrder: photo.displayOrder ?? index,
    }))
    .filter((photo) => photo.photoUrl)
    .sort((a, b) => (a.displayOrder ?? 0) - (b.displayOrder ?? 0));

  if (!normalizedPhotos.length) {
    return (
      <section>
        <h3 className="mb-3 text-base font-bold text-slate-900">Property Photos</h3>
        <div className="rounded-lg border border-dashed border-slate-300 bg-slate-50 p-6 text-center text-sm font-medium text-slate-600">
          No property photos available.
        </div>
      </section>
    );
  }

  return (
    <section>
      <h3 className="mb-3 text-base font-bold text-slate-900">Property Photos</h3>
      <div className="grid grid-cols-1 gap-3 md:grid-cols-2 lg:grid-cols-3">
        {normalizedPhotos.map((photo, index) => (
          <div key={`${photo.photoUrl}-${index}`} className="overflow-hidden rounded-lg border border-slate-200 bg-white cursor-pointer hover:shadow-lg transition-shadow" onClick={() => onPhotoClick?.(photo.photoUrl)}>
            <div className="aspect-[4/3] bg-slate-100">
              <img
                src={photo.photoUrl}
                alt={`Property photo ${index + 1}`}
                className="h-full w-full object-cover"
                loading="lazy"
                decoding="async"
              />
            </div>
          </div>
        ))}
      </div>
    </section>
  );
};

const UnitsTable = ({ units = [] }) => {
  if (!units.length) {
    return (
      <section>
        <h3 className="mb-3 text-base font-bold text-slate-900">Property Units</h3>
        <div className="rounded-lg border border-dashed border-slate-300 bg-slate-50 p-6 text-center text-sm font-medium text-slate-600">
          No units available.
        </div>
      </section>
    );
  }

  return (
    <section>
      <h3 className="mb-3 text-base font-bold text-slate-900">Property Units ({units.length})</h3>
      <div className="overflow-x-auto rounded-lg border border-slate-200 bg-white">
        <table className="w-full min-w-[760px] text-left text-sm">
          <thead className="bg-slate-100 text-xs uppercase tracking-wide text-slate-700">
            <tr>
              <th className="px-4 py-3 font-bold">Unit Type</th>
              <th className="px-4 py-3 font-bold">Floor Area</th>
              <th className="px-4 py-3 font-bold">Lot Area</th>
              <th className="px-4 py-3 font-bold">Reservation Fee</th>
              <th className="px-4 py-3 font-bold">Equity Period</th>
              <th className="px-4 py-3 font-bold">Monthly Equity</th>
              <th className="px-4 py-3 font-bold">Total Price</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-200">
            {units.map((unit, index) => (
              <tr key={unit.id || index} className="align-top hover:bg-slate-50">
                <td className="px-4 py-3 font-semibold text-slate-900">{unit.unitType || 'N/A'}</td>
                <td className="px-4 py-3 text-slate-700">{unit.floorArea ?? 'N/A'} sqm</td>
                <td className="px-4 py-3 text-slate-700">{unit.lotArea ?? 'N/A'} sqm</td>
                <td className="px-4 py-3 text-slate-700">{formatPrice(unit.reservationFee)}</td>
                <td className="px-4 py-3 text-slate-700">
                  {unit.equityPeriodMonths !== null && unit.equityPeriodMonths !== undefined
                    ? `${unit.equityPeriodMonths} months`
                    : 'N/A'}
                </td>
                <td className="px-4 py-3 text-slate-700">{formatPrice(unit.monthlyEquity)}</td>
                <td className="px-4 py-3 font-semibold text-slate-900">{formatPrice(unit.totalSellingPrice)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  );
};

export function PropertyDetailModal({
  property,
  isOpen,
  onClose,
  onPropertyUpdated,
  onPropertyDeleted,
  initialEdit = false,
}) {
  const { user } = useAuthContext();
  const [detailProperty, setDetailProperty] = useState(null);
  const [formData, setFormData] = useState(toEditState());
  const [isLoading, setIsLoading] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [unitForm, setUnitForm] = useState(emptyUnitForm);
  const [editingUnitIndex, setEditingUnitIndex] = useState(null);
  const [photoFiles, setPhotoFiles] = useState([]);
  const [photoPreviewUrls, setPhotoPreviewUrls] = useState([]);
  const [availableAmenities, setAvailableAmenities] = useState([]);
  const [amenitySearch, setAmenitySearch] = useState('');
  const [fullscreenPhoto, setFullscreenPhoto] = useState(null);
  const [error, setError] = useState(null);

  const role = user?.role;
  const permissions = useMemo(() => getDetailViewPermissions(role), [role]);
  const isAdmin = role === 'ADMIN';
  const isAdminOrAgent = isAdminOrAgentRole(role);
  const isRegisteredUser = isRegisteredUserRole(role);
  const customAmenityNames = asList(formData.customAmenities);
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
  const searchedAmenityExists = availableAmenities.some(
    (amenity) => String(amenity.name || '').toLowerCase() === amenitySearch.trim().toLowerCase()
  ) || customAmenityNames.some(
    (amenity) => amenity.toLowerCase() === amenitySearch.trim().toLowerCase()
  );

  useEffect(() => {
    const urls = photoFiles.map((file) => URL.createObjectURL(file));
    setPhotoPreviewUrls(urls);

    return () => {
      urls.forEach((url) => URL.revokeObjectURL(url));
    };
  }, [photoFiles]);

  useEffect(() => {
    let mounted = true;

    const fetchDetailsForRole = async () => {
      if (!isOpen || !property?.id) return;

      setIsLoading(true);
      setError(null);
      setIsEditing(initialEdit && role === 'ADMIN');
      setDetailProperty(null);
      setUnitForm(emptyUnitForm);
      setEditingUnitIndex(null);
      setPhotoFiles([]);
      setAmenitySearch('');
      if (initialEdit && role === 'ADMIN') {
        await loadAmenities();
      }

      try {
        let response = null;

        if (isRegisteredUser) {
          response = await propertyApi.getUserPropertyDetails(property.id);
        } else if (role === 'AGENT') {
          response = await propertyApi.getAgentPropertyDetails(property.id);
        } else if (role === 'ADMIN') {
          response = await propertyApi.getAdminPropertyById(property.id);
        }

        if (mounted) {
          setDetailProperty(response);
          setFormData(toEditState(response || property));
        }
      } catch (err) {
        if (mounted) {
          setError(err?.message || err || 'Failed to load property details');
          setFormData(toEditState(property));
        }
      } finally {
        if (mounted) {
          setIsLoading(false);
        }
      }
    };

    fetchDetailsForRole();

    return () => {
      mounted = false;
    };
  }, [initialEdit, isOpen, isRegisteredUser, property, property?.id, role]);

  if (!isOpen || !property) return null;

  const currentProperty = detailProperty || property;
  const listingTypes = normalizeListingTypes(currentProperty);
  const amenities = normalizeAmenities(currentProperty);

  const setField = (name, value) => {
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const toggleListingType = (type) => {
    setFormData((prev) => ({
      ...prev,
      listingTypes: prev.listingTypes.includes(type)
        ? prev.listingTypes.filter((item) => item !== type)
        : [...prev.listingTypes, type],
    }));
  };

  const toggleAmenity = (amenityId) => {
    setFormData((prev) => ({
      ...prev,
      amenityIds: prev.amenityIds.includes(amenityId)
        ? prev.amenityIds.filter((id) => id !== amenityId)
        : [...prev.amenityIds, amenityId],
    }));
  };

  const addAmenity = (amenityId) => {
    setFormData((prev) => prev.amenityIds.includes(amenityId)
      ? prev
      : { ...prev, amenityIds: [...prev.amenityIds, amenityId] });
    setAmenitySearch('');
  };

  const addCustomAmenity = (amenityName) => {
    const normalizedName = amenityName.trim();
    if (!normalizedName) return;

    const alreadyExists = availableAmenities.some(
      (amenity) => String(amenity.name || '').toLowerCase() === normalizedName.toLowerCase()
    ) || asList(formData.customAmenities).some(
      (amenity) => amenity.toLowerCase() === normalizedName.toLowerCase()
    );

    if (alreadyExists) {
      setAmenitySearch('');
      return;
    }

    setFormData((prev) => ({
      ...prev,
      customAmenities: [...asList(prev.customAmenities), normalizedName],
    }));
    setAmenitySearch('');
  };

  const removeCustomAmenity = (amenityName) => {
    setFormData((prev) => ({
      ...prev,
      customAmenities: asList(prev.customAmenities).filter((item) => item !== amenityName),
    }));
  };

  const getSelectedAmenityName = (amenityId) => {
    const amenity = availableAmenities.find((item) => item.id === amenityId)
      || amenities.find((item) => item.id === amenityId);
    return amenity?.name || 'Amenity';
  };

  const loadAmenities = async () => {
    try {
      const amenities = await propertyApi.getAmenities(false);
      setAvailableAmenities(amenities);
    } catch {
      setAvailableAmenities([]);
    }
  };

  const handleSave = async () => {
    if (!isAdmin || !currentProperty.id) return;

    setIsSaving(true);
    setError(null);
    try {
      const uploadedPhotos = await uploadQueuedPhotos();
      const payload = toUpdatePayload({
        ...formData,
        photos: [...formData.photos, ...uploadedPhotos],
      });
      const updated = await propertyApi.updateProperty(currentProperty.id, payload);
      const updatedWithCover = withFreshCoverPhoto(updated);
      setDetailProperty(updatedWithCover);
      setFormData(toEditState(updatedWithCover));
      setPhotoFiles([]);
      setIsEditing(false);
      onPropertyUpdated?.(updatedWithCover);
    } catch (err) {
      setError(err?.message || err || 'Failed to update property');
    } finally {
      setIsSaving(false);
    }
  };

  const handleStartEdit = async () => {
    setFormData(toEditState(currentProperty));
    setIsEditing(true);
    if (!availableAmenities.length) {
      await loadAmenities();
    }
  };

  const handleDelete = async () => {
    if (!isAdmin || !currentProperty.id) return;
    if (!window.confirm('Delete this property? This action cannot be undone.')) return;

    setIsDeleting(true);
    setError(null);
    try {
      await propertyApi.deleteProperty(currentProperty.id);
      onPropertyDeleted?.(currentProperty.id);
      onClose();
    } catch (err) {
      setError(err?.message || err || 'Failed to delete property');
    } finally {
      setIsDeleting(false);
    }
  };

  const setUnitField = (name, value) => {
    setUnitForm((prev) => ({ ...prev, [name]: value }));
  };

  const toggleFinancingType = (type) => {
    setFormData((prev) => ({
      ...prev,
      financingTypes: prev.financingTypes.includes(type)
        ? prev.financingTypes.filter((item) => item !== type)
        : [...prev.financingTypes, type],
    }));
  };

  const handlePhotoFileChange = (event) => {
    setPhotoFiles(Array.from(event.target.files || []));
  };

  const removeExistingPhoto = (photoIndex) => {
    setFormData((prev) => ({
      ...prev,
      photos: prev.photos.filter((_, index) => index !== photoIndex),
    }));
  };

  const moveExistingPhoto = (photoIndex, direction) => {
    setFormData((prev) => {
      const photos = [...prev.photos];
      const targetIndex = photoIndex + direction;
      if (targetIndex < 0 || targetIndex >= photos.length) {
        return prev;
      }
      [photos[photoIndex], photos[targetIndex]] = [photos[targetIndex], photos[photoIndex]];
      return { ...prev, photos };
    });
  };

  const uploadQueuedPhotos = async () => {
    if (!photoFiles.length) {
      return [];
    }

    const startIndex = formData.photos.length;
    const uploadedPhotos = [];
    for (const [index, file] of photoFiles.entries()) {
      uploadedPhotos.push(await propertyApi.uploadPropertyPhoto(file, startIndex + index));
    }
    return uploadedPhotos;
  };

  const handleEditUnit = (unit, index) => {
    setEditingUnitIndex(index);
    setUnitForm(toUnitForm(unit));
  };

  const handleCancelUnitEdit = () => {
    setEditingUnitIndex(null);
    setUnitForm(emptyUnitForm);
  };

  const handleSaveUnit = () => {
    const unitPayload = toUnitPayload(unitForm);
    if (!unitPayload.unitType || !unitPayload.totalSellingPrice) return;

    setFormData((prev) => ({
      ...prev,
      units: editingUnitIndex !== null
        ? prev.units.map((unit, index) => (
          index === editingUnitIndex ? { ...unit, ...unitPayload } : unit
        ))
        : [...prev.units, unitPayload],
    }));
    handleCancelUnitEdit();
  };

  const handleDeleteUnit = (unitIndex) => {
    setFormData((prev) => ({
      ...prev,
      units: prev.units.filter((_, index) => index !== unitIndex),
    }));
    if (editingUnitIndex === unitIndex) {
      handleCancelUnitEdit();
    }
  };

  const handleBackdropClick = (event) => {
    if (event.target === event.currentTarget) {
      onClose();
    }
  };

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/50 p-4 backdrop-blur-sm"
      onClick={handleBackdropClick}
    >
      <div className="flex max-h-[90vh] w-full max-w-6xl flex-col overflow-hidden rounded-lg bg-slate-50 shadow-2xl">
        <div className="flex items-start justify-between gap-4 border-b border-blue-900/20 bg-blue-800 px-6 py-5 text-white">
          <div className="min-w-0">
            <p className="text-xs font-bold uppercase tracking-wide text-blue-100">
              {isAdmin ? 'Admin Property Details' : role === 'AGENT' ? 'Agent Property Details' : 'Property Details'}
            </p>
            <h2 className="mt-1 break-words text-2xl font-bold">{currentProperty.name || 'Property Details'}</h2>
            {isAdminOrAgent && currentProperty.developer && (
              <p className="mt-1 text-sm font-medium text-blue-100">{currentProperty.developer}</p>
            )}
          </div>
          <div className="flex items-center gap-3">
            {currentProperty.hasPromo && !isEditing && (
              <div className="rounded-lg bg-yellow-500 px-3 py-1 text-xs font-bold uppercase tracking-wide text-white">
                ONGOING PROMO!
              </div>
            )}
            <button
              onClick={onClose}
              className="rounded-lg p-2 text-white hover:bg-blue-900/60"
              aria-label="Close property details"
            >
              <FiX size={24} />
            </button>
          </div>
        </div>

        <div className="overflow-y-auto px-6 py-6">
          {isLoading && (
            <div className="mb-6 rounded-lg border border-blue-200 bg-blue-50 p-4 text-sm font-semibold text-blue-800">
              Loading full property details...
            </div>
          )}

          {error && (
            <div className="mb-6 rounded-lg border border-red-200 bg-red-50 p-4 text-sm font-semibold text-red-700">
              {error}
            </div>
          )}

          {isEditing ? (
            <div className="space-y-6">
              <div className="rounded-lg border border-slate-200 bg-white p-5">
                <div className="mb-4 flex items-center gap-2">
                  <FiImage className="text-blue-600" size={18} />
                  <div>
                    <h3 className="text-base font-bold text-slate-900">Property Photos</h3>
                    <p className="text-sm text-slate-500">The first photo is used as the card cover.</p>
                  </div>
                </div>

                {/* Existing Photos */}
                {formData.photos.length > 0 && (
                  <div>
                    <p className="text-xs font-bold uppercase tracking-wide text-slate-600 mb-3">Existing Photos ({formData.photos.length})</p>
                    <div className="mb-5 grid grid-cols-1 gap-3 md:grid-cols-2 lg:grid-cols-3">
                      {formData.photos.map((photo, index) => (
                        <div key={`${photo.photoUrl}-${index}`} className="overflow-hidden rounded-lg border border-slate-200 bg-slate-50">
                          <div className="aspect-[4/3] bg-slate-100 cursor-pointer hover:opacity-75 transition-opacity" onClick={() => setFullscreenPhoto(photo.photoUrl)}>
                          <img
                            src={photo.photoUrl}
                            alt={`Property photo ${index + 1}`}
                            className="h-full w-full object-cover"
                            loading="lazy"
                            decoding="async"
                          />
                          </div>
                          <div className="space-y-3 p-3">
                            <div className="flex items-center justify-between gap-2">
                              <span className="text-xs font-bold uppercase tracking-wide text-slate-600">
                                {index === 0 ? 'Cover Photo' : `Photo ${index + 1}`}
                              </span>
                              <button
                                type="button"
                                onClick={() => removeExistingPhoto(index)}
                                className="rounded border border-red-200 bg-red-50 px-2 py-1 text-xs font-semibold text-red-700 hover:bg-red-100"
                              >
                                Remove
                              </button>
                            </div>
                            <div className="flex gap-2">
                              <button
                                type="button"
                                onClick={() => moveExistingPhoto(index, -1)}
                                disabled={index === 0}
                                className="flex-1 rounded border border-slate-300 px-2 py-1 text-xs font-semibold text-slate-700 hover:bg-white disabled:opacity-40"
                              >
                                ↑ Move Up
                              </button>
                              <button
                                type="button"
                                onClick={() => moveExistingPhoto(index, 1)}
                                disabled={index === formData.photos.length - 1}
                                className="flex-1 rounded border border-slate-300 px-2 py-1 text-xs font-semibold text-slate-700 hover:bg-white disabled:opacity-40"
                              >
                                ↓ Move Down
                              </button>
                            </div>
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>
                )}

                {/* New Photos Preview */}
                {photoFiles.length > 0 && (
                  <div>
                    <p className="text-xs font-bold uppercase tracking-wide text-slate-600 mb-3 text-blue-700">New Photos to Upload ({photoFiles.length})</p>
                    <div className="mb-5 grid grid-cols-1 gap-4 md:grid-cols-2">
                      {photoFiles.map((file, index) => (
                        <div key={`${file.name}-${index}`} className="overflow-hidden rounded-lg border border-blue-300 bg-blue-50">
                          <div className="aspect-[16/9] bg-slate-100 cursor-pointer hover:opacity-75 transition-opacity" onClick={() => setFullscreenPhoto(photoPreviewUrls[index])}>
                            <img
                              src={photoPreviewUrls[index]}
                              alt={`New photo: ${file.name}`}
                              className="h-full w-full object-cover"
                              loading="lazy"
                              decoding="async"
                            />
                          </div>
                          <div className="space-y-2 p-3">
                            <div className="flex items-center justify-between gap-2">
                              <span className="text-xs font-bold uppercase tracking-wide text-blue-700 truncate">{file.name}</span>
                            </div>
                            <p className="text-xs text-slate-600">{(file.size / 1024 / 1024).toFixed(2)}MB</p>
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>
                )}

                {formData.photos.length === 0 && photoFiles.length === 0 && (
                  <div className="mb-5 rounded-lg border border-dashed border-slate-300 bg-slate-50 p-5 text-center text-sm font-medium text-slate-600">
                    No photos yet. Add some to get started!
                  </div>
                )}

                <label className="inline-flex cursor-pointer items-center gap-2 rounded-lg border border-slate-300 bg-white px-4 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-50">
                  <FiImage size={16} />
                  Add Photos
                  <input
                    type="file"
                    accept="image/png,image/jpeg,image/webp"
                    multiple
                    onChange={handlePhotoFileChange}
                    className="hidden"
                  />
                </label>
              </div>

              <div className="rounded-lg border border-slate-200 bg-white p-5">
                <div className="mb-4">
                  <h3 className="text-base font-bold text-slate-900">Property Information</h3>
                  <p className="text-sm text-slate-500">Update the main property fields.</p>
                </div>
                <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
                <FormField label="Property Name">
                  <input className={inputClass} value={formData.name} onChange={(event) => setField('name', event.target.value)} />
                </FormField>
                <FormField label="Developer">
                  <input className={inputClass} value={formData.developer} onChange={(event) => setField('developer', event.target.value)} />
                </FormField>
                <FormField label="Location">
                  <input className={inputClass} value={formData.location} onChange={(event) => setField('location', event.target.value)} />
                </FormField>
                <FormField label="Turnover Date">
                  <input className={inputClass} value={formData.turnoverDate} onChange={(event) => setField('turnoverDate', event.target.value)} />
                </FormField>
                <FormField label="Basic Description" span>
                  <textarea className={inputClass} rows={3} value={formData.basicDescription} onChange={(event) => setField('basicDescription', event.target.value)} />
                </FormField>
                <FormField label="Listing Type" span>
                  <div className="flex flex-wrap gap-3 rounded-lg border border-slate-300 p-3">
                    {LISTING_TYPES.map((type) => (
                      <label key={type.value} className="flex items-center gap-2 text-sm font-medium text-slate-700">
                        <input
                          type="checkbox"
                          checked={formData.listingTypes.includes(type.value)}
                          onChange={() => toggleListingType(type.value)}
                          className="h-4 w-4 rounded border-slate-300 text-blue-600"
                        />
                        {type.label}
                      </label>
                    ))}
                  </div>
                </FormField>
                <FormField label="Financing Types" span>
                  <div className="flex flex-wrap gap-3 rounded-lg border border-slate-300 p-3">
                    {FINANCING_TYPES.map((type) => (
                      <label key={type.value} className="flex items-center gap-2 text-sm font-medium text-slate-700">
                        <input type="checkbox" checked={formData.financingTypes.includes(type.value)} onChange={() => toggleFinancingType(type.value)} className="h-4 w-4 rounded border-slate-300 text-blue-600" />
                        {type.label}
                      </label>
                    ))}
                  </div>
                </FormField>
                <FormField label="Amenities" span>
                  <div className="space-y-4">
                    <div className="rounded-lg border border-slate-300 p-3">
                      <p className="mb-3 text-xs font-bold uppercase tracking-wide text-slate-600">Top amenities</p>
                      {topAmenities.length > 0 ? (
                        <div className="flex flex-wrap gap-3">
                          {topAmenities.map((amenity) => (
                            <label key={amenity.id} className="flex items-center gap-2 rounded-full border border-slate-200 px-3 py-2 text-sm font-medium text-slate-700">
                              <input
                                type="checkbox"
                                checked={formData.amenityIds.includes(amenity.id)}
                                onChange={() => toggleAmenity(amenity.id)}
                                className="h-4 w-4 rounded border-slate-300 text-blue-600"
                              />
                              <span>{amenity.name}</span>
                              <span className="text-xs font-semibold text-slate-400">{amenity.usageCount || 0}</span>
                            </label>
                          ))}
                        </div>
                      ) : (
                        <p className="text-sm text-slate-600">No amenities available.</p>
                      )}
                    </div>

                    <div className="relative">
                      <input
                        className={inputClass}
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
                      <div className="flex flex-wrap gap-2">
                        {formData.amenityIds.map((amenityId) => {
                          return (
                            <button
                              key={amenityId}
                              type="button"
                              onClick={() => toggleAmenity(amenityId)}
                              className="rounded-full bg-blue-50 px-3 py-1 text-sm font-semibold text-blue-700 hover:bg-blue-100"
                            >
                              {getSelectedAmenityName(amenityId)} x
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
                </FormField>
                <FormField label="Key Selling Points" span>
                  <textarea className={inputClass} rows={3} value={formData.keySellingPoints} onChange={(event) => setField('keySellingPoints', event.target.value)} />
                </FormField>
                <FormField label="Brochure PDF URL">
                  <input className={inputClass} value={formData.brochurePdfUrl} onChange={(event) => setField('brochurePdfUrl', event.target.value)} />
                </FormField>
                <FormField label="Inventory Link">
                  <input className={inputClass} value={formData.inventoryLink} onChange={(event) => setField('inventoryLink', event.target.value)} />
                </FormField>
                <div className="flex flex-wrap gap-6 md:col-span-2">
                  <label className="flex items-center gap-2 text-sm font-semibold text-slate-700">
                    <input type="checkbox" checked={formData.petFriendly} onChange={(event) => setField('petFriendly', event.target.checked)} />
                    Pet Friendly
                  </label>
                  <label className="flex items-center gap-2 text-sm font-semibold text-slate-700">
                    <input type="checkbox" checked={formData.parkingAvailable} onChange={(event) => setField('parkingAvailable', event.target.checked)} />
                    Parking Available
                  </label>
                  <label className="flex items-center gap-2 text-sm font-semibold text-slate-700">
                    <input type="checkbox" checked={formData.hasPromo} onChange={(event) => setField('hasPromo', event.target.checked)} />
                    Ongoing Promo
                  </label>
                  <label className="flex items-center gap-2 text-sm font-semibold text-slate-700">
                    <input type="checkbox" checked={formData.featured} onChange={(event) => setField('featured', event.target.checked)} />
                    Featured
                  </label>
                  <label className="flex items-center gap-2 text-sm font-semibold text-slate-700">
                    <input type="checkbox" checked={formData.visible} onChange={(event) => setField('visible', event.target.checked)} />
                    Visible
                  </label>
                </div>
                </div>
              </div>

              <div className="rounded-lg border border-slate-200 bg-white p-5">
                <div className="mb-4">
                  <h3 className="text-base font-bold text-slate-900">Property Units</h3>
                  <p className="text-sm text-slate-500">Add, edit, or remove units locally. They are saved with the property form.</p>
                </div>

                <div className="mb-5 grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3">
                  <FormField label="Unit Type">
                    <input className={inputClass} value={unitForm.unitType} onChange={(event) => setUnitField('unitType', event.target.value)} placeholder="Studio, 1BR, 2BR" />
                  </FormField>
                  <FormField label="Floor Area">
                    <input className={inputClass} type="number" value={unitForm.floorArea} onChange={(event) => setUnitField('floorArea', event.target.value)} />
                  </FormField>
                  <FormField label="Lot Area">
                    <input className={inputClass} type="number" value={unitForm.lotArea} onChange={(event) => setUnitField('lotArea', event.target.value)} />
                  </FormField>
                  <FormField label="Reservation Fee">
                    <input className={inputClass} type="number" value={unitForm.reservationFee} onChange={(event) => setUnitField('reservationFee', event.target.value)} />
                  </FormField>
                  <FormField label="Equity Period Months">
                    <input className={inputClass} type="number" value={unitForm.equityPeriodMonths} onChange={(event) => setUnitField('equityPeriodMonths', event.target.value)} />
                  </FormField>
                  <FormField label="Monthly Equity">
                    <input className={inputClass} type="number" value={unitForm.monthlyEquity} onChange={(event) => setUnitField('monthlyEquity', event.target.value)} />
                  </FormField>
                  <FormField label="Total Selling Price">
                    <input className={inputClass} type="number" value={unitForm.totalSellingPrice} onChange={(event) => setUnitField('totalSellingPrice', event.target.value)} />
                  </FormField>
                </div>

                <div className="mb-5 flex flex-wrap gap-3">
                  <button
                    onClick={handleSaveUnit}
                    className="rounded-lg bg-blue-700 px-4 py-2 text-sm font-semibold text-white hover:bg-blue-800 disabled:opacity-60"
                  >
                    {editingUnitIndex !== null ? 'Update Unit' : 'Add Unit'}
                  </button>
                  {editingUnitIndex !== null && (
                    <button
                      onClick={handleCancelUnitEdit}
                      className="rounded-lg border border-slate-300 px-4 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-50 disabled:opacity-60"
                    >
                      Cancel Unit Edit
                    </button>
                  )}
                </div>

                {(formData.units || []).length > 0 ? (
                  <div className="space-y-3">
                    {(formData.units || []).map((unit, index) => (
                      <div key={unit.id || `${unit.unitType}-${index}`} className="flex flex-col gap-3 rounded-lg border border-slate-200 bg-slate-50 p-4 md:flex-row md:items-center md:justify-between">
                        <div>
                          <p className="font-bold text-slate-900">{unit.unitType || 'Unit'}</p>
                          <p className="text-sm text-slate-600">
                            {unit.floorArea ?? 'N/A'} sqm floor / {unit.lotArea ?? 'N/A'} sqm lot - {formatPrice(unit.totalSellingPrice)}
                          </p>
                        </div>
                        <div className="flex gap-2">
                          <button
                            onClick={() => handleEditUnit(unit, index)}
                            className="rounded-lg border border-blue-200 bg-blue-50 px-3 py-2 text-sm font-semibold text-blue-700 hover:bg-blue-100"
                          >
                            Edit Unit
                          </button>
                          <button
                            onClick={() => handleDeleteUnit(index)}
                            className="rounded-lg border border-red-200 bg-red-50 px-3 py-2 text-sm font-semibold text-red-700 hover:bg-red-100"
                          >
                            Delete Unit
                          </button>
                        </div>
                      </div>
                    ))}
                  </div>
                ) : (
                  <div className="rounded-lg border border-dashed border-slate-300 bg-slate-50 p-5 text-center text-sm font-medium text-slate-600">
                    No units yet.
                  </div>
                )}
              </div>
            </div>
          ) : (
            <div className="space-y-7">
              <PhotoGallery photos={currentProperty.photos || []} onPhotoClick={setFullscreenPhoto} />

              <section className="rounded-lg border border-slate-200 bg-white p-5">
                <h3 className="mb-4 flex items-center gap-2 text-base font-bold text-slate-900">
                  <FiHome className="text-blue-600" />
                  Main Details
                </h3>
                <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3">
                  {permissions.location && <DetailItem icon={FiMapPin} label="Location" value={currentProperty.location} />}
                  {permissions.priceRangeMin && permissions.priceRangeMax && (
                    <DetailItem
                      icon={FiDollarSign}
                      label="Price Range"
                      value={formatPriceRange(currentProperty.priceRangeMin, currentProperty.priceRangeMax)}
                    />
                  )}
                  {permissions.listingType && (
                    <DetailItem
                      icon={FiFileText}
                      label="Listing Type"
                      value={listingTypes.length ? listingTypes.map(listingTypeLabel).join(', ') : currentProperty.listingType}
                    />
                  )}
                  {permissions.financingTypes && (
                    <DetailItem
                      icon={FiDollarSign}
                      label="Financing"
                      value={asList(currentProperty.financingTypes).map(financingTypeLabel).join(', ')}
                    />
                  )}
                  {permissions.turnoverDate && <DetailItem icon={FiFileText} label="Turnover Date" value={currentProperty.turnoverDate} />}
                  {permissions.petFriendly && <DetailItem icon={FiCheckCircle} label="Pet Friendly" value={formatBoolean(currentProperty.petFriendly)} />}
                  {permissions.parkingAvailable && <DetailItem icon={FiCheckCircle} label="Parking Available" value={formatBoolean(currentProperty.parkingAvailable)} />}
                  {permissions.createdBy && <DetailItem icon={FiUser} label="Created By" value={currentProperty.createdBy} />}
                </div>
              </section>

              {permissions.basicDescription && (
                <TextSection title="Basic Description">{currentProperty.basicDescription}</TextSection>
              )}

              {permissions.amenities && (
                <section>
                  <h3 className="mb-3 text-base font-bold text-slate-900">Amenities</h3>
                  {amenities.length > 0 ? (
                    <div className="flex flex-wrap gap-2 rounded-lg border border-slate-200 bg-white p-4">
                      {amenities.map((amenity) => (
                        <span key={amenity.id || amenity.name} className="rounded-full bg-slate-100 px-3 py-1 text-sm font-semibold text-slate-700">
                          {amenity.name}
                        </span>
                      ))}
                    </div>
                  ) : (
                    <div className="rounded-lg border border-slate-200 bg-white p-4 text-sm font-medium text-slate-600">
                      N/A
                    </div>
                  )}
                </section>
              )}

              {permissions.keySellingPoints && (
                <TextSection title="Key Selling Points" subtle>{currentProperty.keySellingPoints}</TextSection>
              )}

              {(permissions.brochurePdfUrl || permissions.inventoryLink) && (
                <section>
                  <h3 className="mb-3 text-base font-bold text-slate-900">Resources</h3>
                  <div className="grid grid-cols-1 gap-3 md:grid-cols-2">
                    {permissions.brochurePdfUrl && <LinkField label="Brochure PDF URL" href={currentProperty.brochurePdfUrl} />}
                    {permissions.inventoryLink && <LinkField label="Inventory Link" href={currentProperty.inventoryLink} />}
                    {!currentProperty.brochurePdfUrl && !currentProperty.inventoryLink && (
                      <div className="rounded-lg border border-slate-200 bg-white p-4 text-sm font-medium text-slate-600 md:col-span-2">
                        No resources available.
                      </div>
                    )}
                  </div>
                </section>
              )}

              {permissions.units && <UnitsTable units={currentProperty.units || []} />}
            </div>
          )}
        </div>

        <div className="flex flex-col gap-3 border-t border-slate-200 bg-white px-6 py-4 sm:flex-row sm:items-center sm:justify-between">
          {isAdmin ? (
            <div className="flex gap-3">
              {isEditing ? (
                <>
                  <button
                    onClick={handleSave}
                    disabled={isSaving}
                    className="rounded-lg bg-blue-700 px-5 py-2 text-sm font-semibold text-white hover:bg-blue-800 disabled:opacity-60"
                  >
                    {isSaving ? 'Saving...' : 'Save Changes'}
                  </button>
                  <button
                    onClick={() => {
                      setIsEditing(false);
                      setFormData(toEditState(currentProperty));
                    }}
                    disabled={isSaving}
                    className="rounded-lg border border-slate-300 px-5 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-50 disabled:opacity-60"
                  >
                    Cancel
                  </button>
                </>
              ) : (
                <>
                  <button
                    onClick={handleStartEdit}
                    className="flex items-center gap-2 rounded-lg border border-blue-200 bg-blue-50 px-5 py-2 text-sm font-semibold text-blue-700 hover:bg-blue-100"
                  >
                    <FiEdit2 size={16} />
                    Edit
                  </button>
                  <button
                    onClick={handleDelete}
                    disabled={isDeleting}
                    className="flex items-center gap-2 rounded-lg border border-red-200 bg-red-50 px-5 py-2 text-sm font-semibold text-red-700 hover:bg-red-100 disabled:opacity-60"
                  >
                    <FiTrash2 size={16} />
                    {isDeleting ? 'Deleting...' : 'Delete'}
                  </button>
                </>
              )}
            </div>
          ) : (
            <div />
          )}
          <button
            onClick={onClose}
            className="rounded-lg bg-slate-800 px-5 py-2 text-sm font-semibold text-white hover:bg-slate-900"
          >
            Close
          </button>
        </div>
      </div>
      {fullscreenPhoto && (
        <div
          className="fixed inset-0 z-50 flex items-center justify-center bg-black/90 p-4 backdrop-blur-sm"
          onClick={() => setFullscreenPhoto(null)}
        >
          <button
            onClick={() => setFullscreenPhoto(null)}
            className="absolute right-6 top-6 rounded-lg bg-white/20 p-2 text-white hover:bg-white/30"
            aria-label="Close fullscreen photo"
          >
            <FiX size={32} />
          </button>
          <img
            src={fullscreenPhoto}
            alt="Fullscreen property photo"
            className="max-h-[90vh] max-w-[90vw] object-contain"
            onClick={(e) => e.stopPropagation()}
          />
        </div>
      )}
    </div>
  );
}
