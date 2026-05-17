import { ApiError } from '../../../../common/api/client/apiClient';
import { loginNormalUser, registerNormalUser } from '../../api/normal/api';
import { saveAuthenticatedUser } from '../../model/authSession';
import type { LoginResult, UserLoginPayload } from '../../types/types';

// 일반 사용자 로그인 실패 시 신규 등록을 시도하고 인증 세션을 저장합니다.
export async function submitNormalUserLogin(payload: UserLoginPayload): Promise<LoginResult> {
  try {
    const user = await loginNormalUser(payload);
    const authenticatedUser = { ...user, scope: 'normal' as const };
    saveAuthenticatedUser(authenticatedUser);
    return { user: authenticatedUser, created: false };
  } catch (error) {
    if (!(error instanceof ApiError) || error.status !== 401) {
      throw error;
    }

    const user = await registerNormalUser(payload);
    const authenticatedUser = { ...user, scope: 'normal' as const };
    saveAuthenticatedUser(authenticatedUser);
    return { user: authenticatedUser, created: true };
  }
}
