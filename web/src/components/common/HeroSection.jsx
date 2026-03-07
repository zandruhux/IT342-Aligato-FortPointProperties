import React, { useState } from 'react';
import propertyImage from '../../assets/property.png';

export default function HeroSection({ showSearch = false }) {
  const [searchQuery, setSearchQuery] = useState('');

  return (
    <div
      className="relative bg-cover bg-center overflow-hidden h-full"
      style={{
        backgroundImage: `url(${propertyImage})`,
        backgroundSize: 'cover',
        backgroundPosition: 'center',
      }}
    >
      {/* Overlay for better text readability */}
      <div className="absolute inset-0 bg-black/30"></div>

      {/* Content */}
      <div className="relative z-10 flex flex-col items-start h-full p-8 md:p-16 pt-12">
        <div className="max-w-xl">
          <h1 className="text-5xl font-bold mb-4" style={{ color: '#FFFFFF' }}>
            Find Your
            <br />
            <span style={{ color: '#007EB7' }}>Perfect</span> Home
          </h1>
          <p className="text-lg leading-relaxed mb-8" style={{ color: '#FFFFFF' }}>
            Discover exceptional properties across the Philippines. Whether
            you're buying, selling, or investing, our expert team is here to guide
            you every step of the way.
          </p>

          {showSearch && (
            <div className="flex items-center bg-white rounded-lg shadow-lg overflow-hidden max-w-lg">
              <input
                type="text"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder="Location, City, or Address"
                className="flex-1 px-4 py-3 focus:outline-none"
                style={{ color: '#747474' }}
              />
              <button
                className="text-white px-6 py-3 font-semibold hover:opacity-90 transition whitespace-nowrap"
                style={{ backgroundColor: '#007EB7' }}
              >
                Search Properties
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
