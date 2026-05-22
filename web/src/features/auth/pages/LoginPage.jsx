import React from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { LoginForm } from '../components'
import HeroSection, { HeroBackdrop } from '../../../shared/components/ui/HeroSection'

export default function LoginPage({ onLoginSuccess }) {
  const navigate = useNavigate()
  const [searchParams] = useSearchParams()
  const oauthError = searchParams.get('error') || ''

  return (
    <HeroBackdrop>
      <div className="flex justify-center lg:justify-start">
        <LoginForm
          onSwitchToRegister={() => navigate('/register')}
          onLoginSuccess={onLoginSuccess}
          initialError={oauthError}
        />
      </div>
      <div className="hidden lg:block lg:pt-10">
        <HeroSection variant="auth" />
      </div>
    </HeroBackdrop>
  )
}
