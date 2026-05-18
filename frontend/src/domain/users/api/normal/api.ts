import { api } from '../../../../common/api/client/apiClient';
import type { NormalUserResponse, UserLoginPayload } from '../../types/types';
import { USER_API_URLS } from '../apiUrls';

export async function loginNormalUser(payload: UserLoginPayload): Promise<NormalUserResponse> {
  // Call the normal user login endpoint and unwrap the response data.
  const response = await api.post<NormalUserResponse, UserLoginPayload>(USER_API_URLS.NORMAL.LOGIN, payload);
  return response.data;
}

export async function registerNormalUser(payload: UserLoginPayload): Promise<NormalUserResponse> {
  // Call the normal user registration endpoint and unwrap the response data.
  const response = await api.post<NormalUserResponse, UserLoginPayload>(USER_API_URLS.NORMAL.BASE, payload);
  return response.data;
}
