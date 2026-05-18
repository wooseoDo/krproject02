import { AuthHeader } from '../../../../common/components/layout/AuthHeader';
import type { SurveyListFilters as SurveyListFiltersValue, SurveyListItem } from '../../types/types';
import { SurveyListCard } from './SurveyListCard';

interface SurveyListViewProps {
  title: string;
  description: string;
  accountType: string;
  birthDate: string;
  accountInfo: string;
  dashboardPath: string;
  surveyPath: string;
  filters: SurveyListFiltersValue;
  pageItems: SurveyListItem[];
  page: number;
  totalItems: number;
  totalPages: number;
  isLoading: boolean;
  error: string;
  onLogout: () => void;
  onFilterChange: (key: keyof SurveyListFiltersValue, value: string) => void;
  onResetFilters: () => void;
  onPageChange: (page: number) => void;
}

export function SurveyListView({
  title,
  description,
  accountType,
  birthDate,
  accountInfo,
  dashboardPath,
  surveyPath,
  filters,
  pageItems,
  page,
  totalItems,
  totalPages,
  isLoading,
  error,
  onLogout,
  onFilterChange,
  onResetFilters,
  onPageChange,
}: SurveyListViewProps) {
  return (
    <main className="survey-page-shell">
      <AuthHeader
        accountType={accountType}
        birthDate={birthDate}
        accountInfo={accountInfo}
        dashboardPath={dashboardPath}
        surveyPath={surveyPath}
        onLogout={onLogout}
      />

      <section className="survey-page-header">
        <p className="section-label">SURVEYS</p>
        <h1>{title}</h1>
        <p>{description}</p>
      </section>

      <SurveyListCard
        filters={filters}
        pageItems={pageItems}
        page={page}
        totalItems={totalItems}
        totalPages={totalPages}
        isLoading={isLoading}
        error={error}
        onFilterChange={onFilterChange}
        onResetFilters={onResetFilters}
        onPageChange={onPageChange}
      />
    </main>
  );
}
