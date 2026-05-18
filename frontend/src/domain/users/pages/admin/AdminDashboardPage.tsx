import { AdminSurveyDashboardCard } from '../../../surveys/components/admin/AdminSurveyDashboardCard';
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
      description="관리자가 전체 조사지와 응답 흐름을 확인하는 첫 화면입니다."
      accountType="관리자"
      birthDate={user?.birthDate ?? '-'}
      accountInfo={user?.createdAt ? new Date(user.createdAt).toLocaleDateString('ko-KR') : '-'}
      dashboardPath="/admin/dashboard"
      surveyPath="/admin/surveys"
      metrics={[
        { label: '전체 조사지', value: '바로 조회' },
        { label: '운영 상태', value: '대기' },
        { label: '최근 변경', value: '-' },
      ]}
      panelTitle="운영 요약"
      panelItems={[
        '대시보드에서 전체 조사지 목록과 상태를 바로 확인할 수 있습니다.',
        '계정 관리, 설문 버전 관리, 응답 검토 메뉴가 이곳에서 확장됩니다.',
      ]}
      onLogout={handleLogout}
    >
      <AdminSurveyDashboardCard />
    </DashboardView>
  );
}
