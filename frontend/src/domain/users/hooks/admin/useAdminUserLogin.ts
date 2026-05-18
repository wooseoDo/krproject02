import { loginAdminUser } from '../../api/admin/api';
import { saveAuthenticatedUser } from '../../model/authSession';
import type { LoginSubmitResult, UserLoginPayload } from '../../types/types';

export async function submitAdminUserLogin(payload: UserLoginPayload): Promise<LoginSubmitResult> {
  const user = await loginAdminUser(payload);
  saveAuthenticatedUser({ ...user, scope: 'admin' });
  return { created: false };
}
