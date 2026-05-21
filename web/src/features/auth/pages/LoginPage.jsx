import React from 'react'
import { useNavigate } from 'react-router-dom'
import LoginForm from '../components/LoginForm'
import HeroSection, { HeroBackdrop } from '../../../shared/components/ui/HeroSection'

export default function LoginPage({ onLoginSuccess }) {
  const navigate = useNavigate()

  return (
    <HeroBackdrop>
      <div className="flex justify-center lg:justify-start">
        <LoginForm onSwitchToRegister={() => navigate('/register')} onLoginSuccess={onLoginSuccess} />
      </div>
      <div className="hidden lg:block lg:pt-10">
        <HeroSection variant="auth" />
      </div>
    </HeroBackdrop>
  )
}
