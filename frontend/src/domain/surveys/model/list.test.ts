import { describe, expect, it } from 'vitest';
import { buildSurveyListItems, filterSurveyListItems } from './list';
import type { SurveyListFilters, SurveyListItemResponse } from '../types/types';

const defaultFilters: SurveyListFilters = {
  title: '',
  maxScore: '',
  estimatedTimeSec: '',
  surveyVersion: '',
  status: '',
  releasedAtFrom: '',
  releasedAtTo: '',
};

const surveys: SurveyListItemResponse[] = [
  {
    surveyId: '019b1000-0000-7000-8000-000000000001',
    title: '첫 번째 조사지',
    surveyVersion: 1,
    status: 'PUBLISHED',
    maxScore: 30,
    category: null,
    estimatedTimeSec: 600,
    createdAt: '2026-05-18T09:00:00+09:00',
  },
  {
    surveyId: '019b1000-0000-7000-8000-000000000002',
    title: '두 번째 조사지',
    surveyVersion: 2,
    status: 'DRAFT',
    maxScore: 40,
    category: null,
    estimatedTimeSec: 300,
    createdAt: '2026-05-19T09:00:00+09:00',
  },
  {
    surveyId: '019b1000-0000-7000-8000-000000000003',
    title: '세 번째 조사지',
    surveyVersion: 3,
    status: 'CLOSED',
    maxScore: 50,
    category: null,
    estimatedTimeSec: 900,
    createdAt: '2026-05-20T09:00:00+09:00',
  },
];

describe('survey list model', () => {
  it('assigns row numbers from the backend paged order', () => {
    const items = buildSurveyListItems(surveys, 2, 10);

    expect(items.map((item) => item.title)).toEqual(['첫 번째 조사지', '두 번째 조사지', '세 번째 조사지']);
    expect(items.map((item) => item.rowNumber)).toEqual([11, 12, 13]);
  });

  it('filters release dates between start and end dates inclusively', () => {
    const items = buildSurveyListItems(surveys);
    const filteredItems = filterSurveyListItems(items, {
      ...defaultFilters,
      releasedAtFrom: '2026-05-18',
      releasedAtTo: '2026-05-19',
    });

    expect(filteredItems.map((item) => item.title)).toEqual(['첫 번째 조사지', '두 번째 조사지']);
  });
});
