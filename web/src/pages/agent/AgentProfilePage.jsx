import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import AgentSidebar from '../../features/agent/components/common/AgentSidebar';

const AgentProfilePage = ({ onLogout }) => {
  const [isLoggedIn, setIsLoggedIn] = React.useState(false);
  const [userRole, setUserRole] = React.useState('');
  const [userData, setUserData] = React.useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    const user = localStorage.getItem('user');

    if (!token) {
      navigate('/login');
      return;
    }

    setIsLoggedIn(true);

    if (user) {
      try {
        const parsedUser = JSON.parse(user);
        const role = parsedUser.roles?.[0] || parsedUser.role || '';
        setUserRole(role);
        setUserData(parsedUser);

        if (role !== 'AGENT' && role !== 'ADMIN') {
          navigate('/');
        }
      } catch (error) {
        console.error('Error parsing user data:', error);
        navigate('/login');
      }
    }
  }, [navigate]);

  if (!isLoggedIn) {
    return <div className="text-center py-12">Loading...</div>;
  }

  return (
    <div className="flex bg-gray-50 min-h-screen">
      <AgentSidebar
        isLoggedIn={isLoggedIn}
        onLogout={onLogout}
        userRole={userRole}
      />

      <div className="flex-1 ml-56">
        <div className="bg-gradient-to-r from-blue-600 to-blue-700 text-white py-10 shadow-md">
          <div className="max-w-7xl mx-auto px-6 sm:px-8 lg:px-10">
            <h1 className="text-5xl font-bold tracking-tight">My Profile</h1>
            <p className="text-blue-100 mt-3 text-lg">Manage your agent profile and account settings</p>
          </div>
        </div>

        <div className="max-w-7xl mx-auto px-6 sm:px-8 lg:px-10 py-16">
          <div className="bg-white rounded-lg shadow-md p-10">
            <div className="flex items-center gap-8 mb-10 pb-10 border-b-2 border-gray-200">
              <div className="w-28 h-28 bg-gradient-to-br from-blue-600 to-blue-700 rounded-full flex items-center justify-center text-white text-4xl font-bold flex-shrink-0 shadow-md">
                {userData?.firstName?.charAt(0)}{userData?.lastName?.charAt(0)}
              </div>
              <div>
                <h2 className="text-3xl font-bold text-gray-900">
                  {userData?.firstName} {userData?.lastName}
                </h2>
                <p className="text-gray-600 text-lg mt-2">{userData?.email}</p>
                <p className="text-gray-600 mt-3">
                  <span className="font-semibold text-gray-900">Role:</span> <span className="bg-blue-100 text-blue-700 px-3 py-1 rounded-full text-sm font-semibold">{userRole}</span>
                </p>
              </div>
            </div>

            <div className="pt-8">
              <h3 className="text-2xl font-bold text-gray-900 mb-8">Account Information</h3>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                <div className="bg-gray-50 p-6 rounded-lg">
                  <p className="text-sm text-gray-600 font-semibold mb-2 uppercase tracking-wide">First Name</p>
                  <p className="text-lg text-gray-900 font-medium">{userData?.firstName}</p>
                </div>
                <div className="bg-gray-50 p-6 rounded-lg">
                  <p className="text-sm text-gray-600 font-semibold mb-2 uppercase tracking-wide">Last Name</p>
                  <p className="text-lg text-gray-900 font-medium">{userData?.lastName}</p>
                </div>
                <div className="bg-gray-50 p-6 rounded-lg">
                  <p className="text-sm text-gray-600 font-semibold mb-2 uppercase tracking-wide">Email</p>
                  <p className="text-lg text-gray-900 font-medium">{userData?.email}</p>
                </div>
                <div className="bg-gray-50 p-6 rounded-lg">
                  <p className="text-sm text-gray-600 font-semibold mb-2 uppercase tracking-wide">Role</p>
                  <p className="text-lg text-gray-900 font-medium">{userRole}</p>
                </div>
              </div>
            </div>

            <div className="mt-12 pt-10 border-t-2 border-gray-200">
              <button className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 px-8 rounded-lg transition-colors duration-200 shadow-md hover:shadow-lg">
                Edit Profile
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AgentProfilePage;
