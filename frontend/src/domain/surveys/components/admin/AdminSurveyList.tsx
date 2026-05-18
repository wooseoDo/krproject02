import { clearAuthenticatedUser, readAuthenticatedUser } from '../../../users/model/authSession';
import { useAdminSurveyList } from '../../hooks/admin/useAdminSurveyList';
import { SurveyListView } from '../shared/SurveyListView';

export function AdminSurveyList() {
  const user = readAuthenticatedUser();
  const list = useAdminSurveyList();

  const handleLogout = () => {
    clearAuthenticatedUser();
    window.history.pushState({}, '', '/admin/login');
    window.dispatchEvent(new PopStateEvent('popstate'));
  };

  return (
    <SurveyListView
      title="관리자 조사지 조회"
      description="전체 조사지 목록을 상태와 버전까지 포함해 최신 출시일 기준으로 확인합니다."
      accountType="관리자"
      birthDate={user?.birthDate ?? '-'}
      accountInfo={user?.createdAt ? new Date(user.createdAt).toLocaleDateString('ko-KR') : '-'}
      dashboardPath="/admin/dashboard"
      surveyPath="/admin/surveys"
      filters={list.filters}
      pageItems={list.pageItems}
      page={list.page}
      totalItems={list.totalItems}
      totalPages={list.totalPages}
      isLoading={list.isLoading}
      error={list.error}
      onLogout={handleLogout}
      onFilterChange={list.updateFilter}
      onResetFilters={list.resetFilters}
      onPageChange={list.setPage}
    />
  );
}
