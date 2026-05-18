import { clearAuthenticatedUser, readAuthenticatedUser } from '../../../users/model/authSession';
import { useNormalSurveyList } from '../../hooks/normal/useNormalSurveyList';
import { SurveyListView } from '../shared/SurveyListView';

export function NormalSurveyList() {
  const user = readAuthenticatedUser();
  const list = useNormalSurveyList();

  const handleLogout = () => {
    clearAuthenticatedUser();
    window.history.pushState({}, '', '/login');
    window.dispatchEvent(new PopStateEvent('popstate'));
  };

  return (
    <SurveyListView
      title="사용자 조사지 조회"
      description="참여 가능한 조사지 목록을 최신 출시일 기준으로 확인합니다."
      accountType="일반 사용자"
      birthDate={user?.birthDate ?? '-'}
      accountInfo={user?.createdAt ? new Date(user.createdAt).toLocaleDateString('ko-KR') : '-'}
      dashboardPath="/dashboard"
      surveyPath="/surveys"
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
