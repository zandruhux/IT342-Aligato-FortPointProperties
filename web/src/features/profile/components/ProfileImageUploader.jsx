import React, { useEffect, useRef } from 'react';
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
  previewUrl,
  isMarkedForRemoval = false,
  onSelectFile,
  onMarkRemove,
  showStatus = false,
  showActions = true,
}) {
  const fileInputRef = useRef(null);
  const imageUrl = isMarkedForRemoval ? '' : previewUrl || profile?.profileImageUrl || '';

  useEffect(() => {
    return () => {
      if (previewUrl) {
        URL.revokeObjectURL(previewUrl);
      }
    };
  }, [previewUrl]);

  const handleFileChange = (event) => {
    const file = event.target.files?.[0];
    if (!file) return;

    onSelectFile?.(file);
    event.target.value = '';
  };

  const handleRemove = () => {
    onMarkRemove?.();
  };

  return (
    <div className="space-y-4">
      <div className="relative mx-auto h-24 w-24">
        <div className="h-24 w-24 overflow-hidden rounded-full shadow-lg">
        {imageUrl ? (
          <img
            src={imageUrl}
            alt={`${profile.firstname || 'User'} profile`}
            className="w-full h-full object-cover"
          />
        ) : (
          <div className={`w-full h-full ${accentClass} flex items-center justify-center text-white text-2xl font-bold`}>
            {getInitials(profile)}
          </div>
        )}
        </div>
        {showStatus && (
          <span
            className={`absolute bottom-2 right-1 h-4 w-4 rounded-full border-2 border-white ${
              profile?.status && profile.status !== 'Active' ? 'bg-red-500' : 'bg-green-500'
            }`}
            aria-label={profile?.status && profile.status !== 'Active' ? 'Inactive account' : 'Active account'}
          />
        )}
      </div>

      <input
        ref={fileInputRef}
        type="file"
        accept="image/jpeg,image/png,image/webp"
        className="hidden"
        onChange={handleFileChange}
      />

      {showActions && (
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
            disabled={loading || (!profile?.profileImageUrl && !previewUrl)}
            className="inline-flex items-center justify-center gap-2 px-3 py-2 bg-gray-100 text-gray-800 rounded-lg text-sm font-semibold hover:bg-gray-200 disabled:opacity-60"
          >
            <FiTrash2 size={16} />
            Remove
          </button>
        </div>
      )}

      {showActions && previewUrl && <p className="text-xs text-gray-600 font-medium">New image selected. Click Save to apply it.</p>}
      {showActions && isMarkedForRemoval && <p className="text-xs text-gray-600 font-medium">Image will be removed when you save.</p>}
    </div>
  );
}
