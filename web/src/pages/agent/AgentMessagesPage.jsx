import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import AgentSidebar from '../../features/agent/components/common/AgentSidebar';

const AgentMessagesPage = ({ onLogout }) => {
  const [isLoggedIn, setIsLoggedIn] = React.useState(false);
  const [userRole, setUserRole] = React.useState('');
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
        const userData = JSON.parse(user);
        const role = userData.roles?.[0] || userData.role || '';
        setUserRole(role);

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
            <h1 className="text-5xl font-bold tracking-tight">Messages</h1>
            <p className="text-blue-100 mt-3 text-lg">Communicate with clients and team members</p>
          </div>
        </div>

        <div className="max-w-7xl mx-auto px-6 sm:px-8 lg:px-10 py-16">
          <div className="bg-white rounded-lg shadow-md p-10 text-center">
            <p className="text-gray-500 text-lg font-medium">Messages content coming soon</p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AgentMessagesPage;
