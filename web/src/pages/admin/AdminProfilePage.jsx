import { useState, useEffect } from 'react';
import AdminSidebar from '../../features/admin/components/common/AdminSidebar';

export default function AdminProfilePage({ onLogout }) {
  const [userData, setUserData] = useState(null);

  useEffect(() => {
    const user = localStorage.getItem('user');
    if (user) {
      try {
        setUserData(JSON.parse(user));
      } catch (error) {
        console.error('Error parsing user data:', error);
      }
    }
  }, []);

  const getInitials = (name) => {
    return name
      ?.split(' ')
      .map(part => part[0])
      .join('')
      .toUpperCase() || 'A';
  };

  return (
    <div className="flex min-h-screen bg-slate-50">
      {/* Sidebar */}
      <AdminSidebar onLogout={onLogout} />

      {/* Main Content */}
      <div className="ml-64 flex-1 flex flex-col">
        {/* Header */}
        <header className="bg-gradient-to-r from-blue-600 to-blue-700 text-white shadow-lg sticky top-0 z-40">
          <div className="px-8 py-6">
            <h1 className="text-2xl font-bold">Profile</h1>
            <p className="text-blue-100 text-sm mt-1">Manage your admin profile and preferences</p>
          </div>
        </header>

        {/* Content */}
        <main className="flex-1 overflow-auto">
          <div className="p-8">
            {userData ? (
              <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                {/* Profile Card */}
                <div className="md:col-span-1">
                  <div className="bg-white rounded-lg shadow-sm border border-slate-200 p-6 text-center">
                    <div className="w-20 h-20 bg-gradient-to-br from-blue-600 to-blue-700 rounded-full flex items-center justify-center text-white text-2xl font-bold mx-auto mb-4 shadow-lg">
                      {getInitials(userData.name)}
                    </div>
                    <h2 className="text-xl font-bold text-slate-900 mb-1">{userData.name || 'Admin'}</h2>
                    <p className="text-sm text-blue-600 font-semibold mb-4 uppercase tracking-wide">
                      {userData.roles?.[0] || userData.role || 'ADMIN'}
                    </p>
                    <button className="w-full px-4 py-2 bg-gradient-to-r from-blue-600 to-blue-700 text-white rounded-lg font-semibold hover:from-blue-700 hover:to-blue-800 transition-all duration-200">
                      Edit Profile
                    </button>
                  </div>
                </div>

                {/* Profile Details */}
                <div className="md:col-span-2">
                  <div className="bg-white rounded-lg shadow-sm border border-slate-200 p-6">
                    <h3 className="text-lg font-bold text-slate-900 mb-6">Personal Information</h3>
                    <div className="space-y-6">
                      <div>
                        <p className="text-xs text-slate-700 uppercase tracking-wide font-bold mb-2">Full Name</p>
                        <p className="text-base text-slate-900 font-semibold">{userData.name || '-'}</p>
                      </div>
                      <div>
                        <p className="text-xs text-slate-700 uppercase tracking-wide font-bold mb-2">Email Address</p>
                        <p className="text-base text-slate-900 font-semibold">{userData.email || '-'}</p>
                      </div>
                      <div>
                        <p className="text-xs text-slate-700 uppercase tracking-wide font-bold mb-2">Role</p>
                        <div className="inline-block px-3 py-1 bg-blue-100 text-blue-800 rounded-full text-sm font-bold">
                          {userData.roles?.[0] || userData.role || 'ADMIN'}
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
                <p className="text-slate-700 font-semibold">Loading profile information...</p>
              </div>
            )}
          </div>
        </main>
      </div>
    </div>
  );
}
