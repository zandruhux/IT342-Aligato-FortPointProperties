import { useNavigate, useLocation } from 'react-router-dom';
import { FiHome, FiSettings, FiFileText, FiUser, FiLogOut } from 'react-icons/fi';

export default function AdminSidebar({ onLogout }) {
  const navigate = useNavigate();
  const location = useLocation();

  const isActive = (path) => {
    return location.pathname === path;
  };

  const navItems = [
    { path: '/admin/properties', label: 'Properties', icon: FiHome },
    { path: '/admin/articles', label: 'Articles', icon: FiFileText },
    { path: '/admin/settings', label: 'Settings', icon: FiSettings },
    { path: '/admin/profile', label: 'Profile', icon: FiUser }
  ];

  return (
    <aside className="w-64 bg-gradient-to-b from-slate-950 to-slate-900 text-white h-screen fixed left-0 top-0 flex flex-col shadow-2xl">
      {/* Logo Section */}
      <div className="p-6 border-b border-slate-700">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 bg-gradient-to-br from-red-500 to-red-600 rounded-lg flex items-center justify-center font-bold text-white shadow-lg">
            FP
          </div>
          <div>
            <h2 className="text-lg font-bold text-white">Fort Point</h2>
            <p className="text-xs text-red-400 font-semibold">Admin Portal</p>
          </div>
        </div>
      </div>

      {/* Navigation Items */}
      <nav className="flex-1 py-6 px-4 space-y-2">
        {navItems.map((item) => {
          const Icon = item.icon;
          const active = isActive(item.path);
          return (
            <button
              key={item.path}
              onClick={() => navigate(item.path)}
              className={`w-full flex items-center gap-3 px-4 py-3 rounded-lg transition-all duration-200 font-bold ${
                active
                  ? 'bg-gradient-to-r from-red-600 to-red-500 text-white shadow-lg'
                  : 'text-slate-200 hover:bg-slate-700 hover:text-white'
              }`}
            >
              <Icon size={20} />
              <span>{item.label}</span>
            </button>
          );
        })}
      </nav>

      {/* Logout Button */}
      <div className="p-4 border-t border-slate-700">
        <button
          onClick={onLogout}
          className="w-full flex items-center gap-3 px-4 py-3 bg-red-600 hover:bg-red-700 text-white rounded-lg transition-all duration-200 font-semibold shadow-lg"
        >
          <FiLogOut size={20} />
          <span>Logout</span>
        </button>
      </div>
    </aside>
  );
}
