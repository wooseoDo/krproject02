import { loginAdminUser } from '../../api/admin/api';
import { saveAuthenticatedUser } from '../../model/authSession';
import type { LoginResult, UserLoginPayload } from '../../types/types';

export async function submitAdminUserLogin(payload: UserLoginPayload): Promise<LoginResult> {
  const user = await loginAdminUser(payload);
  const authenticatedUser = { ...user, scope: 'admin' as const };
  saveAuthenticatedUser(authenticatedUser);
  return { user: authenticatedUser, created: false };
}
