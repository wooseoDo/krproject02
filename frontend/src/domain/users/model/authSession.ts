import type { AuthenticatedUser } from '../types/types';

const AUTH_SESSION_KEY = 'krproject02.auth.user';

export function saveAuthenticatedUser(user: AuthenticatedUser) {
  window.sessionStorage.setItem(AUTH_SESSION_KEY, JSON.stringify(user));
}

export function readAuthenticatedUser(): AuthenticatedUser | null {
  const raw = window.sessionStorage.getItem(AUTH_SESSION_KEY);

  if (!raw) {
    return null;
  }

  try {
    return JSON.parse(raw) as AuthenticatedUser;
  } catch {
    window.sessionStorage.removeItem(AUTH_SESSION_KEY);
    return null;
  }
}

export function clearAuthenticatedUser() {
  window.sessionStorage.removeItem(AUTH_SESSION_KEY);
}
