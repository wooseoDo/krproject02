import type { SurveyStatus } from '../types/types';

export const SURVEY_STATUS_LABELS: Record<SurveyStatus, string> = {
  DRAFT: '작성중',
  PUBLISHED: '배포중',
  LOCKED: '중단',
  CLOSED: '종료',
};
