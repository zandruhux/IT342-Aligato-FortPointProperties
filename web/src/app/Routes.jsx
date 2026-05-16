import React from 'react'
import { Routes, Route, Navigate } from 'react-router-dom'
import { RegisterPage, LoginPage } from '../features/auth/pages'
import HomePage from '../features/public/pages'
import { PropertyListPage } from '../features/properties/pages'
import { FavoritePage } from '../features/favorites/pages'
import { AgentPropertiesListPage as AgentPropertiesPage } from '../features/properties/pages'
import { AgentBulletin } from '../features/bulletin'
import { AgentInboxPage, RegisteredUserMessagesPage } from '../features/messaging'
import { AgentProfile } from '../features/profile/pages'
import { AdminPropertiesListPage as AdminPropertiesPage } from '../features/properties/pages'
import { AdminSettings } from '../features/settings'
import { AdminProfile } from '../features/profile/pages'
import {
  AdminCareerApplicationDetailsPage,
  AdminCareerApplicationsPage,
  AgentDashboardPage,
  CareerApplicationPage,
} from '../features/careerApplication'
import { useAuthContext } from '../shared/context/useAuthContext'

const normalizeRole = (role) => {
  if (role === 'registered_user' || role === 'USER') {
    return 'REGISTERED_USER'
  }
  return role || ''
}

const RequireRole = ({ isLoggedIn, allowedRoles, children }) => {
  const { user } = useAuthContext()

  if (!isLoggedIn) {
    return <Navigate to="/login" />
  }

  if (!allowedRoles.includes(normalizeRole(user?.role))) {
    return <Navigate to="/" replace />
  }

  return children
}

/**
 * AppRoutes - Centralized route definitions
 * All routes are defined in one place for easier maintenance
 * 
 * Props:
 * - isLoggedIn: Boolean indicating if user is authenticated
 * - onLogout: Callback function for logout action
 * - onLoginSuccess: Callback function for successful login
 */
const AppRoutes = ({ isLoggedIn, onLogout, onLoginSuccess }) => {
  return (
    <Routes>
      {/* Public Routes - No authentication required */}
      <Route path="/" element={<HomePage />} />
      <Route path="/properties" element={<PropertyListPage />} />

      {/* Registered Users - Authentication required */}
      <Route 
        path="/favorites" 
        element={isLoggedIn ? <FavoritePage /> : <Navigate to="/login" />} 
      />
      <Route
        path="/career"
        element={
          <RequireRole isLoggedIn={isLoggedIn} allowedRoles={['REGISTERED_USER']}>
            <CareerApplicationPage />
          </RequireRole>
        }
      />
      <Route
        path="/messages"
        element={isLoggedIn ? <RegisteredUserMessagesPage /> : <Navigate to="/login" />}
      />

      {/* Agent Routes - Authentication + Agent role required */}
      <Route
        path="/agent/dashboard"
        element={
          <RequireRole isLoggedIn={isLoggedIn} allowedRoles={['AGENT']}>
            <AgentDashboardPage onLogout={onLogout} />
          </RequireRole>
        }
      />
      <Route 
        path="/agent/properties" 
        element={
          <RequireRole isLoggedIn={isLoggedIn} allowedRoles={['AGENT']}>
            <AgentPropertiesPage onLogout={onLogout} />
          </RequireRole>
        } 
      />
      <Route 
        path="/agent/bulletin" 
        element={
          <RequireRole isLoggedIn={isLoggedIn} allowedRoles={['AGENT']}>
            <AgentBulletin onLogout={onLogout} />
          </RequireRole>
        } 
      />
      <Route 
        path="/agent/messages" 
        element={
          <RequireRole isLoggedIn={isLoggedIn} allowedRoles={['AGENT']}>
            <AgentInboxPage onLogout={onLogout} />
          </RequireRole>
        }
      />
      <Route 
        path="/agent/profile" 
        element={
          <RequireRole isLoggedIn={isLoggedIn} allowedRoles={['AGENT']}>
            <AgentProfile onLogout={onLogout} />
          </RequireRole>
        } 
      />

      {/* Admin Routes - Authentication + Admin role required */}
      <Route
        path="/admin/dashboard"
        element={
          <RequireRole isLoggedIn={isLoggedIn} allowedRoles={['ADMIN']}>
            <AdminPropertiesPage onLogout={onLogout} />
          </RequireRole>
        }
      />
      <Route 
        path="/admin/properties" 
        element={
          <RequireRole isLoggedIn={isLoggedIn} allowedRoles={['ADMIN']}>
            <AdminPropertiesPage onLogout={onLogout} />
          </RequireRole>
        } 
      />
      <Route
        path="/admin/career-applications"
        element={
          <RequireRole isLoggedIn={isLoggedIn} allowedRoles={['ADMIN']}>
            <AdminCareerApplicationsPage />
          </RequireRole>
        }
      />
      <Route
        path="/admin/career-applications/:id"
        element={
          <RequireRole isLoggedIn={isLoggedIn} allowedRoles={['ADMIN']}>
            <AdminCareerApplicationDetailsPage />
          </RequireRole>
        }
      />
      <Route 
        path="/admin/settings" 
        element={
          <RequireRole isLoggedIn={isLoggedIn} allowedRoles={['ADMIN']}>
            <AdminSettings onLogout={onLogout} />
          </RequireRole>
        } 
      />
      <Route 
        path="/admin/profile" 
        element={
          <RequireRole isLoggedIn={isLoggedIn} allowedRoles={['ADMIN']}>
            <AdminProfile onLogout={onLogout} />
          </RequireRole>
        } 
      />

      {/* Auth Routes - Only accessible when not logged in */}
      <Route
        path="/login"
        element={isLoggedIn ? <Navigate to="/" /> : <LoginPage onLoginSuccess={onLoginSuccess} />}
      />

      <Route
        path="/register"
        element={isLoggedIn ? <Navigate to="/" /> : <RegisterPage />}
      />
    </Routes>
  )
}

export default AppRoutes
