import { clearAuthenticatedUser, readAuthenticatedUser } from '../../model/authSession';

// 일반 사용자 로그인 이후의 대시보드 화면을 렌더링합니다.
export default function UserDashboardPage() {
  const user = readAuthenticatedUser();

  // 인증 세션을 제거하고 일반 사용자 로그인 화면으로 이동합니다.
  const handleLogout = () => {
    clearAuthenticatedUser();
    window.history.pushState({}, '', '/login');
    window.dispatchEvent(new PopStateEvent('popstate'));
  };

  return (
    <main className="dashboard-shell">
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
          <p>응답 이력과 완료 상태 영역은 다음 단계에서 연결하면 됩니다.</p>
        </div>
      </section>

      <button className="secondary-action" type="button" onClick={handleLogout}>
        로그아웃
      </button>
    </main>
  );
}
