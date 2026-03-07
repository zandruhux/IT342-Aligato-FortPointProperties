import Header from './components/common/Header'
import RegisterPage from './pages/RegisterPage'
import './App.css'

function App() {
  return (
    <div className="min-h-screen flex flex-col bg-white">
      <Header />
      <main className="flex-1">
        <RegisterPage />
      </main>
    </div>
  )
}

export default App
