import { useEffect } from 'react';
import { useProfile } from '../hooks/useProfile';
import { ProfilePanel } from '../components';
import { AgentSidebar } from '../../../shared/components/layout';

/**
 * AgentProfile Component
 * Refactored to use vertical slicing architecture
 * Displays and allows editing of agent profile
 */
export default function AgentProfile() {
  const { profile, loading, error, fetchProfile, updateProfile, uploadProfileImage, removeProfileImage } = useProfile();

  useEffect(() => {
    fetchProfile();
  }, [fetchProfile]);

  if (loading && !profile) {
    return (
      <div className="flex h-full">
        <AgentSidebar />
        <div className="flex-1 ml-56 bg-gray-50 py-12">
          <div className="max-w-4xl mx-auto px-4">
            <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-12 text-center">
              <p className="text-gray-700 font-semibold">Loading profile information...</p>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="flex h-full">
      {/* Sidebar */}
      <AgentSidebar />
      
      {/* Main Content */}
      <div className="flex-1 ml-56 bg-gray-50 py-12">
        <div className="max-w-4xl mx-auto px-4">
        <div className="mb-6">
          <h1 className="text-3xl font-bold text-gray-900">My Profile</h1>
          <p className="text-gray-600 mt-1">View and manage your agent profile</p>
        </div>

        <ProfilePanel
          profile={profile}
          loading={loading}
          error={error}
          accentClass="bg-green-600"
          accentTextClass="text-green-600"
          onUpload={uploadProfileImage}
          onRemove={removeProfileImage}
          onUpdate={updateProfile}
        />
        </div>
      </div>
    </div>
  );
}
