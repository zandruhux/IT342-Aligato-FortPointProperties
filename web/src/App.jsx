import { useEffect, useState } from 'react'
import { Routes, Route, Navigate, useNavigate, useLocation } from 'react-router-dom'
import Header from './components/common/Header'
import RegisterPage from './pages/auth/RegisterPage'
import LoginPage from './pages/auth/LoginPage'
import HomePage from './pages/public/HomePage'
import PropertyListPage from './pages/public/PropertyListPage'
import FavoritePage from './pages/users/FavoritePage'
import AgentPropertiesPage from './pages/agent/AgentPropertiesPage'
import AgentBulletinPage from './pages/agent/AgentBulletinPage'
import AgentMessagesPage from './pages/agent/AgentMessagesPage'
import AgentArticlesPage from './pages/agent/AgentArticlesPage'
import AgentProfilePage from './pages/agent/AgentProfilePage'
import AdminPropertiesPage from './pages/admin/AdminPropertiesPage'
import AdminArticlesPage from './pages/admin/AdminArticlesPage'
import AdminSettingsPage from './pages/admin/AdminSettingsPage'
import AdminProfilePage from './pages/admin/AdminProfilePage'
import './App.css'

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false)
  const navigate = useNavigate()
  const location = useLocation()

  // Check if user is already logged in on mount
  useEffect(() => {
    const token = localStorage.getItem('accessToken')
    if (token) {
      setIsLoggedIn(true)
      
      // Check if user is an AGENT/ADMIN and redirect if on home page
      const user = localStorage.getItem('user')
      if (user) {
        try {
          const userData = JSON.parse(user)
          const role = userData.roles?.[0] || userData.role || ''
          
          // Redirect ADMIN to admin dashboard
          if (role === 'ADMIN' && location.pathname === '/') {
            navigate('/admin/properties')
          }
          
          // Redirect AGENT to agent dashboard
          if (role === 'AGENT' && location.pathname === '/') {
            navigate('/agent/properties')
          }
        } catch (error) {
          console.error('Error parsing user data:', error)
        }
      }
    }
  }, [navigate, location.pathname])

  const handleLoginSuccess = () => {
    setIsLoggedIn(true)
    
    // Check user role and navigate accordingly
    const user = localStorage.getItem('user')
    if (user) {
      try {
        const userData = JSON.parse(user)
        const role = userData.roles?.[0] || userData.role || ''
        
        // Redirect ADMIN to admin dashboard
        if (role === 'ADMIN') {
          navigate('/admin/properties')
          return
        }
        
        // Redirect AGENT to agent dashboard
        if (role === 'AGENT') {
          navigate('/agent/properties')
          return
        }
      } catch (error) {
        console.error('Error parsing user data:', error)
      }
    }
    
    // Default redirect to home for other users
    navigate('/')
  }

  const handleLogout = () => {
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('user')
    setIsLoggedIn(false)
    navigate('/login')
  }

  return (
    <div className="min-h-screen flex flex-col bg-white">
      {!location.pathname.startsWith('/agent') && !location.pathname.startsWith('/admin') && (
        <Header
          isLoggedIn={isLoggedIn}
          onLogout={handleLogout}
        />
      )}
      <main className="flex-1">
        <Routes>

          {/* Public Routes */}
          <Route path="/" element={<HomePage />}/>
          <Route path="/properties" element={<PropertyListPage />}/>

          {/* Registered Users */}
          <Route path="/favorites" element={isLoggedIn ? <FavoritePage /> : <Navigate to="/login" />}/>

          {/* Agent Routes */}
          <Route path="/agent/properties" element={isLoggedIn ? <AgentPropertiesPage onLogout={handleLogout} /> : <Navigate to="/login" />}/>
          <Route path="/agent/bulletin" element={isLoggedIn ? <AgentBulletinPage onLogout={handleLogout} /> : <Navigate to="/login" />}/>
          <Route path="/agent/messages" element={isLoggedIn ? <AgentMessagesPage onLogout={handleLogout} /> : <Navigate to="/login" />}/>
          <Route path="/agent/articles" element={isLoggedIn ? <AgentArticlesPage onLogout={handleLogout} /> : <Navigate to="/login" />}/>
          <Route path="/agent/profile" element={isLoggedIn ? <AgentProfilePage onLogout={handleLogout} /> : <Navigate to="/login" />}/>

          {/* Admin Routes */}
          <Route path="/admin/properties" element={isLoggedIn ? <AdminPropertiesPage onLogout={handleLogout} /> : <Navigate to="/login" />}/>
          <Route path="/admin/articles" element={isLoggedIn ? <AdminArticlesPage onLogout={handleLogout} /> : <Navigate to="/login" />}/>
          <Route path="/admin/settings" element={isLoggedIn ? <AdminSettingsPage onLogout={handleLogout} /> : <Navigate to="/login" />}/>
          <Route path="/admin/profile" element={isLoggedIn ? <AdminProfilePage onLogout={handleLogout} /> : <Navigate to="/login" />}/>

          {/* Auth Routes */}
          <Route
            path="/login"
            element={isLoggedIn ? <Navigate to="/" /> : <LoginPage onLoginSuccess={handleLoginSuccess} />}
          />

          <Route
            path="/register"
            element={isLoggedIn ? <Navigate to="/" /> : <RegisterPage />}
          />
          
        </Routes>
      </main>
    </div>
  )
}

export default App
