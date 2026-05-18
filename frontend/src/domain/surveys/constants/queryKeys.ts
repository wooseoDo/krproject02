export const surveyQueryKeys = {
  normal: {
    all: ['normal', 'surveys'] as const,
    list: () => [...surveyQueryKeys.normal.all, 'list'] as const,
  },
  admin: {
    all: ['admin', 'surveys'] as const,
    list: () => [...surveyQueryKeys.admin.all, 'list'] as const,
  },
} as const;
