import React, { useEffect, useMemo, useState } from 'react';
import {
  FiCheckCircle,
  FiDollarSign,
  FiEdit2,
  FiExternalLink,
  FiFileText,
  FiHome,
  FiMapPin,
  FiTrash2,
  FiUser,
  FiX,
} from 'react-icons/fi';
import { useAuthContext } from '../../../shared/context/useAuthContext';
import { formatPrice, formatPriceRange, getDetailViewPermissions } from '../../../shared/utils/propertyHelpers';
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
  priceRangeMin: property.priceRangeMin ?? '',
  priceRangeMax: property.priceRangeMax ?? '',
  location: property.location || '',
  listingType: asList(property.listingType),
  petFriendly: !!property.petFriendly,
  parkingAvailable: !!property.parkingAvailable,
  turnoverDate: property.turnoverDate || '',
  amenities: property.amenities || '',
  keySellingPoints: property.keySellingPoints || '',
  brochurePdfUrl: property.brochurePdfUrl || '',
  inventoryLink: property.inventoryLink || '',
});

const toUpdatePayload = (formData) => ({
  ...formData,
  priceRangeMin: Number(formData.priceRangeMin),
  priceRangeMax: Number(formData.priceRangeMax),
});

const emptyUnitForm = {
  unitType: '',
  floorArea: '',
  lotArea: '',
  reservationFee: '',
  equityPeriodMonths: '',
  monthlyEquity: '',
  totalSellingPrice: '',
  financingTypes: [],
};

const toUnitForm = (unit = {}) => ({
  unitType: unit.unitType || '',
  floorArea: unit.floorArea ?? '',
  lotArea: unit.lotArea ?? '',
  reservationFee: unit.reservationFee ?? '',
  equityPeriodMonths: unit.equityPeriodMonths ?? '',
  monthlyEquity: unit.monthlyEquity ?? '',
  totalSellingPrice: unit.totalSellingPrice ?? '',
  financingTypes: asList(unit.financingTypes),
});

