import type { AuthSessionUser } from '../types/types';

const AUTH_SESSION_KEY = 'krproject02.auth.user';

export function saveAuthenticatedUser(user: AuthSessionUser) {
  // Keep the currently authenticated user in tab-scoped session storage.
  window.sessionStorage.setItem(AUTH_SESSION_KEY, JSON.stringify(user));
}

export function readAuthenticatedUser(): AuthSessionUser | null {
  // Read the authenticated user and clear corrupt session payloads.
  const raw = window.sessionStorage.getItem(AUTH_SESSION_KEY);

  if (!raw) {
    return null;
  }

  try {
    return JSON.parse(raw) as AuthSessionUser;
  } catch {
    window.sessionStorage.removeItem(AUTH_SESSION_KEY);
    return null;
  }
}

export function clearAuthenticatedUser() {
  // Remove the current login session from session storage.
  window.sessionStorage.removeItem(AUTH_SESSION_KEY);
}
