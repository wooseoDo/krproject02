import { DataTable, type DataTableColumn } from '../../../../common/components/table/DataTable';
import { SURVEY_STATUS_LABELS } from '../../constants/statusLabels';
import type { SurveyListItem } from '../../types/types';

function formatSeconds(seconds: number | null) {
  if (seconds === null) {
    return '-';
  }

  if (seconds < 60) {
    return `${seconds}초`;
  }

  const minutes = Math.floor(seconds / 60);
  const remainderSeconds = seconds % 60;

  return remainderSeconds === 0 ? `${minutes}분` : `${minutes}분 ${remainderSeconds}초`;
}

function formatDate(value: string) {
  return new Date(value).toLocaleDateString('ko-KR');
}

const surveyColumns: DataTableColumn<SurveyListItem>[] = [
  {
    id: 'rowNumber',
    header: '번호',
    cell: (survey) => survey.rowNumber,
  },
  {
    id: 'title',
    header: '조사지 제목',
    cell: (survey) => survey.title,
  },
  {
    id: 'maxScore',
    header: '최고점수',
    cell: (survey) => survey.maxScore,
  },
  {
    id: 'estimatedTimeSec',
    header: '시간',
    cell: (survey) => formatSeconds(survey.estimatedTimeSec),
  },
  {
    id: 'surveyVersion',
    header: '버전',
    cell: (survey) => `v${survey.surveyVersion}`,
  },
  {
    id: 'status',
    header: '상태',
    cell: (survey) => SURVEY_STATUS_LABELS[survey.status],
  },
  {
    id: 'createdAt',
    header: '출시일',
    cell: (survey) => formatDate(survey.createdAt),
  },
];

interface SurveyDataTableProps {
  data: SurveyListItem[];
  emptyMessage: string;
}

export function SurveyDataTable({ data, emptyMessage }: SurveyDataTableProps) {
  return (
    <DataTable
      data={data}
      columns={surveyColumns}
      keyExtractor={(survey) => survey.surveyId ?? `${survey.title}-${survey.surveyVersion}`}
      emptyMessage={emptyMessage}
    />
  );
}
