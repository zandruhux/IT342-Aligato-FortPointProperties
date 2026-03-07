import { useState, useEffect } from 'react'
import Header from './components/common/Header'
import RegisterPage from './pages/RegisterPage'
import LoginPage from './pages/LoginPage'
import HomePage from './pages/HomePage'
import './App.css'

function App() {
  const [currentPage, setCurrentPage] = useState('login')
  const [isLoggedIn, setIsLoggedIn] = useState(false)

  // Check if user is already logged in on mount
  useEffect(() => {
    const token = localStorage.getItem('token')
    if (token) {
      setIsLoggedIn(true)
      setCurrentPage('home')
    }
  }, [])

  const handleLoginSuccess = () => {
    setIsLoggedIn(true)
    setCurrentPage('home')
  }

  const handleLogout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    setIsLoggedIn(false)
    setCurrentPage('login')
  }

  const renderPage = () => {
    if (isLoggedIn) {
      return <HomePage />
    }

    if (currentPage === 'register') {
      return <RegisterPage onSwitchToLogin={() => setCurrentPage('login')} />
    }

    return (
      <LoginPage
        onSwitchToRegister={() => setCurrentPage('register')}
        onLoginSuccess={handleLoginSuccess}
      />
    )
  }

  return (
    <div className="min-h-screen flex flex-col bg-white">
      <Header
        isLoggedIn={isLoggedIn}
        onSignIn={() => setCurrentPage('login')}
        onLogout={handleLogout}
      />
      <main className="flex-1">
        {renderPage()}
      </main>
    </div>
  )
}

export default App
