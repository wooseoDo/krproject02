import { api } from '../../../../common/api/client/apiClient';
import type { AdminUserResponse, UserLoginPayload } from '../../types/types';
import { USER_API_URLS } from '../apiUrls';

export async function loginAdminUser(payload: UserLoginPayload): Promise<AdminUserResponse> {
  // Call the admin login endpoint and unwrap the response data.
  const response = await api.post<AdminUserResponse, UserLoginPayload>(USER_API_URLS.ADMIN.LOGIN, payload);
  return response.data;
}
