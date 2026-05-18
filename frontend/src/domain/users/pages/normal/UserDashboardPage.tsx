import { NormalSurveyDashboardCard } from '../../../surveys/components/normal/NormalSurveyDashboardCard';
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
      description="로그인 이후 사용자가 조사지와 개인 진행 상태를 확인하는 첫 화면입니다."
      accountType="일반 사용자"
      birthDate={user?.birthDate ?? '-'}
      accountInfo={user?.createdAt ? new Date(user.createdAt).toLocaleDateString('ko-KR') : '-'}
      dashboardPath="/dashboard"
      surveyPath="/surveys"
      metrics={[
        { label: '참여 가능 조사지', value: '바로 조회' },
        { label: '진행 상태', value: '대기' },
        { label: '최근 활동', value: '-' },
      ]}
      panelTitle="오늘의 진행 현황"
      panelItems={[
        '대시보드에서 참여 가능한 조사지 목록을 바로 확인할 수 있습니다.',
        '응답 이력과 완료 상태 영역은 다음 단계에서 연결하면 됩니다.',
      ]}
      onLogout={handleLogout}
    >
      <NormalSurveyDashboardCard />
    </DashboardView>
  );
}
