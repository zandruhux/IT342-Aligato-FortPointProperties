import React, { useEffect, useState } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { authApi } from '../api'
import { useAuthContext } from '../../../shared/context/useAuthContext'

const normalizeRole = (role) => {
  const normalized = String(role || '').trim().toUpperCase().replace(/[-\s]+/g, '_')
  if (normalized === 'REGISTERED_USER' || normalized === 'REGISTERED_USEER' || normalized === 'USER') {
    return 'REGISTERED_USER'
  }
  return normalized || ''
}

const getRedirectPathForRole = (role) => {
  const normalizedRole = normalizeRole(role)
  if (normalizedRole === 'ADMIN') {
    return '/admin/properties'
  }
  if (normalizedRole === 'AGENT') {
    return '/agent/properties'
  }
  return '/'
}

export default function GoogleOAuthCallbackPage({ onLoginSuccess }) {
  const navigate = useNavigate()
  const [searchParams] = useSearchParams()
  const { login } = useAuthContext()
  const [message, setMessage] = useState('Completing Google sign in...')

  useEffect(() => {
    let cancelled = false

    const completeLogin = async () => {
      const oauthError = searchParams.get('error')
      if (oauthError) {
        navigate(`/login?error=${encodeURIComponent(oauthError)}`, { replace: true })
        return
      }

      const accessToken = searchParams.get('token') || searchParams.get('accessToken')
      const refreshToken = searchParams.get('refreshToken') || ''

      if (!accessToken) {
        navigate('/login?error=Google%20login%20did%20not%20return%20a%20token.', { replace: true })
        return
      }

      try {
        localStorage.setItem('accessToken', accessToken)
        localStorage.setItem('refreshToken', refreshToken)

        const user = await authApi.getProfile()
        if (cancelled) {
          return
        }

        login(user, { accessToken, refreshToken })

        if (onLoginSuccess) {
          onLoginSuccess(user)
          return
        }

        navigate(getRedirectPathForRole(user?.role), { replace: true })
      } catch (error) {
        localStorage.removeItem('accessToken')
        localStorage.removeItem('refreshToken')
        localStorage.removeItem('user')
        localStorage.removeItem('role')

        const errorMessage = error?.message || 'Google login could not be completed.'
        if (!cancelled) {
          setMessage(errorMessage)
          navigate(`/login?error=${encodeURIComponent(errorMessage)}`, { replace: true })
        }
      }
    }

    completeLogin()

    return () => {
      cancelled = true
    }
  }, [login, navigate, onLoginSuccess, searchParams])

  return (
    <div className="min-h-[60vh] flex items-center justify-center px-6">
      <div className="w-full max-w-md rounded-lg bg-white p-8 text-center shadow-lg">
        <h1 className="text-xl font-bold mb-2" style={{ color: '#000000' }}>Google Sign In</h1>
        <p className="text-sm" style={{ color: '#747474' }}>{message}</p>
      </div>
    </div>
  )
}
