import React, { useState } from 'react';
import propertyImage from '../../assets/property.png';

export default function HeroSection({ showSearch = false }) {
  const [searchQuery, setSearchQuery] = useState('');

  return (
    <div
      className="relative bg-cover bg-center overflow-hidden"
      style={{
        backgroundImage: `url(${propertyImage})`,
        backgroundSize: 'cover',
        backgroundPosition: 'center',
        minHeight: showSearch ? '500px' : '400px',
      }}
    >
      {/* Overlay for better text readability */}
      <div className="absolute inset-0 bg-black/30"></div>

      {/* Content */}
      <div className="relative z-10 flex flex-col justify-center items-start h-full p-8 md:p-16">
        <div className="max-w-xl">
          <h1 className="text-5xl font-bold text-gray-900 mb-4">
            Find Your
            <br />
            <span className="text-cyan-600">Perfect</span> Home
          </h1>
          <p className="text-gray-700 text-lg leading-relaxed mb-8">
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
                className="flex-1 px-4 py-3 text-gray-700 focus:outline-none"
              />
              <button className="bg-cyan-600 text-white px-6 py-3 font-semibold hover:bg-cyan-700 transition whitespace-nowrap">
                Search Properties
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
