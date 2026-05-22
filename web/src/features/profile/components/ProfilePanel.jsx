import { useEffect, useRef, useState } from 'react';
import { FiEdit2, FiMail, FiPhone, FiSave, FiUser, FiX } from 'react-icons/fi';
import { IMAGE_LIMITS, validateImageFile } from '../../../shared/utils/fileValidation';
import ProfileImageUploader from './ProfileImageUploader';

const inputClass = 'w-full rounded-lg border border-slate-300 px-3 py-2 text-sm font-semibold text-slate-900 outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 disabled:bg-slate-50 disabled:text-slate-700';

const getDisplayRole = (role) => {
  if (role === 'registered_user' || role === 'REGISTERED_USER') {
    return 'USER';
  }
  return role || 'USER';
};

const getName = (profile) => {
  return profile?.name || `${profile?.firstname || ''} ${profile?.lastname || ''}`.trim() || 'User';
};

const toForm = (profile = {}) => ({
  firstname: profile.firstname || '',
  lastname: profile.lastname || '',
  email: profile.email || '',
  phoneNumber: profile.phoneNumber || '',
});

export default function ProfilePanel({
  profile,
  loading,
  error,
  accentClass = 'bg-blue-600',
  accentTextClass = 'text-blue-600',
  onUpload,
  onRemove,
  onUpdate,
}) {
  const [isEditing, setIsEditing] = useState(false);
  const [form, setForm] = useState(toForm(profile));
  const [message, setMessage] = useState('');
  const [imageFile, setImageFile] = useState(null);
  const [imagePreviewUrl, setImagePreviewUrl] = useState('');
  const [removeImage, setRemoveImage] = useState(false);
  const lastProfileIdRef = useRef(profile?.id || null);
  const isEditingRef = useRef(false);
  const isDirtyRef = useRef(false);

  useEffect(() => {
    isEditingRef.current = isEditing;
  }, [isEditing]);

  useEffect(() => {
    const nextProfileId = profile?.id || null;
    const idChanged = nextProfileId && nextProfileId !== lastProfileIdRef.current;

    if (idChanged) {
      lastProfileIdRef.current = nextProfileId;
    }

    if (isEditingRef.current || isDirtyRef.current) {
      return;
    }

    setForm(toForm(profile));
    setMessage('');
  }, [profile]);

  if (!profile) {
    return (
      <div className="rounded-lg border border-slate-200 bg-white p-12 text-center shadow-sm">
        <p className="font-semibold text-slate-700">Unable to load profile information</p>
      </div>
    );
  }

  const setField = (field, value) => {
    isDirtyRef.current = true;
    setForm((current) => ({ ...current, [field]: value }));
  };

  const handleCancel = () => {
    setForm(toForm(profile));
    setIsEditing(false);
    setMessage('');
    if (imagePreviewUrl) {
      URL.revokeObjectURL(imagePreviewUrl);
    }
    setImageFile(null);
    setImagePreviewUrl('');
    setRemoveImage(false);
    isDirtyRef.current = false;
  };

  const handleSelectImage = (file) => {
    try {
      validateImageFile(file, {
        maxSizeBytes: IMAGE_LIMITS.PROFILE,
        requiredMessage: 'Profile image is required',
        sizeMessage: 'Profile image size exceeds maximum limit',
        typeMessage: 'Invalid profile image type',
      });
    } catch (err) {
      setMessage(err.message || 'Invalid profile image');
      return;
    }

    if (imagePreviewUrl) {
      URL.revokeObjectURL(imagePreviewUrl);
    }
    setMessage('');
    setImageFile(file);
    setImagePreviewUrl(URL.createObjectURL(file));
    setRemoveImage(false);
    isDirtyRef.current = true;
  };

  const handleMarkRemoveImage = () => {
    if (imagePreviewUrl) {
      URL.revokeObjectURL(imagePreviewUrl);
    }
    setImageFile(null);
    setImagePreviewUrl('');
    setRemoveImage(true);
    isDirtyRef.current = true;
  };

  const handleStartEdit = () => {
    setMessage('');
    setIsEditing(true);
  };

  const handleSave = async () => {
    setMessage('');
    try {
      const trimmedPhone = form.phoneNumber.trim();
      const payload = {
        email: profile?.email || form.email,
        firstname: form.firstname.trim() || profile?.firstname || '',
        lastname: form.lastname.trim() || profile?.lastname || '',
        phoneNumber: trimmedPhone ? trimmedPhone : null,
      };

      await onUpdate(payload);
      if (removeImage) {
        await onRemove();
      } else if (imageFile) {
        await onUpload(imageFile);
      }
      setMessage('Profile updated.');
      setIsEditing(false);
      setImageFile(null);
      setImagePreviewUrl('');
      setRemoveImage(false);
      isDirtyRef.current = false;
    } catch (err) {
      setMessage(err?.message || err || 'Unable to update profile.');
    }
  };

  return (
    <div className="overflow-hidden rounded-lg border border-slate-200 bg-white shadow-sm">
      <div className="border-b border-slate-200 bg-gradient-to-r from-blue-50 to-white px-6 py-6">
        <div className="flex flex-col gap-6 md:flex-row md:items-center md:justify-between">
          <div className="flex flex-col gap-5 sm:flex-row sm:items-center">
            <ProfileImageUploader
              profile={profile}
              loading={loading}
              accentClass={accentClass}
              previewUrl={imagePreviewUrl}
              isMarkedForRemoval={removeImage}
              onSelectFile={handleSelectImage}
              onMarkRemove={handleMarkRemoveImage}
              showStatus
              showActions={isEditing}
            />
            <div>
              <h2 className="text-2xl font-bold text-slate-900">{getName(profile)}</h2>
              <p className={`mt-1 text-sm font-bold uppercase tracking-wide ${accentTextClass}`}>
                {getDisplayRole(profile.roles?.[0] || profile.role)}
              </p>
              <p className="mt-2 max-w-xl text-sm text-slate-600">
                Keep your contact details current so Fort Point Properties can reach you quickly.
              </p>
            </div>
          </div>

          <div className="flex gap-2">
            {isEditing ? (
              <>
                <button
                  type="button"
                  onClick={handleSave}
                  disabled={loading}
                  className="inline-flex items-center gap-2 rounded-lg bg-blue-600 px-4 py-2 text-sm font-bold text-white hover:bg-blue-700 disabled:opacity-60"
                >
                  <FiSave size={16} />
                  Save
                </button>
                <button
                  type="button"
                  onClick={handleCancel}
                  disabled={loading}
                  className="inline-flex items-center gap-2 rounded-lg border border-slate-300 px-4 py-2 text-sm font-bold text-slate-700 hover:bg-slate-50 disabled:opacity-60"
                >
                  <FiX size={16} />
                  Cancel
                </button>
              </>
            ) : (
              <button
                type="button"
                onClick={handleStartEdit}
                className="inline-flex items-center gap-2 rounded-lg border border-blue-200 bg-blue-50 px-4 py-2 text-sm font-bold text-blue-700 hover:bg-blue-100"
              >
                <FiEdit2 size={16} />
                Edit Profile
              </button>
            )}
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 gap-5 p-6 md:grid-cols-2">
        <Field icon={FiUser} label="First Name">
          <input className={inputClass} value={form.firstname} onChange={(event) => setField('firstname', event.target.value)} disabled={!isEditing} required />
        </Field>
        <Field icon={FiUser} label="Last Name">
          <input className={inputClass} value={form.lastname} onChange={(event) => setField('lastname', event.target.value)} disabled={!isEditing} required />
        </Field>
        <Field icon={FiMail} label="Email Address">
          <input className={inputClass} type="email" value={form.email} onChange={(event) => setField('email', event.target.value)} disabled required />
        </Field>
        <Field icon={FiPhone} label="Phone Number">
          <input className={inputClass} value={form.phoneNumber} onChange={(event) => setField('phoneNumber', event.target.value)} disabled={!isEditing} placeholder="Add phone number" />
        </Field>
      </div>

      {(message || error) && (
        <div className="border-t border-slate-200 px-6 py-4">
          <p className={`text-sm font-semibold ${String(message || error).includes('updated') ? 'text-green-700' : 'text-red-600'}`}>
            {message || error}
          </p>
        </div>
      )}
    </div>
  );
}

function Field({ icon, label, children }) {
  const IconComponent = icon;

  return (
    <label className="block">
      <span className="mb-2 flex items-center gap-2 text-xs font-bold uppercase tracking-wide text-slate-500">
        <IconComponent size={15} className="text-blue-600" />
        {label}
      </span>
      {children}
    </label>
  );
}
