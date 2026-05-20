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
    return <Navigate to="/admin/dashboard" replace />
  }

  if (isLoggedIn && location.pathname === '/' && role === 'AGENT') {
    return <Navigate to="/agent/dashboard" replace />
  }

  const handleLoginSuccess = (loggedInUser) => {
    // Check user role and navigate accordingly
    const nextRole = normalizeRole(loggedInUser?.role || user?.role)
    
    if (nextRole === 'ADMIN') {
      navigate('/admin/dashboard')
    } else if (nextRole === 'AGENT') {
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
  if (role === 'registered_user' || role === 'USER' || role === 'REGISTERED_USER') {
    return 'REGISTERED_USER'
  }
  return role || ''
}

export default App
