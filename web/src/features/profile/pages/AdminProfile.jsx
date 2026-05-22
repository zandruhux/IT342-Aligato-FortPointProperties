import { useEffect } from 'react';
import { useProfile } from '../hooks';
import { ProfilePanel } from '../components';
import { AdminSidebar } from '../../../shared/components/layout';

/**
 * AdminProfile Component
 * Refactored to use vertical slicing architecture
 * Displays and allows editing of admin profile
 */
export default function AdminProfile() {
  const { profile, loading, error, fetchProfile, updateProfile, uploadProfileImage, removeProfileImage } = useProfile();

  useEffect(() => {
    fetchProfile();
  }, [fetchProfile]);

  if (loading && !profile) {
    return (
      <div className="flex h-full">
        <AdminSidebar />
        <div className="flex-1 ml-64 bg-slate-50 py-12">
          <div className="max-w-4xl mx-auto px-4">
            <div className="bg-white rounded-lg shadow-sm border border-slate-200 p-12 text-center">
              <p className="text-slate-700 font-semibold">Loading profile information...</p>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="flex h-full">
      {/* Sidebar */}
      <AdminSidebar />
      
      {/* Main Content */}
      <div className="flex-1 ml-64 bg-slate-50 py-12">
        <div className="max-w-4xl mx-auto px-4">
        <div className="mb-6">
          <h1 className="text-3xl font-bold text-slate-900">My Profile</h1>
          <p className="text-slate-600 mt-1">View and manage your admin profile</p>
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
    </div>
  );
}
