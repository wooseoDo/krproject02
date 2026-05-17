import type { AuthenticatedUser } from '../types/types';

const AUTH_SESSION_KEY = 'krproject02.auth.user';

// 인증된 사용자 정보를 세션 스토리지에 저장합니다.
export function saveAuthenticatedUser(user: AuthenticatedUser) {
  window.sessionStorage.setItem(AUTH_SESSION_KEY, JSON.stringify(user));
}

// 세션 스토리지에서 인증된 사용자 정보를 읽어옵니다.
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

// 세션 스토리지에 저장된 인증 정보를 제거합니다.
export function clearAuthenticatedUser() {
  window.sessionStorage.removeItem(AUTH_SESSION_KEY);
}
