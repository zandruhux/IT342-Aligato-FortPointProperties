import React from 'react'
import { useLocation } from 'react-router-dom'
import Header from '../shared/components/layout/Header'
import FloatingChatWidget from '../features/messaging/components/FloatingChatWidget'
import { useAuthContext } from '../shared/context/useAuthContext'

/**
 * AppLayout - Global application layout wrapper
 * Manages global UI shell (Header, Sidebar, Main content area)
 * Role-based visibility rules:
 * - Hide Header for /agent/* and /admin/* routes
 * - Public routes show full layout with Header
 */
const AppLayout = ({ children, isLoggedIn, onLogout }) => {
  const location = useLocation()
  const { user } = useAuthContext()
  const role = normalizeRole(user?.role)
  
  // Hide header for agent and admin routes (these have their own sidebar/layout)
  const hideHeader = location.pathname.startsWith('/agent')
    || location.pathname.startsWith('/admin')
    || ((role === 'ADMIN' || role === 'AGENT') && location.pathname.startsWith('/blogs'))

  return (
    <div className="min-h-screen flex flex-col bg-white">
      {!hideHeader && (
        <Header
          isLoggedIn={isLoggedIn}
          onLogout={onLogout}
        />
      )}
      <main className="flex-1">
        {children}
      </main>
      <FloatingChatWidget />
    </div>
  )
}

const normalizeRole = (role) => {
  if (role === 'registered_user' || role === 'USER' || role === 'REGISTERED_USER') {
    return 'REGISTERED_USER'
  }
  return role || ''
}

export default AppLayout
