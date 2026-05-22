import { useEffect } from 'react';
import { useProfile } from '../hooks';
import { ProfilePanel } from '../components';

export default function RegisteredUserProfile() {
  const { profile, loading, error, fetchProfile, updateProfile, uploadProfileImage, removeProfileImage } = useProfile();

  useEffect(() => {
    fetchProfile();
  }, [fetchProfile]);

  if (loading && !profile) {
    return (
      <div className="bg-gray-50 py-12">
        <div className="max-w-4xl mx-auto px-4">
          <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-12 text-center">
            <p className="text-gray-700 font-semibold">Loading profile information...</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-gray-50 py-12 min-h-full">
      <div className="max-w-4xl mx-auto px-4">
        <div className="mb-6">
          <h1 className="text-3xl font-bold text-gray-900">My Profile</h1>
          <p className="text-gray-600 mt-1">View and manage your account profile</p>
        </div>

        <ProfilePanel
          profile={profile}
          loading={loading}
          error={error}
          accentClass="bg-blue-600"
          accentTextClass="text-blue-600"
          onUpload={uploadProfileImage}
          onRemove={removeProfileImage}
          onUpdate={updateProfile}
        />
      </div>
    </div>
  );
}
