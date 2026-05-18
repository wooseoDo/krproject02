import { DashboardView } from '../../components/shared/DashboardView';
import { clearAuthenticatedUser, readAuthenticatedUser } from '../../model/authSession';

export default function UserDashboardPage() {
  const user = readAuthenticatedUser();

  const handleLogout = () => {
    clearAuthenticatedUser();
    window.history.pushState({}, '', '/login');
    window.dispatchEvent(new PopStateEvent('popstate'));
  };

  return (
    <DashboardView
      sectionLabel="USER DASHBOARD"
      title="사용자 대시보드"
      description="로그인 이후 사용자가 설문과 개인 진행 상태를 확인하는 첫 화면입니다."
      metrics={[
        { label: '계정 구분', value: '일반 사용자' },
        { label: '생년월일', value: user?.birthDate ?? '-' },
        {
          label: '계정 생성일',
          value: user?.createdAt ? new Date(user.createdAt).toLocaleDateString('ko-KR') : '-',
        },
      ]}
      panelTitle="오늘의 진행 현황"
      panelItems={[
        '진행 가능한 설문을 불러올 준비가 되었습니다.',
        '응답 이력과 완료 상태 영역은 다음 단계에서 연결하면 됩니다.',
      ]}
      onLogout={handleLogout}
    />
  );
}
