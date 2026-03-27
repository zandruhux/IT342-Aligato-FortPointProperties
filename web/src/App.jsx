import { useEffect, useState } from 'react'
import { Routes, Route, Navigate, useNavigate } from 'react-router-dom'
import Header from './components/common/Header'
import RegisterPage from './pages/RegisterPage'
import LoginPage from './pages/LoginPage'
import HomePage from './pages/HomePage'
import './App.css'
import { GoogleOAuthProvider } from '@react-oauth/google'


function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false)
  const navigate = useNavigate()

  // Check if user is already logged in on mount
  useEffect(() => {
    const token = localStorage.getItem('accessToken')
    if (token) {
      setIsLoggedIn(true)
    }
  }, [])

  const handleLoginSuccess = () => {
    setIsLoggedIn(true)
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
      <Header
        isLoggedIn={isLoggedIn}
        onLogout={handleLogout}
      />
      <main className="flex-1">
        <Routes>
          <Route
            path="/login"
            element={isLoggedIn ? <Navigate to="/" /> : <LoginPage onLoginSuccess={handleLoginSuccess} />}
          />
          <Route
            path="/register"
            element={isLoggedIn ? <Navigate to="/" /> : <RegisterPage />}
          />
          <Route
            path="/"
            element={isLoggedIn ? <HomePage /> : <Navigate to="/login" />}
          />
        </Routes>
      </main>
    </div>
  )
}

export default App
