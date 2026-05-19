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

export interface AdminSurveyDetailOptionResponse {
  optionId: string | null;
  optionSort: number;
  optionLabel: string;
  optionScore: number;
}

export interface AdminSurveyDetailQuestionResponse {
  questionId: string | null;
  questionSort: number;
  questionType: SurveyQuestionType;
  title: string;
  score: number;
  options: AdminSurveyDetailOptionResponse[];
}

export interface AdminSurveyDetailSectionResponse {
  sectionId: string | null;
  sectionSort: number;
  title: string;
  targetAverageScore: number | null;
  questions: AdminSurveyDetailQuestionResponse[];
}

export interface AdminSurveyDetailResponse {
  surveyId: string | null;
  surveyGroupId: string | null;
  previousSurveyId: string | null;
  surveyVersion: number;
  latest: boolean;
  title: string;
  category: string | null;
  description: string | null;
  status: SurveyStatus;
  maxScore: number;
  estimatedTimeSec: number | null;
  sections: AdminSurveyDetailSectionResponse[];
}

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

export interface NormalSurveyParticipationOptionResponse {
  optionId: string | null;
  optionSort: number;
  optionLabel: string;
}

export interface NormalSurveyParticipationQuestionResponse {
  questionId: string | null;
  questionSort: number;
  questionType: SurveyQuestionType;
  title: string;
  options: NormalSurveyParticipationOptionResponse[];
}

export interface NormalSurveyParticipationSectionResponse {
  sectionId: string | null;
  sectionSort: number;
  title: string;
  targetAverageScore: number | null;
  questions: NormalSurveyParticipationQuestionResponse[];
}

export interface NormalSurveyParticipationDetailResponse {
  surveyId: string | null;
  surveyGroupId: string | null;
  surveyVersion: number;
  title: string;
  category: string | null;
  description: string | null;
  status: SurveyStatus;
  maxScore: number;
  estimatedTimeSec: number | null;
  sections: NormalSurveyParticipationSectionResponse[];
}

export interface NormalSurveyParticipationStartRequest {
  userId: string | null;
}

export interface NormalSurveyParticipationStartResponse {
  responseId: string | null;
  surveyId: string | null;
  surveyGroupId: string | null;
  surveyVersion: number;
  surveyTitle: string;
  resumed: boolean;
  completed: boolean;
  submittedAt: string | null;
  elapsedTimeSec: number | null;
  totalScore: number | null;
}

export interface NormalSurveySubmitAnswerRequest {
  questionId: string;
  optionId: string;
}

export interface NormalSurveySubmitRequest {
  elapsedTimeSec: number;
  answers: NormalSurveySubmitAnswerRequest[];
}

export interface NormalSurveySubmitResponse {
  responseId: string | null;
  surveyId: string | null;
  surveyGroupId: string | null;
  surveyVersion: number;
  completed: boolean;
  submittedAt: string | null;
  elapsedTimeSec: number | null;
  totalScore: number | null;
}

export interface SurveyParticipationDraftAnswer {
  questionId: string;
  optionId: string;
  answeredAt: string;
}

export interface SurveyParticipationDraft {
  responseId: string;
  surveyId: string;
  surveyGroupId: string;
  surveyVersion: number;
  userId: string;
  startedAt: string;
  lastSavedAt: string;
  answers: Record<string, SurveyParticipationDraftAnswer>;
}
