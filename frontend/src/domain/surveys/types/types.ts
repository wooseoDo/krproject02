export type SurveyScope = 'normal' | 'admin';

export type SurveyStatus = 'DRAFT' | 'PUBLISHED' | 'LOCKED' | 'CLOSED';

export type SurveyQuestionType = 'SINGLE_CHOICE' | 'LIKERT';

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

export interface SurveyListPageResponse {
  items: SurveyListItemResponse[];
  page: number;
  size: number;
  totalItems: number;
  totalPages: number;
}

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

export interface SurveyListQueryParams {
  page: number;
  pageSize: number;
  filters: SurveyListFilters;
}

export interface AdminSurveyQuestionOptionRequest {
  optionLabel: string;
  optionScore: number;
}

export interface AdminSurveyQuestionRequest {
  questionType: SurveyQuestionType;
  title: string;
  score: number;
  options: AdminSurveyQuestionOptionRequest[];
}

export interface AdminSurveySectionRequest {
  title: string;
  targetAverageScore: number | null;
  questions: AdminSurveyQuestionRequest[];
}

export interface AdminSurveyCreateRequest {
  title: string;
  category: string | null;
  description: string | null;
  status: SurveyStatus;
  maxScore: number;
  estimatedTimeSec: number;
  sections: AdminSurveySectionRequest[];
}

export interface AdminSurveyCreateResponse {
  surveyId: string | null;
  title: string;
  surveyVersion: number;
  status: SurveyStatus;
}

export type AdminSurveyUpdateRequest = AdminSurveyCreateRequest;

export type AdminSurveyUpdateResponse = AdminSurveyCreateResponse;

export interface SurveyQuestionDraft {
  id: string;
  questionType: SurveyQuestionType;
  title: string;
  score: string;
  optionCount: number;
  options: string[];
}

export interface SurveySectionDraft {
  id: string;
  title: string;
  targetAverageScore: string;
  questions: SurveyQuestionDraft[];
}

export interface AdminSurveyCreateDraft {
  title: string;
  category: string;
  description: string;
  status: SurveyStatus;
  maxScore: string;
  estimatedTimeMinutes: string;
  sections: SurveySectionDraft[];
}
