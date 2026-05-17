import { clearAuthenticatedUser, readAuthenticatedUser } from '../../model/authSession';

// 관리자 로그인 이후의 대시보드 화면을 렌더링합니다.
export default function AdminDashboardPage() {
  const user = readAuthenticatedUser();

  // 인증 세션을 제거하고 관리자 로그인 화면으로 이동합니다.
  const handleLogout = () => {
    clearAuthenticatedUser();
    window.history.pushState({}, '', '/admin/login');
    window.dispatchEvent(new PopStateEvent('popstate'));
  };

  return (
    <main className="dashboard-shell dashboard-shell--admin">
      <section className="dashboard-hero">
        <p className="section-label">ADMIN DASHBOARD</p>
        <h1>관리자 대시보드</h1>
        <p>관리자가 전체 응답 흐름과 계정 상태를 확인하는 첫 화면입니다.</p>
      </section>

      <section className="dashboard-grid">
        <article className="metric-card">
          <span>계정 구분</span>
          <strong>관리자</strong>
        </article>
        <article className="metric-card">
          <span>생년월일</span>
          <strong>{user?.birthDate ?? '-'}</strong>
        </article>
        <article className="metric-card">
          <span>계정 상태</span>
          <strong>{user?.active ? '활성' : '-'}</strong>
        </article>
      </section>

      <section className="work-panel">
        <h2>운영 요약</h2>
        <div className="status-list">
          <p>사용자 응답 수집 현황을 연결할 수 있는 관리자 영역입니다.</p>
          <p>계정 관리, 설문 버전 관리, 응답 검색 메뉴가 이곳에서 확장됩니다.</p>
        </div>
      </section>

      <button className="secondary-action" type="button" onClick={handleLogout}>
        로그아웃
      </button>
    </main>
  );
}
