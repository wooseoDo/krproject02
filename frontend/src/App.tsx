import './App.css'
import AdminDashboardPage from './domain/users/pages/admin/AdminDashboardPage'
import AdminLoginPage from './domain/users/pages/admin/AdminLoginPage'
import UserDashboardPage from './domain/users/pages/normal/UserDashboardPage'
import UserLoginPage from './domain/users/pages/normal/UserLoginPage'
import { readAuthenticatedUser } from './domain/users/model/authSession'
import { useEffect, useState } from 'react'

function App() {
  const [path, setPath] = useState(window.location.pathname)

  useEffect(() => {
    const handleLocationChange = () => setPath(window.location.pathname)

    window.addEventListener('popstate', handleLocationChange)
    return () => window.removeEventListener('popstate', handleLocationChange)
  }, [])

  if (path === '/admin/login') {
    return <AdminLoginPage />
  }

  if (path === '/admin/dashboard') {
    const user = readAuthenticatedUser()
    return user?.scope === 'admin' ? <AdminDashboardPage /> : <AdminLoginPage />
  }

  if (path === '/dashboard') {
    const user = readAuthenticatedUser()
    return user?.scope === 'normal' ? <UserDashboardPage /> : <UserLoginPage />
  }

  return <UserLoginPage />
}

export default App
