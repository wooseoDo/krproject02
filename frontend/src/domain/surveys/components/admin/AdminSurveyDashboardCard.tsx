import { useAdminSurveyList } from '../../hooks/admin/useAdminSurveyList';
import { SurveyListCard } from '../shared/SurveyListCard';

export function AdminSurveyDashboardCard() {
  const list = useAdminSurveyList();

  return (
    <SurveyListCard
      title="전체 조사지"
      filters={list.filters}
      pageItems={list.pageItems}
      page={list.page}
      totalItems={list.totalItems}
      totalPages={list.totalPages}
      isLoading={list.isLoading}
      error={list.error}
      onFilterChange={list.updateFilter}
      onResetFilters={list.resetFilters}
      onPageChange={list.setPage}
    />
  );
}
