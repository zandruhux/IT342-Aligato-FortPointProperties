import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuthContext } from '../../context/useAuthContext';
import logo from '../../../assets/FortPointProperties_Logo.jpg';

export default function Header({ isLoggedIn, onLogout }) {
  const navigate = useNavigate();
  const { user } = useAuthContext();
  const canUseMessages = isLoggedIn && (user?.role === 'registered_user' || user?.role === 'USER' || user?.role === 'REGISTERED_USER');
  const canUseCareer = canUseMessages;

  return (
    <header className="bg-white shadow-sm">
      <nav className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4 flex items-center justify-between">
        <Link to="/" className="flex items-center gap-2 no-underline">
          <img
            src={logo}
            alt="Fort Point Properties"
            className="w-14 h-14 rounded-lg object-cover"
          />
          <span className="text-xl font-semibold" style={{ color: '#000000' }}>
            Fort Point Properties
          </span>
        </Link>

        <div className="flex items-center gap-8">
          <Link
            to="/"
            className="hover:opacity-80 transition no-underline"
            style={{ color: '#747474' }}
          >
            Home
          </Link>
          <Link
            to="/properties"
            className="hover:opacity-80 transition no-underline"
            style={{ color: '#747474' }}
          >
            Properties
          </Link>
          {isLoggedIn && (
            <Link
              to="/favorites"
              className="hover:opacity-80 transition no-underline"
              style={{ color: '#747474' }}
            >
              Favorites
            </Link>
          )}
          {canUseCareer && (
            <Link
              to="/career"
              className="hover:opacity-80 transition no-underline"
              style={{ color: '#747474' }}
            >
              Career
            </Link>
          )}
          {canUseMessages && (
            <Link
              to="/messages"
              className="hover:opacity-80 transition no-underline"
              style={{ color: '#747474' }}
            >
              Messages
            </Link>
          )}
        </div>

        {isLoggedIn ? (
          <button
            onClick={onLogout}
            className="text-white px-6 py-2 rounded hover:opacity-90 transition"
            style={{ backgroundColor: '#FF0000' }}
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
