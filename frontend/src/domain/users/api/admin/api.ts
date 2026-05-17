import { postJson } from '../../../../common/api/client/apiClient';
import { USER_API_URLS } from '../apiUrls';
import type { AdminUserResponse, UserLoginPayload } from '../../types/types';

// 관리자 로그인 API를 호출합니다.
export function loginAdminUser(payload: UserLoginPayload): Promise<AdminUserResponse> {
  return postJson<AdminUserResponse, UserLoginPayload>(USER_API_URLS.ADMIN.LOGIN, payload);
}
