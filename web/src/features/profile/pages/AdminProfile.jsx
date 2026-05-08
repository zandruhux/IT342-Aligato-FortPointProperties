import { useEffect } from 'react';
import { useProfile } from '../hooks/useProfile';
import { AdminSidebar } from '../../../shared/components/layout';

/**
 * AdminProfile Component
 * Refactored to use vertical slicing architecture
 * Displays and allows editing of admin profile
 */
export default function AdminProfile() {
  const { profile, loading, fetchProfile } = useProfile();

  useEffect(() => {
    fetchProfile();
  }, []);

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
        {profile ? (
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div className="md:col-span-1">
              <div className="bg-white rounded-lg shadow-sm border border-slate-200 p-6 text-center">
                <div className="w-20 h-20 bg-gradient-to-br from-blue-600 to-blue-700 rounded-full flex items-center justify-center text-white text-2xl font-bold mx-auto mb-4 shadow-lg">
                  {getInitials(profile.name || profile.firstname + ' ' + profile.lastname)}
                </div>
                <h2 className="text-xl font-bold text-slate-900 mb-1">
                  {profile.name || `${profile.firstname} ${profile.lastname}`}
                </h2>
                <p className="text-sm text-blue-600 font-semibold mb-4 uppercase tracking-wide">
                  {profile.roles?.[0] || profile.role || 'ADMIN'}
                </p>
                <button className="w-full px-4 py-2 bg-gradient-to-r from-blue-600 to-blue-700 text-white rounded-lg font-semibold hover:from-blue-700 hover:to-blue-800 transition-all duration-200">
                  Edit Profile
                </button>
              </div>
            </div>

            <div className="md:col-span-2">
              <div className="bg-white rounded-lg shadow-sm border border-slate-200 p-6">
                <h3 className="text-lg font-bold text-slate-900 mb-6">Personal Information</h3>
                <div className="space-y-6">
                  <div>
                    <p className="text-xs text-slate-700 uppercase tracking-wide font-bold mb-2">Full Name</p>
                    <p className="text-base text-slate-900 font-semibold">
                      {profile.name || `${profile.firstname} ${profile.lastname}` || '-'}
                    </p>
                  </div>
                  <div>
                    <p className="text-xs text-slate-700 uppercase tracking-wide font-bold mb-2">Email Address</p>
                    <p className="text-base text-slate-900 font-semibold">{profile.email || '-'}</p>
                  </div>
                  <div>
                    <p className="text-xs text-slate-700 uppercase tracking-wide font-bold mb-2">Role</p>
                    <div className="inline-block px-3 py-1 bg-blue-100 text-blue-800 rounded-full text-sm font-bold">
                      {profile.roles?.[0] || profile.role || 'ADMIN'}
                    </div>
                  </div>
                  <div>
                    <p className="text-xs text-slate-700 uppercase tracking-wide font-bold mb-2">Account Status</p>
                    <div className="inline-block px-3 py-1 bg-green-100 text-green-800 rounded-full text-sm font-bold">
                      Active
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        ) : (
          <div className="bg-white rounded-lg shadow-sm border border-slate-200 p-12 text-center">
            <p className="text-slate-700 font-semibold">Unable to load profile information</p>
          </div>
        )}
        </div>
      </div>
    </div>
  );
}
