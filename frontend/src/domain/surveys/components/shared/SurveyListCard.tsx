import { SURVEY_MESSAGES } from '../../constants/messages';
import type { SurveyListFilters as SurveyListFiltersValue, SurveyListItem } from '../../types/types';
import { SurveyDataTable } from './SurveyDataTable';
import { SurveyListFilters } from './SurveyListFilters';

interface SurveyListCardProps {
  title?: string;
  filters: SurveyListFiltersValue;
  pageItems: SurveyListItem[];
  page: number;
  totalItems: number;
  totalPages: number;
  isLoading: boolean;
  error: string;
  onFilterChange: (key: keyof SurveyListFiltersValue, value: string) => void;
  onResetFilters: () => void;
  onPageChange: (page: number) => void;
}

export function SurveyListCard({
  title = '조사지 목록',
  filters,
  pageItems,
  page,
  totalItems,
  totalPages,
  isLoading,
  error,
  onFilterChange,
  onResetFilters,
  onPageChange,
}: SurveyListCardProps) {
  return (
    <section className="survey-list-card">
      <div className="survey-list-card__header">
        <h2>{title}</h2>
        <div className="survey-table-summary">
          <span>총 {totalItems.toLocaleString()}개</span>
          <span>최신순 정렬</span>
        </div>
      </div>

      <SurveyListFilters filters={filters} onFilterChange={onFilterChange} onReset={onResetFilters} />

      <div className="survey-table-section">
        {isLoading && <p className="table-state-message">조사지 목록을 불러오는 중입니다.</p>}
        {error && <p className="table-state-message is-error">{SURVEY_MESSAGES.LOAD_ERROR}</p>}
        {!isLoading && !error && <SurveyDataTable data={pageItems} emptyMessage={SURVEY_MESSAGES.EMPTY} />}

        <div className="pagination-bar">
          <button type="button" disabled={page <= 1} onClick={() => onPageChange(page - 1)}>
            이전
          </button>
          <span>
            {page} / {totalPages}
          </span>
          <button type="button" disabled={page >= totalPages} onClick={() => onPageChange(page + 1)}>
            다음
          </button>
        </div>
      </div>
    </section>
  );
}
