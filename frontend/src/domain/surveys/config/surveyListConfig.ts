import type { SurveyListFilters, SurveyListPageState } from '../types/types';

export const SURVEY_PAGE_SIZE = 2;

export const createSurveyListDefaultFilters = (): SurveyListFilters => ({
  title: '',
  maxScore: '',
  estimatedTimeSec: '',
  surveyVersion: '',
  status: '',
  releasedAtFrom: '',
  releasedAtTo: '',
});

export const createSurveyListDefaultPageState = (): SurveyListPageState => ({
  page: 1,
  pageSize: SURVEY_PAGE_SIZE,
  filters: createSurveyListDefaultFilters(),
});
