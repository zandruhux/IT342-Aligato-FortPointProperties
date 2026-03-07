import React from 'react';
import LoginForm from '../components/common/LoginForm';
import HeroSection from '../components/common/HeroSection';

export default function LoginPage({ onSwitchToRegister }) {
  return (
    <div className="flex h-screen bg-gray-100">
      {/* Left side - Login Form */}
      <div className="w-1/3 flex items-center justify-center p-8">
        <LoginForm onSwitchToRegister={onSwitchToRegister} />
      </div>

      {/* Right side - Hero Section */}
      <div className="w-2/3 bg-gray-300">
        <HeroSection />
      </div>
    </div>
  );
}
