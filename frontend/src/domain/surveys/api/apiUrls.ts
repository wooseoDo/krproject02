export const SURVEY_API_URLS = {
  NORMAL: {
    BASE: '/api/normal/surveys',
    PARTICIPATION_DETAIL: (surveyId: string) => `/api/normal/surveys/${surveyId}/participation`,
    PARTICIPATION_START: (surveyId: string) => `/api/normal/surveys/${surveyId}/responses/start`,
    PARTICIPATION_SUBMIT: (responseId: string) => `/api/normal/surveys/responses/${responseId}/submit`,
  },
  ADMIN: {
    BASE: '/api/admin/surveys',
    DETAIL: (surveyId: string) => `/api/admin/surveys/${surveyId}`,
  },
} as const;
