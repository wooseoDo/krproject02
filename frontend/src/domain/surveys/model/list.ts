import type { SurveyListFilters, SurveyListItem, SurveyListItemResponse } from '../types/types';

function matchesNumberFilter(value: number | null, filter: string) {
  return filter === '' || String(value ?? '').includes(filter);
}

function matchesDateRange(value: string, from: string, to: string) {
  // Compare YYYY-MM-DD strings so the selected start and end dates are both included.
  const date = value.slice(0, 10);
  return (from === '' || date >= from) && (to === '' || date <= to);
}

export function buildSurveyListItems(surveys: SurveyListItemResponse[]): SurveyListItem[] {
  // Sort by release date descending and assign row numbers from the newest item.
  return [...surveys]
    .sort((left, right) => new Date(right.createdAt).getTime() - new Date(left.createdAt).getTime())
    .map((survey, index) => ({
      ...survey,
      rowNumber: index + 1,
    }));
}

export function filterSurveyListItems(items: SurveyListItem[], filters: SurveyListFilters): SurveyListItem[] {
  return items.filter((item) => {
    const titleMatched = item.title.toLowerCase().includes(filters.title.trim().toLowerCase());
    const statusMatched = filters.status === '' || item.status === filters.status;

    return (
      titleMatched &&
      statusMatched &&
      matchesNumberFilter(item.maxScore, filters.maxScore) &&
      matchesNumberFilter(item.estimatedTimeSec, filters.estimatedTimeSec) &&
      matchesNumberFilter(item.surveyVersion, filters.surveyVersion) &&
      matchesDateRange(item.createdAt, filters.releasedAtFrom, filters.releasedAtTo)
    );
  });
}

export function paginateSurveyListItems(items: SurveyListItem[], page: number, pageSize: number) {
  const start = (page - 1) * pageSize;
  return items.slice(start, start + pageSize);
}
