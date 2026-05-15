import { postJson } from '../../../../common/api/client/apiClient';
import { USER_API_URLS } from '../apiUrls';
import type { AdminUserResponse, UserLoginPayload } from '../../types/types';

export function loginAdminUser(payload: UserLoginPayload): Promise<AdminUserResponse> {
  return postJson<AdminUserResponse, UserLoginPayload>(USER_API_URLS.ADMIN.LOGIN, payload);
}
