export const userQueryKeys = {
  normal: {
    all: ['normal', 'users'] as const,
    login: () => [...userQueryKeys.normal.all, 'login'] as const,
  },
  admin: {
    all: ['admin', 'users'] as const,
    login: () => [...userQueryKeys.admin.all, 'login'] as const,
  },
} as const;
