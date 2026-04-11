import React, { useState } from 'react';
import propertyImage from '../../assets/property.png';

export default function HeroSection({ showSearch = false }) {
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('For Sale');

  return (
    <section className="bg-gradient-to-br from-blue-50 to-blue-100 py-16">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-12 items-center">
          {/* Left Content */}
          <div>
            <h1 className="text-5xl font-bold mb-4" style={{ color: '#000000' }}>
              Find Your
              <br />
              <span style={{ color: '#007EB7' }}>Perfect</span> Home
            </h1>
            <p className="text-lg leading-relaxed mb-8" style={{ color: '#747474' }}>
              Discover exceptional properties across the Philippines. Whether
              you're buying, selling, or investing, our expert team is here to guide
              you every step of the way.
            </p>

            {showSearch && (
              <div className="mb-8">
                {/* Search Input */}
                <div className="flex items-center bg-white rounded-lg shadow-lg overflow-hidden mb-4">
                  <svg className="w-5 h-5 ml-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"></path>
                  </svg>
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
              </div>
            )}

            {/* Trusted By Section */}
            <div className="flex items-center gap-3">
              <div className="flex -space-x-2">
                {[0, 1, 2, 3].map((i) => (
                  <div
                    key={i}
                    className="w-10 h-10 rounded-full border-2 border-white flex items-center justify-center font-semibold text-white"
                    style={{ backgroundColor: '#007EB7' }}
                  >
                    {String.fromCharCode(65 + i)}
                  </div>
                ))}
              </div>
              <div>
                <p className="text-sm font-semibold text-gray-900">
                  Trusted by 1,800+ clients
                </p>
                <p className="text-xs text-gray-500">Join our satisfied homeowners</p>
              </div>
            </div>
          </div>

          {/* Right Image */}
          <div className="hidden lg:block">
            <img
              src={propertyImage}
              alt="Property"
              className="rounded-3xl shadow-lg w-full h-96 object-cover"
            />
          </div>
        </div>
      </div>
    </section>
  );
}