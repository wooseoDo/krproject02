import { clearAuthenticatedUser, readAuthenticatedUser } from '../../model/authSession';

export default function UserDashboardPage() {
  const user = readAuthenticatedUser();

  const handleLogout = () => {
    clearAuthenticatedUser();
    window.history.pushState({}, '', '/login');
    window.dispatchEvent(new PopStateEvent('popstate'));
  };

  return (
    <main className="dashboard-shell">
      <nav className="dashboard-nav">
        <a href="/login">사용자 로그인</a>
        <a href="/admin/login">관리자 로그인</a>
      </nav>

      <section className="dashboard-hero">
        <p className="section-label">USER DASHBOARD</p>
        <h1>사용자 대시보드</h1>
        <p>로그인 이후 사용자가 설문과 개인 진행 상태를 확인하는 첫 화면입니다.</p>
      </section>

      <section className="dashboard-grid">
        <article className="metric-card">
          <span>계정 구분</span>
          <strong>일반 사용자</strong>
        </article>
        <article className="metric-card">
          <span>생년월일</span>
          <strong>{user?.birthDate ?? '-'}</strong>
        </article>
        <article className="metric-card">
          <span>계정 생성일</span>
          <strong>{user?.createdAt ? new Date(user.createdAt).toLocaleDateString('ko-KR') : '-'}</strong>
        </article>
      </section>

      <section className="work-panel">
        <h2>오늘의 진행 현황</h2>
        <div className="status-list">
          <p>진행 가능한 설문을 불러올 준비가 되었습니다.</p>
          <p>응답 내역과 완료 상태 영역을 이곳에 연결하면 됩니다.</p>
        </div>
      </section>

      <button className="secondary-action" type="button" onClick={handleLogout}>
        로그아웃
      </button>
    </main>
  );
}
