import React from 'react';

export default function Header({ isLoggedIn, onSignIn, onLogout }) {
  return (
    <header className="bg-white shadow-sm">
      <nav className="container mx-auto px-6 py-4 flex items-center justify-between">
        <div className="flex items-center gap-2">
          <div className="w-10 h-10 bg-gray-300 rounded-lg"></div>
          <span className="text-xl font-semibold text-gray-800">Fort Point Properties</span>
        </div>
        
        <div className="flex items-center gap-8">
          <a href="#home" className="text-gray-700 hover:text-blue-600 transition">Home</a>
          <a href="#properties" className="text-gray-700 hover:text-blue-600 transition">Properties</a>
          <a href="#articles" className="text-gray-700 hover:text-blue-600 transition">Articles</a>
        </div>

        {isLoggedIn ? (
          <button
            onClick={onLogout}
            className="bg-red-500 text-white px-6 py-2 rounded hover:bg-red-600 transition"
          >
            Logout
          </button>
        ) : (
          <button
            onClick={onSignIn}
            className="bg-blue-600 text-white px-6 py-2 rounded hover:bg-blue-700 transition"
          >
            Sign In
          </button>
        )}
      </nav>
    </header>
  );
}
