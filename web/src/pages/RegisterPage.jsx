import React from 'react';
import RegistrationForm from '../components/common/RegistrationForm';
import HeroSection from '../components/common/HeroSection';

export default function RegisterPage() {
  return (
    <div className="flex h-screen bg-gray-100">
      {/* Left side - Registration Form */}
      <div className="w-1/3 flex items-center justify-center p-8">
        <RegistrationForm />
      </div>

      {/* Right side - Hero Section */}
      <div className="w-2/3 bg-gray-300">
        <HeroSection />
      </div>
    </div>
  );
}
