import { useEffect } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import AppLayout from './app/AppLayout'
import AppRoutes from './app/Routes'
import { useAuthContext } from './shared/context/AuthContext'
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
      const role = user.role || ''
      
      // Redirect ADMIN to admin dashboard
      if (role === 'ADMIN') {
        navigate('/admin/properties')
      }
      
      // Redirect AGENT to agent dashboard
      if (role === 'AGENT') {
        navigate('/agent/properties')
      }
    }
  }, [isLoggedIn, user, location.pathname, navigate])

  const handleLoginSuccess = () => {
    // Check user role and navigate accordingly
    const role = user?.role || ''
    
    if (role === 'ADMIN') {
      navigate('/admin/properties')
    } else if (role === 'AGENT') {
      navigate('/agent/properties')
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

export default App
