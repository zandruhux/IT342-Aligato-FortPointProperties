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
        path="/messages"
        element={isLoggedIn ? <RegisteredUserMessagesPage /> : <Navigate to="/login" />}
      />

      {/* Agent Routes - Authentication + Agent role required */}
      <Route 
        path="/agent/properties" 
        element={isLoggedIn ? <AgentPropertiesPage onLogout={onLogout} /> : <Navigate to="/login" />} 
      />
      <Route 
        path="/agent/bulletin" 
        element={isLoggedIn ? <AgentBulletin onLogout={onLogout} /> : <Navigate to="/login" />} 
      />
      <Route 
        path="/agent/messages" 
        element={isLoggedIn ? <AgentInboxPage onLogout={onLogout} /> : <Navigate to="/login" />}
      />
      <Route 
        path="/agent/profile" 
        element={isLoggedIn ? <AgentProfile onLogout={onLogout} /> : <Navigate to="/login" />} 
      />

      {/* Admin Routes - Authentication + Admin role required */}
      <Route 
        path="/admin/properties" 
        element={isLoggedIn ? <AdminPropertiesPage onLogout={onLogout} /> : <Navigate to="/login" />} 
      />
      <Route 
        path="/admin/settings" 
        element={isLoggedIn ? <AdminSettings onLogout={onLogout} /> : <Navigate to="/login" />} 
      />
      <Route 
        path="/admin/profile" 
        element={isLoggedIn ? <AdminProfile onLogout={onLogout} /> : <Navigate to="/login" />} 
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
