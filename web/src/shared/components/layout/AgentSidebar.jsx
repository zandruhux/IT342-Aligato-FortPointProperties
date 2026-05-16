import React from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { FiHome, FiInbox, FiLogOut, FiMessageSquare, FiUser } from 'react-icons/fi';
import { useAuthContext } from '../../context/useAuthContext';
import logo from '../../../assets/FortPointProperties_Logo.jpg';

const AgentSidebar = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { isLoggedIn, user, logout } = useAuthContext();

  if (!isLoggedIn || user?.role !== 'AGENT') {
    return null;
  }

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const isActive = (path) => {
    return location.pathname === path;
  };

  const navItems = [
    { path: '/agent/dashboard', label: 'Dashboard', icon: FiHome },
    { path: '/agent/properties', label: 'Properties', icon: FiHome },
    { path: '/agent/bulletin', label: 'Bulletin Board', icon: FiInbox },
    { path: '/agent/messages', label: 'Messages', icon: FiMessageSquare },
    { path: '/agent/profile', label: 'Profile', icon: FiUser },
  ];

  return (
    <div className="fixed left-0 top-0 h-screen w-56 bg-gradient-to-b from-slate-950 via-slate-900 to-slate-900 text-white flex flex-col shadow-2xl overflow-y-auto z-50">
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

      <div className="px-3 py-3 mx-3 mt-4 bg-green-100 rounded-lg text-center">
        <p className="text-xs font-bold text-green-900 tracking-wide">AGENT</p>
      </div>

      <nav className="flex-1 pt-8 px-3 space-y-2">
        {navItems.map((item) => {
          const Icon = item.icon;
          return (
            <button
              key={item.path}
              onClick={() => navigate(item.path)}
              className={`w-full text-left px-4 py-3 rounded-lg font-medium transition-all duration-200 flex items-center gap-3 ${
                isActive(item.path)
                  ? 'bg-blue-600 text-white shadow-lg'
                  : 'text-slate-300 hover:bg-slate-700 hover:text-white'
              }`}
            >
              <Icon size={18} />
              <span className="text-sm font-semibold">{item.label}</span>
            </button>
          );
        })}
      </nav>

      <div className="border-t border-slate-700 p-4 bg-gradient-to-t from-slate-900 to-slate-800">
        <button
          onClick={handleLogout}
          className="w-full flex items-center justify-center gap-2 bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 rounded-lg transition-all duration-200 shadow-lg hover:shadow-xl text-sm"
        >
          <FiLogOut size={18} />
          Logout
        </button>
      </div>
    </div>
  );
};

export default AgentSidebar;
