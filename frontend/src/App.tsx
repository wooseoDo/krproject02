import './App.css'
import AdminDashboardPage from './domain/users/pages/admin/AdminDashboardPage'
import AdminLoginPage from './domain/users/pages/admin/AdminLoginPage'
import UserDashboardPage from './domain/users/pages/normal/UserDashboardPage'
import UserLoginPage from './domain/users/pages/normal/UserLoginPage'
import { readAuthenticatedUser } from './domain/users/model/authSession'
import { useEffect, useState } from 'react'

// 현재 URL 경로에 맞는 로그인/대시보드 화면을 렌더링합니다.
function App() {
  const [path, setPath] = useState(window.location.pathname)

  useEffect(() => {
    // 브라우저 히스토리 변경 시 현재 경로 상태를 갱신합니다.
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
