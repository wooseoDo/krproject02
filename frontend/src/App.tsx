import { useEffect, useState } from 'react';
import './App.css';
import AdminSurveyListPage from './domain/surveys/pages/admin/AdminSurveyListPage';
import SurveyListPage from './domain/surveys/pages/normal/SurveyListPage';
import AdminDashboardPage from './domain/users/pages/admin/AdminDashboardPage';
import AdminLoginPage from './domain/users/pages/admin/AdminLoginPage';
import UserDashboardPage from './domain/users/pages/normal/UserDashboardPage';
import UserLoginPage from './domain/users/pages/normal/UserLoginPage';
import { readAuthenticatedUser } from './domain/users/model/authSession';

function ApiRouteNotice() {
  return (
    <main className="route-notice-shell">
      <section className="route-notice-panel">
        <p className="section-label">API ROUTE</p>
        <h1>프론트 화면 경로가 아닙니다</h1>
        <p>
          이 주소는 백엔드 API 호출에 사용됩니다. 로그인 화면은 사용자
          <a href="/login"> /login</a>, 관리자는
          <a href="/admin/login"> /admin/login</a>에서 접근해 주세요.
        </p>
      </section>
    </main>
  );
}

function NotFoundPage() {
  return (
    <main className="route-notice-shell">
      <section className="route-notice-panel">
        <p className="section-label">NOT FOUND</p>
        <h1>페이지를 찾을 수 없습니다</h1>
        <p>
          사용자 로그인은 <a href="/login">/login</a>, 관리자 로그인은
          <a href="/admin/login">/admin/login</a>입니다.
        </p>
      </section>
    </main>
  );
}

function App() {
  const [path, setPath] = useState(window.location.pathname);

  useEffect(() => {
    const handleLocationChange = () => setPath(window.location.pathname);

    window.addEventListener('popstate', handleLocationChange);
    return () => window.removeEventListener('popstate', handleLocationChange);
  }, []);

  if (path === '/' || path === '/login') {
    return <UserLoginPage />;
  }

  if (path === '/admin/login') {
    return <AdminLoginPage />;
  }

  if (path === '/dashboard') {
    const user = readAuthenticatedUser();
    return user?.scope === 'normal' ? <UserDashboardPage /> : <UserLoginPage />;
  }

  if (path === '/admin/dashboard') {
    const user = readAuthenticatedUser();
    return user?.scope === 'admin' ? <AdminDashboardPage /> : <AdminLoginPage />;
  }

  if (path === '/surveys') {
    const user = readAuthenticatedUser();
    return user?.scope === 'normal' ? <SurveyListPage /> : <UserLoginPage />;
  }

  if (path === '/admin/surveys') {
    const user = readAuthenticatedUser();
    return user?.scope === 'admin' ? <AdminSurveyListPage /> : <AdminLoginPage />;
  }

  if (path.startsWith('/api/')) {
    return <ApiRouteNotice />;
  }

  return <NotFoundPage />;
}

export default App;
