export const USER_API_URLS = {
  NORMAL: {
    BASE: '/api/normal/users',
    LOGIN: '/api/normal/users/login',
  },
  ADMIN: {
    LOGIN: '/api/admin/users/login',
  },
} as const;
