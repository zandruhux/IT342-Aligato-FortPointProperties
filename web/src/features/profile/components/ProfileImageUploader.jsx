import React, { useRef, useState } from 'react';
import { FiCamera, FiTrash2 } from 'react-icons/fi';

const getInitials = (profile) => {
  const name = profile?.name || `${profile?.firstname || ''} ${profile?.lastname || ''}`.trim();
  return name
    ?.split(' ')
    .filter(Boolean)
    .map((part) => part[0])
    .join('')
    .toUpperCase() || 'U';
};

export default function ProfileImageUploader({
  profile,
  loading,
  accentClass = 'bg-blue-600',
  onUpload,
  onRemove,
}) {
  const fileInputRef = useRef(null);
  const [message, setMessage] = useState('');

  const handleFileChange = async (event) => {
    const file = event.target.files?.[0];
    if (!file) return;

    setMessage('');
    try {
      await onUpload(file);
      setMessage('Profile image updated.');
    } catch (error) {
      setMessage(error?.message || 'Unable to update profile image.');
    } finally {
      event.target.value = '';
    }
  };

  const handleRemove = async () => {
    setMessage('');
    try {
      await onRemove();
      setMessage('Profile image removed.');
    } catch (error) {
      setMessage(error?.message || 'Unable to remove profile image.');
    }
  };

  return (
    <div className="space-y-4">
      <div className="w-24 h-24 rounded-full overflow-hidden mx-auto shadow-lg">
        {profile?.profileImageUrl ? (
          <img
            src={profile.profileImageUrl}
            alt={`${profile.firstname || 'User'} profile`}
            className="w-full h-full object-cover"
          />
        ) : (
          <div className={`w-full h-full ${accentClass} flex items-center justify-center text-white text-2xl font-bold`}>
            {getInitials(profile)}
          </div>
        )}
      </div>

      <input
        ref={fileInputRef}
        type="file"
        accept="image/jpeg,image/png,image/webp"
        className="hidden"
        onChange={handleFileChange}
      />

      <div className="grid grid-cols-2 gap-2">
        <button
          type="button"
          onClick={() => fileInputRef.current?.click()}
          disabled={loading}
          className="inline-flex items-center justify-center gap-2 px-3 py-2 bg-blue-600 text-white rounded-lg text-sm font-semibold hover:bg-blue-700 disabled:opacity-60"
        >
          <FiCamera size={16} />
          Upload
        </button>
        <button
          type="button"
          onClick={handleRemove}
          disabled={loading || !profile?.profileImageUrl}
          className="inline-flex items-center justify-center gap-2 px-3 py-2 bg-gray-100 text-gray-800 rounded-lg text-sm font-semibold hover:bg-gray-200 disabled:opacity-60"
        >
          <FiTrash2 size={16} />
          Remove
        </button>
      </div>

      {message && (
        <p className="text-xs text-gray-600 font-medium">{message}</p>
      )}
    </div>
  );
}
