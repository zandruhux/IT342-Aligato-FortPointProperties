import React from 'react'
import { useNavigate } from 'react-router-dom'
import RegistrationForm from '../components/RegistrationForm'
import HeroSection, { HeroBackdrop } from '../../../shared/components/ui/HeroSection'

export default function RegisterPage() {
  const navigate = useNavigate()

  return (
    <HeroBackdrop>
      <div className="flex justify-center lg:justify-start">
        <RegistrationForm onSwitchToLogin={() => navigate('/login')} />
      </div>
      <div className="hidden lg:block lg:pt-10">
        <HeroSection variant="auth" />
      </div>
    </HeroBackdrop>
  )
}
