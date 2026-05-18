import { ApiError } from '../../../../common/api/client/apiClient';
import { loginNormalUser, registerNormalUser } from '../../api/normal/api';
import { saveAuthenticatedUser } from '../../model/authSession';
import type { LoginSubmitResult, UserLoginPayload } from '../../types/types';

export async function submitNormalUserLogin(payload: UserLoginPayload): Promise<LoginSubmitResult> {
  try {
    const user = await loginNormalUser(payload);
    saveAuthenticatedUser({ ...user, scope: 'normal' });
    return { created: false };
  } catch (error) {
    if (!(error instanceof ApiError) || error.status !== 401) {
      throw error;
    }

    const user = await registerNormalUser(payload);
    saveAuthenticatedUser({ ...user, scope: 'normal' });
    return { created: true };
  }
}
