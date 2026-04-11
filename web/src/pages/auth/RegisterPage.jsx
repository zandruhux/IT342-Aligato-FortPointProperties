import React from 'react';
import { useNavigate } from 'react-router-dom';
import RegistrationForm from '../../features/auth/components/RegistrationForm';
import HeroSection from '../../components/common/HeroSection';

export default function RegisterPage() {
  const navigate = useNavigate();

  return (
    <div className="flex" style={{ height: 'calc(100vh - 64px)' }}>
      {/* Left side - Registration Form */}
      <div className="w-1/3 flex justify-center px-16 pt-12 overflow-y-auto" style={{ backgroundColor: '#FFFFFF' }}>
        <RegistrationForm onSwitchToLogin={() => navigate('/login')} />
      </div>

      {/* Right side - Hero Section */}
      <div className="w-2/3 h-full">
        <HeroSection />
      </div>
    </div>
  );
}
