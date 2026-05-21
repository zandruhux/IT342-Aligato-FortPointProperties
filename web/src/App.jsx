import { Navigate, useNavigate, useLocation } from 'react-router-dom'
import AppLayout from './app/AppLayout'
import AppRoutes from './app/Routes'
import { useAuthContext } from './shared/context/useAuthContext'
import './App.css'

function App() {
  const { isLoggedIn, user, logout, authReady } = useAuthContext()
  const navigate = useNavigate()
  const location = useLocation()

  if (!authReady) {
    return null
  }

  const role = normalizeRole(user?.role)

  if (isLoggedIn && location.pathname === '/' && role === 'ADMIN') {
    return <Navigate to="/admin/properties" replace />
  }

  if (isLoggedIn && location.pathname === '/' && role === 'AGENT') {
    return <Navigate to="/agent/properties" replace />
  }

  const handleLoginSuccess = (loggedInUser) => {
    // Check user role and navigate accordingly
    const nextRole = normalizeRole(loggedInUser?.role || user?.role)
    
    if (nextRole === 'ADMIN') {
      navigate('/admin/properties')
    } else if (nextRole === 'AGENT') {
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

const normalizeRole = (role) => {
  const normalized = String(role || '').trim().toUpperCase().replace(/[-\s]+/g, '_')
  if (normalized === 'REGISTERED_USER' || normalized === 'REGISTERED_USEER' || normalized === 'USER') {
    return 'REGISTERED_USER'
  }
  return normalized || ''
}

export default App
