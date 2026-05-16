import { useEffect } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import AppLayout from './app/AppLayout'
import AppRoutes from './app/Routes'
import { useAuthContext } from './shared/context/useAuthContext'
import './App.css'

function App() {
  const { isLoggedIn, user, logout, initializeFromStorage } = useAuthContext()
  const navigate = useNavigate()
  const location = useLocation()

  // Initialize auth state from localStorage on mount
  useEffect(() => {
    initializeFromStorage()
  }, [initializeFromStorage])

  // Handle role-based redirects
  useEffect(() => {
    if (isLoggedIn && user && location.pathname === '/') {
      const role = normalizeRole(user.role)
      
      // Redirect ADMIN to admin dashboard
      if (role === 'ADMIN') {
        navigate('/admin/dashboard')
      }
      
      // Redirect AGENT to agent dashboard
      if (role === 'AGENT') {
        navigate('/agent/dashboard')
      }
    }
  }, [isLoggedIn, user, location.pathname, navigate])

  const handleLoginSuccess = (loggedInUser) => {
    // Check user role and navigate accordingly
    const role = normalizeRole(loggedInUser?.role || user?.role)
    
    if (role === 'ADMIN') {
      navigate('/admin/dashboard')
    } else if (role === 'AGENT') {
      navigate('/agent/dashboard')
    } else {
      navigate('/')
    }
  }

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <AppLayout isLoggedIn={isLoggedIn} onLogout={handleLogout}>
      <AppRoutes
        isLoggedIn={isLoggedIn}
        onLogout={handleLogout}
        onLoginSuccess={handleLoginSuccess}
      />
    </AppLayout>
  )
}

const normalizeRole = (role) => {
  if (role === 'registered_user' || role === 'USER') {
    return 'REGISTERED_USER'
  }
  return role || ''
}

export default App
