import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import AgentSidebar from '../../features/agent/components/common/AgentSidebar';
import AgentPropertiesSection from '../../features/agent/components/Properties/AgentPropertiesSection';

const AgentPropertiesPage = ({ onLogout }) => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [userRole, setUserRole] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    const user = localStorage.getItem('user');

    if (!token) {
      navigate('/login');
      return;
    }

    setIsLoggedIn(true);

    // Extract role from user data
    if (user) {
      try {
        const userData = JSON.parse(user);
        const role = userData.roles?.[0] || userData.role || '';
        setUserRole(role);

        // Check if user has AGENT or ADMIN role
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
      {/* Sidebar */}
      <AgentSidebar
        isLoggedIn={isLoggedIn}
        onLogout={onLogout}
        userRole={userRole}
      />

      {/* Main Content */}
      <div className="flex-1 ml-56">
        <div className="bg-gradient-to-r from-blue-600 to-blue-700 text-white py-10 shadow-md">
          <div className="max-w-7xl mx-auto px-6 sm:px-8 lg:px-10">
            <h1 className="text-5xl font-bold tracking-tight">My Properties</h1>
            <p className="text-blue-100 mt-3 text-lg leading-relaxed max-w-2xl">
              Admin-created listings assigned to you. Use the selling points and target market information when talking with clients.
            </p>
          </div>
        </div>

        {/* Properties Section */}
        <div className="max-w-7xl mx-auto px-6 sm:px-8 lg:px-10 py-16">
          <AgentPropertiesSection />
        </div>
      </div>
    </div>
  );
};

export default AgentPropertiesPage;
