import React from 'react'
import { useLocation } from 'react-router-dom'
import Header from '../shared/components/layout/Header'
import FloatingChatWidget from '../features/messaging/components/FloatingChatWidget'

/**
 * AppLayout - Global application layout wrapper
 * Manages global UI shell (Header, Sidebar, Main content area)
 * Role-based visibility rules:
 * - Hide Header for /agent/* and /admin/* routes
 * - Public routes show full layout with Header
 */
const AppLayout = ({ children, isLoggedIn, onLogout }) => {
  const location = useLocation()
  
  // Hide header for agent and admin routes (these have their own sidebar/layout)
  const hideHeader = location.pathname.startsWith('/agent') || location.pathname.startsWith('/admin')

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

export default AppLayout
