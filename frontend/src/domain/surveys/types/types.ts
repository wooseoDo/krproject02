export type SurveyScope = 'normal' | 'admin';

export type SurveyStatus = 'DRAFT' | 'PUBLISHED' | 'LOCKED' | 'CLOSED';

export interface SurveyListItemResponse {
  surveyId: string | null;
  title: string;
  surveyVersion: number;
  status: SurveyStatus;
  maxScore: number;
  category: string | null;
  estimatedTimeSec: number | null;
  createdAt: string;
}

export type SurveyListItem = SurveyListItemResponse & {
  rowNumber: number;
};

export interface SurveyListFilters {
  title: string;
  maxScore: string;
  estimatedTimeSec: string;
  surveyVersion: string;
  status: '' | SurveyStatus;
  releasedAtFrom: string;
  releasedAtTo: string;
}

export interface SurveyListPageState {
  page: number;
  pageSize: number;
  filters: SurveyListFilters;
}
