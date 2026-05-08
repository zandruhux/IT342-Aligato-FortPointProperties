import React from 'react'
import { useNavigate } from 'react-router-dom'
import LoginForm from '../components/LoginForm'
import HeroSection from '../../../shared/components/ui/HeroSection'

export default function LoginPage({ onLoginSuccess }) {
  const navigate = useNavigate()

  return (
    <div className="flex" style={{ height: 'calc(100vh - 64px)' }}>
      {/* Left side - Login Form */}
      <div className="w-1/3 flex justify-center px-16 pt-12" style={{ backgroundColor: '#FFFFFF' }}>
        <LoginForm onSwitchToRegister={() => navigate('/register')} onLoginSuccess={onLoginSuccess} />
      </div>

      {/* Right side - Hero Section */}
      <div className="w-2/3 h-full">
        <HeroSection />
      </div>
    </div>
  )
}
