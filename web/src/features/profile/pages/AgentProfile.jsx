import { useEffect } from 'react';
import { useProfile } from '../hooks/useProfile';
import { AgentSidebar } from '../../../shared/components/layout';

/**
 * AgentProfile Component
 * Refactored to use vertical slicing architecture
 * Displays and allows editing of agent profile
 */
export default function AgentProfile() {
  const { profile, loading, fetchProfile } = useProfile();

  useEffect(() => {
    fetchProfile();
  }, [fetchProfile]);

  const getInitials = (name) => {
    return name
      ?.split(' ')
      .map((part) => part[0])
      .join('')
      .toUpperCase() || 'A';
  };

  if (loading) {
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

        {profile ? (
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div className="md:col-span-1">
              <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6 text-center">
                <div className="w-20 h-20 bg-gradient-to-br from-green-600 to-green-700 rounded-full flex items-center justify-center text-white text-2xl font-bold mx-auto mb-4 shadow-lg">
                  {getInitials(profile.name || profile.firstname + ' ' + profile.lastname)}
                </div>
                <h2 className="text-xl font-bold text-gray-900 mb-1">
                  {profile.name || `${profile.firstname} ${profile.lastname}`}
                </h2>
                <p className="text-sm text-green-600 font-semibold mb-4 uppercase tracking-wide">
                  {profile.roles?.[0] || profile.role || 'AGENT'}
                </p>
                <button className="w-full px-4 py-2 bg-gradient-to-r from-green-600 to-green-700 text-white rounded-lg font-semibold hover:from-green-700 hover:to-green-800 transition-all duration-200">
                  Edit Profile
                </button>
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
                    <p className="text-xs text-gray-700 uppercase tracking-wide font-bold mb-2">Role</p>
                    <div className="inline-block px-3 py-1 bg-green-100 text-green-800 rounded-full text-sm font-bold">
                      {profile.roles?.[0] || profile.role || 'AGENT'}
                    </div>
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
    </div>
  );
}
