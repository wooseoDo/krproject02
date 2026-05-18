import { DashboardView } from '../../components/shared/DashboardView';
import { clearAuthenticatedUser, readAuthenticatedUser } from '../../model/authSession';

export default function AdminDashboardPage() {
  const user = readAuthenticatedUser();

  const handleLogout = () => {
    clearAuthenticatedUser();
    window.history.pushState({}, '', '/admin/login');
    window.dispatchEvent(new PopStateEvent('popstate'));
  };

  return (
    <DashboardView
      variant="admin"
      sectionLabel="ADMIN DASHBOARD"
      title="관리자 대시보드"
      description="관리자가 전체 응답 흐름과 계정 상태를 확인하는 첫 화면입니다."
      metrics={[
        { label: '계정 구분', value: '관리자' },
        { label: '생년월일', value: user?.birthDate ?? '-' },
        {
          label: '계정 생성일',
          value: user?.createdAt ? new Date(user.createdAt).toLocaleDateString('ko-KR') : '-',
        },
      ]}
      panelTitle="운영 요약"
      panelItems={[
        '사용자 응답 수집 현황을 연결할 수 있는 관리자 영역입니다.',
        '계정 관리, 설문 버전 관리, 응답 검토 메뉴가 이곳에서 확장됩니다.',
      ]}
      onLogout={handleLogout}
    />
  );
}
