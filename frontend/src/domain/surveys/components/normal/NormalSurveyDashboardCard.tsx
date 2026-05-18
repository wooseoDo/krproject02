import { useNormalSurveyList } from '../../hooks/normal/useNormalSurveyList';
import { SurveyListCard } from '../shared/SurveyListCard';

export function NormalSurveyDashboardCard() {
  const list = useNormalSurveyList();

  return (
    <SurveyListCard
      title="참여 가능한 조사지"
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
