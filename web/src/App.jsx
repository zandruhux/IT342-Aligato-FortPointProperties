import { useState } from 'react'
import Header from './components/common/Header'
import RegisterPage from './pages/RegisterPage'
import LoginPage from './pages/LoginPage'
import './App.css'

function App() {
  const [currentPage, setCurrentPage] = useState('login')

  return (
    <div className="min-h-screen flex flex-col bg-white">
      <Header />
      <main className="flex-1">
        {currentPage === 'login' ? (
          <LoginPage onSwitchToRegister={() => setCurrentPage('register')} />
        ) : (
          <RegisterPage onSwitchToLogin={() => setCurrentPage('login')} />
        )}
      </main>
    </div>
  )
}

export default App
