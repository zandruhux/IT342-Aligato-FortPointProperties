import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import logo from '../../assets/FortPointProperties_Logo.jpg';

export default function Header({ isLoggedIn, onLogout }) {
  const navigate = useNavigate();

  return (
    <header className="bg-white shadow-sm">
      <nav className="px-16 py-4 flex items-center justify-between">
        <Link to="/" className="flex items-center gap-2 no-underline">
          <img
            src={logo}
            alt="Fort Point Properties"
            className="w-14 h-14 rounded-lg object-cover"
          />
          <span className="text-xl font-semibold" style={{ color: '#000000' }}>Fort Point Properties</span>
        </Link>
        
        <div className="flex items-center gap-8">
          <Link to="/" className="hover:opacity-80 transition no-underline" style={{ color: '#747474' }}>Home</Link>
          <a href="#properties" className="hover:opacity-80 transition" style={{ color: '#747474' }}>Properties</a>
          <a href="#articles" className="hover:opacity-80 transition" style={{ color: '#747474' }}>Articles</a>
        </div>

        {isLoggedIn ? (
          <button
            onClick={onLogout}
            className="text-white px-6 py-2 rounded hover:opacity-90 transition"
            style={{ backgroundColor: '#007EB7' }}
          >
            Logout
          </button>
        ) : (
          <button
            onClick={() => navigate('/login')}
            className="text-white px-6 py-2 rounded hover:opacity-90 transition"
            style={{ backgroundColor: '#007EB7' }}
          >
            Sign In
          </button>
        )}
      </nav>
    </header>
  );
}