const toUnitPayload = (unitForm) => ({
  unitType: unitForm.unitType,
  floorArea: unitForm.floorArea === '' ? null : Number(unitForm.floorArea),
  lotArea: unitForm.lotArea === '' ? null : Number(unitForm.lotArea),
  reservationFee: Number(unitForm.reservationFee),
  equityPeriodMonths: Number(unitForm.equityPeriodMonths),
  monthlyEquity: Number(unitForm.monthlyEquity),
  totalSellingPrice: Number(unitForm.totalSellingPrice),
  financingTypes: unitForm.financingTypes,
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
        <table className="w-full min-w-[820px] text-left text-sm">
          <thead className="bg-slate-100 text-xs uppercase tracking-wide text-slate-700">
            <tr>
              <th className="px-4 py-3 font-bold">Unit Type</th>
              <th className="px-4 py-3 font-bold">Floor Area</th>
              <th className="px-4 py-3 font-bold">Lot Area</th>
              <th className="px-4 py-3 font-bold">Reservation Fee</th>
              <th className="px-4 py-3 font-bold">Equity Period</th>
              <th className="px-4 py-3 font-bold">Monthly Equity</th>
              <th className="px-4 py-3 font-bold">Total Price</th>
              <th className="px-4 py-3 font-bold">Financing</th>
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
                <td className="px-4 py-3">
                  <div className="flex flex-wrap gap-1">
                    {asList(unit.financingTypes).length > 0 ? (
                      asList(unit.financingTypes).map((type) => (
                        <span key={type} className="rounded-full bg-blue-100 px-2 py-1 text-xs font-semibold text-blue-800">
                          {type}
                        </span>
                      ))
                    ) : (
                      <span className="text-slate-500">N/A</span>
                    )}
                  </div>
                </td>
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
  const [editingUnitId, setEditingUnitId] = useState(null);
  const [isSavingUnit, setIsSavingUnit] = useState(false);
  const [error, setError] = useState(null);

  const role = user?.role;
  const permissions = useMemo(() => getDetailViewPermissions(role), [role]);
  const isAdmin = role === 'ADMIN';
  const isAdminOrAgent = isAdminOrAgentRole(role);
  const isRegisteredUser = isRegisteredUserRole(role);

  useEffect(() => {
    let mounted = true;

    const fetchDetailsForRole = async () => {
      if (!isOpen || !property?.id) return;

      setIsLoading(true);
      setError(null);
      setIsEditing(initialEdit && role === 'ADMIN');
      setDetailProperty(null);
      setUnitForm(emptyUnitForm);
      setEditingUnitId(null);

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
  const listingTypes = asList(currentProperty.listingType);
  const amenities = asList(currentProperty.amenities);

  const setField = (name, value) => {
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const toggleListingType = (type) => {
    setFormData((prev) => ({
      ...prev,
      listingType: prev.listingType.includes(type)
        ? prev.listingType.filter((item) => item !== type)
        : [...prev.listingType, type],
    }));
  };

  const handleSave = async () => {
    if (!isAdmin || !currentProperty.id) return;

    setIsSaving(true);
    setError(null);
    try {
      const updated = await propertyApi.updateProperty(currentProperty.id, toUpdatePayload(formData));
      setDetailProperty(updated);
      setFormData(toEditState(updated));
      setIsEditing(false);
      onPropertyUpdated?.(updated);
    } catch (err) {
      setError(err?.message || err || 'Failed to update property');
    } finally {
      setIsSaving(false);
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
    setUnitForm((prev) => ({
      ...prev,
      financingTypes: prev.financingTypes.includes(type)
        ? prev.financingTypes.filter((item) => item !== type)
        : [...prev.financingTypes, type],
    }));
  };

  const refreshAdminDetails = async () => {
    const refreshed = await propertyApi.getAdminPropertyById(currentProperty.id);
    setDetailProperty(refreshed);
    onPropertyUpdated?.(refreshed);
    return refreshed;
  };

  const handleEditUnit = (unit) => {
    setEditingUnitId(unit.id);
    setUnitForm(toUnitForm(unit));
  };

  const handleCancelUnitEdit = () => {
    setEditingUnitId(null);
    setUnitForm(emptyUnitForm);
  };

  const handleSaveUnit = async () => {
    if (!isAdmin || !currentProperty.id) return;

    setIsSavingUnit(true);
    setError(null);
    try {
      if (editingUnitId) {
        await propertyApi.updatePropertyUnit(currentProperty.id, editingUnitId, toUnitPayload(unitForm));
      } else {
        await propertyApi.createPropertyUnit(currentProperty.id, toUnitPayload(unitForm));
      }
      await refreshAdminDetails();
      handleCancelUnitEdit();
    } catch (err) {
      setError(err?.message || err || 'Failed to save property unit');
    } finally {
      setIsSavingUnit(false);
    }
  };

  const handleDeleteUnit = async (unitId) => {
    if (!isAdmin || !currentProperty.id || !unitId) return;
    if (!window.confirm('Delete this unit? This action cannot be undone.')) return;

    setIsSavingUnit(true);
    setError(null);
    try {
      await propertyApi.deletePropertyUnit(currentProperty.id, unitId);
      await refreshAdminDetails();
      if (editingUnitId === unitId) {
        handleCancelUnitEdit();
      }
    } catch (err) {
      setError(err?.message || err || 'Failed to delete property unit');
    } finally {
      setIsSavingUnit(false);
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
          <button
            onClick={onClose}
            className="rounded-lg p-2 text-white hover:bg-blue-900/60"
            aria-label="Close property details"
          >
            <FiX size={24} />
          </button>
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
                <FormField label="Minimum Price">
                  <input className={inputClass} type="number" value={formData.priceRangeMin} onChange={(event) => setField('priceRangeMin', event.target.value)} />
                </FormField>
                <FormField label="Maximum Price">
                  <input className={inputClass} type="number" value={formData.priceRangeMax} onChange={(event) => setField('priceRangeMax', event.target.value)} />
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
                    {['Pre-Selling', 'RFO', 'Rent-To-Own', 'Resale'].map((type) => (
                      <label key={type} className="flex items-center gap-2 text-sm font-medium text-slate-700">
                        <input
                          type="checkbox"
                          checked={formData.listingType.includes(type)}
                          onChange={() => toggleListingType(type)}
                          className="h-4 w-4 rounded border-slate-300 text-blue-600"
                        />
                        {type}
                      </label>
                    ))}
                  </div>
                </FormField>
                <FormField label="Amenities" span>
                  <textarea className={inputClass} rows={2} value={formData.amenities} onChange={(event) => setField('amenities', event.target.value)} />
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
                </div>
                </div>
              </div>

              <div className="rounded-lg border border-slate-200 bg-white p-5">
                <div className="mb-4">
                  <h3 className="text-base font-bold text-slate-900">Property Units</h3>
                  <p className="text-sm text-slate-500">Add, edit, or remove units using the unit management API.</p>
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
                  <FormField label="Financing Types" span>
                    <div className="flex flex-wrap gap-3 rounded-lg border border-slate-300 p-3">
                      {['Cash Only', 'Bank Financing', 'In-House'].map((type) => (
                        <label key={type} className="flex items-center gap-2 text-sm font-medium text-slate-700">
                          <input
                            type="checkbox"
                            checked={unitForm.financingTypes.includes(type)}
                            onChange={() => toggleFinancingType(type)}
                            className="h-4 w-4 rounded border-slate-300 text-blue-600"
                          />
                          {type}
                        </label>
                      ))}
                    </div>
                  </FormField>
                </div>

                <div className="mb-5 flex flex-wrap gap-3">
                  <button
                    onClick={handleSaveUnit}
                    disabled={isSavingUnit}
                    className="rounded-lg bg-blue-700 px-4 py-2 text-sm font-semibold text-white hover:bg-blue-800 disabled:opacity-60"
                  >
                    {isSavingUnit ? 'Saving Unit...' : editingUnitId ? 'Update Unit' : 'Add Unit'}
                  </button>
                  {editingUnitId && (
                    <button
                      onClick={handleCancelUnitEdit}
                      disabled={isSavingUnit}
                      className="rounded-lg border border-slate-300 px-4 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-50 disabled:opacity-60"
                    >
                      Cancel Unit Edit
                    </button>
                  )}
                </div>

                {(currentProperty.units || []).length > 0 ? (
                  <div className="space-y-3">
                    {(currentProperty.units || []).map((unit) => (
                      <div key={unit.id} className="flex flex-col gap-3 rounded-lg border border-slate-200 bg-slate-50 p-4 md:flex-row md:items-center md:justify-between">
                        <div>
                          <p className="font-bold text-slate-900">{unit.unitType || 'Unit'}</p>
                          <p className="text-sm text-slate-600">
                            {unit.floorArea ?? 'N/A'} sqm floor / {unit.lotArea ?? 'N/A'} sqm lot - {formatPrice(unit.totalSellingPrice)}
                          </p>
                        </div>
                        <div className="flex gap-2">
                          <button
                            onClick={() => handleEditUnit(unit)}
                            className="rounded-lg border border-blue-200 bg-blue-50 px-3 py-2 text-sm font-semibold text-blue-700 hover:bg-blue-100"
                          >
                            Edit Unit
                          </button>
                          <button
                            onClick={() => handleDeleteUnit(unit.id)}
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
                      value={listingTypes.length ? listingTypes.join(', ') : currentProperty.listingType}
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
                        <span key={amenity} className="rounded-full bg-slate-100 px-3 py-1 text-sm font-semibold text-slate-700">
                          {amenity}
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
                    onClick={() => setIsEditing(true)}
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
    </div>
  );
}
