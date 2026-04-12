import React from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import logo from '../../../../assets/FortPointProperties_Logo.jpg';

const AgentSidebar = ({ isLoggedIn, onLogout, userRole }) => {
  const navigate = useNavigate();
  const location = useLocation();

  // Check if user is an agent
  if (!isLoggedIn || (userRole !== 'AGENT' && userRole !== 'ADMIN')) {
    return null;
  }

  const isActive = (path) => {
    return location.pathname === path;
  };

  const navItems = [
    { path: '/agent/properties', label: 'Properties', icon: '🏠' },
    { path: '/agent/bulletin', label: 'Bulletin Board', icon: '📋' },
    { path: '/agent/messages', label: 'Messages', icon: '💬' },
    { path: '/agent/articles', label: 'Articles', icon: '📄' },
    { path: '/agent/profile', label: 'Profile', icon: '👤' },
  ];

  const handleNavClick = (path) => {
    navigate(path);
  };

  const handleLogout = () => {
    onLogout();
  }

  return (
    <div className="fixed left-0 top-0 h-screen w-56 bg-gradient-to-b from-slate-950 via-slate-900 to-slate-900 text-white flex flex-col shadow-2xl">
      {/* Logo Section */}
      <div className="border-b border-slate-700 p-6 bg-gradient-to-br from-slate-900 to-slate-800">
        <div className="flex items-center gap-3">
          <div className="w-11 h-11 bg-white rounded-lg flex items-center justify-center shadow-lg">
            <img src={logo} alt="Fort Point Properties Logo" className="w-9 h-9 object-contain" />
          </div>
          <div>
            <h1 className="text-base font-bold tracking-tight text-white">Fort Point Properties</h1>
          </div>
        </div>
      </div>

      {/* Navigation Items */}
      <nav className="flex-1 pt-8 px-3 space-y-2">
        {navItems.map((item) => (
          <button
            key={item.path}
            onClick={() => handleNavClick(item.path)}
            className={`w-full text-left px-4 py-3 rounded-lg font-medium transition-all duration-200 ${
              isActive(item.path)
                ? 'bg-blue-600 text-white shadow-lg'
                : 'text-slate-300 hover:bg-slate-700 hover:text-white'
            }`}
          >
            <span className="mr-3 text-lg">{item.icon}</span>
            <span className="text-sm font-semibold">{item.label}</span>
          </button>
        ))}
      </nav>

      {/* Logout Button */}
      <div className="border-t border-slate-700 p-4 bg-gradient-to-t from-slate-900 to-slate-800">
        <button
          onClick={handleLogout}
          className="w-full bg-red-600 hover:bg-red-700 text-white font-bold py-3 rounded-lg transition-all duration-200 shadow-lg hover:shadow-xl text-sm"
        >
          Logout
        </button>
      </div>
    </div>
  );
};

export default AgentSidebar;
