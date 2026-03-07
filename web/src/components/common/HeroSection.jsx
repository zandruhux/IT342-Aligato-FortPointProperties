import React from 'react';
import propertyImage from '../../assets/property.png';

export default function HeroSection() {
  return (
    <div
      className="relative bg-cover bg-center rounded-lg overflow-hidden"
      style={{
        backgroundImage: `url(${propertyImage})`,
        backgroundSize: 'cover',
        backgroundPosition: 'center',
        minHeight: '400px',
      }}
    >
      {/* Overlay for better text readability */}
      <div className="absolute inset-0 bg-black bg-opacity-30"></div>

      {/* Content */}
      <div className="relative z-10 flex flex-col justify-center items-start h-full p-8">
        <div className="max-w-xl">
          <h1 className="text-5xl font-bold text-gray-900 mb-4">
            Find Your
            <br />
            <span className="text-blue-600">Perfect</span> Home
          </h1>
          <p className="text-gray-700 text-lg leading-relaxed">
            Discover exceptional properties across the Philippines. Whether you're buying, selling, or investing, our expert team is here to guide you every step of the way.
          </p>
        </div>
      </div>
    </div>
  );
}
