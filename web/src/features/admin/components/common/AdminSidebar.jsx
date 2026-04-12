import { useNavigate, useLocation } from 'react-router-dom';
import { FiHome, FiSettings, FiFileText, FiUser, FiLogOut } from 'react-icons/fi';
import logo from '../../../../assets/FortPointProperties_Logo.jpg';

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
      <div className="p-6 border-b border-slate-700 bg-gradient-to-br from-slate-900 to-slate-800">
        <div className="flex items-center gap-3">
          <div className="w-12 h-12 bg-white rounded-lg flex items-center justify-center shadow-lg">
            <img src={logo} alt="Fort Point Properties Logo" className="w-10 h-10 object-contain" />
          </div>
          <div>
            <h2 className="text-base font-bold text-white leading-tight">Fort Point Properties</h2>
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
                  ? 'bg-blue-600 text-white shadow-lg'
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
          className="w-full flex items-center gap-3 px-4 py-3 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-all duration-200 font-semibold shadow-lg"
        >
          <FiLogOut size={20} />
          <span>Logout</span>
        </button>
      </div>
    </aside>
  );
}
