import React from 'react';
import { useNavigate } from 'react-router-dom';
import propertyImage from '../../../assets/property.png';
import PropertySearchFilter from '../../../features/properties/components/PropertySearchFilter';

export default function HeroSection({ showSearch = false }) {
  const navigate = useNavigate();

  const handleHeroSearch = (searchTerm) => {
    navigate(`/properties?location=${encodeURIComponent(searchTerm)}`);
  };

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
                <PropertySearchFilter
                  searchTypes={[{ value: 'location', label: 'Location' }]}
                  onSearch={handleHeroSearch}
                  showPriceRange={false}
                  title=""
                />
              </div>
            )}

            {/* Trusted By Section */}
            <div className="flex flex-col space-y-3">
              <div className="flex items-center gap-3">
                <div className="w-12 h-12 rounded-full bg-white flex items-center justify-center font-bold text-sm" style={{ backgroundColor: '#E8E8E8', color: '#747474' }}>A</div>
                <div className="w-12 h-12 rounded-full bg-white flex items-center justify-center font-bold text-sm" style={{ backgroundColor: '#E8E8E8', color: '#747474' }}>B</div>
                <div className="w-12 h-12 rounded-full bg-white flex items-center justify-center font-bold text-sm" style={{ backgroundColor: '#E8E8E8', color: '#747474' }}>C</div>
                <div className="w-12 h-12 rounded-full bg-white flex items-center justify-center font-bold text-sm" style={{ backgroundColor: '#E8E8E8', color: '#747474' }}>D</div>
              </div>
              <div>
                <p className="font-bold text-lg" style={{ color: '#000000' }}>Trusted by 1,800+ clients</p>
                <p className="text-sm" style={{ color: '#747474' }}>Join our satisfied homeowners</p>
              </div>
            </div>
          </div>

          {/* Right Image */}
          <div className="hidden lg:flex">
            <img src={propertyImage} alt="Property" className="w-full h-auto rounded-lg shadow-lg" />
          </div>
        </div>
      </div>
    </section>
  );
}
