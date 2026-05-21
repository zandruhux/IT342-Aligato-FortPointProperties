import { useEffect } from 'react';
import { useProfile } from '../hooks/useProfile';
import { ProfileImageUploader } from '../components';

export default function RegisteredUserProfile() {
  const { profile, loading, fetchProfile, uploadProfileImage, removeProfileImage } = useProfile();

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

        {profile ? (
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div className="md:col-span-1">
              <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6 text-center">
                <ProfileImageUploader
                  profile={profile}
                  loading={loading}
                  accentClass="bg-blue-600"
                  onUpload={uploadProfileImage}
                  onRemove={removeProfileImage}
                />
                <h2 className="text-xl font-bold text-gray-900 mb-1 mt-4">
                  {profile.name || `${profile.firstname} ${profile.lastname}`}
                </h2>
                <p className="text-sm text-blue-600 font-semibold uppercase tracking-wide">
                  {profile.roles?.[0] || profile.role || 'REGISTERED_USER'}
                </p>
              </div>
            </div>

            <div className="md:col-span-2">
              <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
                <h3 className="text-lg font-bold text-gray-900 mb-6">Personal Information</h3>
                <div className="space-y-6">
                  <div>
                    <p className="text-xs text-gray-700 uppercase tracking-wide font-bold mb-2">Full Name</p>
                    <p className="text-base text-gray-900 font-semibold">
                      {profile.name || `${profile.firstname} ${profile.lastname}` || '-'}
                    </p>
                  </div>
                  <div>
                    <p className="text-xs text-gray-700 uppercase tracking-wide font-bold mb-2">Email Address</p>
                    <p className="text-base text-gray-900 font-semibold">{profile.email || '-'}</p>
                  </div>
                  <div>
                    <p className="text-xs text-gray-700 uppercase tracking-wide font-bold mb-2">Account Status</p>
                    <div className="inline-block px-3 py-1 bg-green-100 text-green-800 rounded-full text-sm font-bold">
                      Active
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        ) : (
          <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-12 text-center">
            <p className="text-gray-700 font-semibold">Unable to load profile information</p>
          </div>
        )}
      </div>
    </div>
  );
}
