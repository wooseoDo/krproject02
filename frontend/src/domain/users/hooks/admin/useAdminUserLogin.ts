import { loginAdminUser } from '../../api/admin/api';
import { saveAuthenticatedUser } from '../../model/authSession';
import type { LoginResult, UserLoginPayload } from '../../types/types';

// 관리자 로그인 성공 시 인증 세션을 저장하고 로그인 결과를 반환합니다.
export async function submitAdminUserLogin(payload: UserLoginPayload): Promise<LoginResult> {
  const user = await loginAdminUser(payload);
  const authenticatedUser = { ...user, scope: 'admin' as const };
  saveAuthenticatedUser(authenticatedUser);
  return { user: authenticatedUser, created: false };
}
