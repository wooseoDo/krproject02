import { ResponsiveSearchFilter } from '../../../../common/components/filters/ResponsiveSearchFilter';
import { SURVEY_STATUS_LABELS } from '../../constants/statusLabels';
import type { SurveyListFilters as SurveyListFiltersValue, SurveyStatus } from '../../types/types';

const statusOptions = [
  { value: '', label: '전체' },
  ...(['DRAFT', 'PUBLISHED', 'LOCKED', 'CLOSED'] as SurveyStatus[]).map((status) => ({
    value: status,
    label: SURVEY_STATUS_LABELS[status],
  })),
];

interface SurveyListFiltersProps {
  filters: SurveyListFiltersValue;
  onFilterChange: (key: keyof SurveyListFiltersValue, value: string) => void;
  onReset: () => void;
}

export function SurveyListFilters({ filters, onFilterChange, onReset }: SurveyListFiltersProps) {
  return (
    <ResponsiveSearchFilter
      fields={[
        {
          id: 'title',
          label: '조사지 제목',
          type: 'text',
          value: filters.title,
          placeholder: '제목 검색',
          onChange: (value) => onFilterChange('title', value),
        },
        {
          id: 'maxScore',
          label: '최고점수',
          type: 'number',
          value: filters.maxScore,
          placeholder: '예: 30',
          onChange: (value) => onFilterChange('maxScore', value),
        },
        {
          id: 'estimatedTimeSec',
          label: '시간',
          type: 'number',
          value: filters.estimatedTimeSec,
          placeholder: '초 단위',
          onChange: (value) => onFilterChange('estimatedTimeSec', value),
        },
        {
          id: 'surveyVersion',
          label: '버전',
          type: 'number',
          value: filters.surveyVersion,
          placeholder: '예: 1',
          onChange: (value) => onFilterChange('surveyVersion', value),
        },
        {
          id: 'status',
          label: '상태',
          type: 'select',
          value: filters.status,
          options: statusOptions,
          onChange: (value) => onFilterChange('status', value),
        },
        {
          id: 'releasedAtFrom',
          label: '출시일 시작',
          type: 'date',
          value: filters.releasedAtFrom,
          onChange: (value) => onFilterChange('releasedAtFrom', value),
        },
        {
          id: 'releasedAtTo',
          label: '출시일 종료',
          type: 'date',
          value: filters.releasedAtTo,
          onChange: (value) => onFilterChange('releasedAtTo', value),
        },
      ]}
      onReset={onReset}
    />
  );
}
