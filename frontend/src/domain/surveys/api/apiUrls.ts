export const SURVEY_API_URLS = {
  NORMAL: {
    BASE: '/api/normal/surveys',
  },
  ADMIN: {
    BASE: '/api/admin/surveys',
    DETAIL: (surveyId: string) => `/api/admin/surveys/${surveyId}`,
  },
} as const;
