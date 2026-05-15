import { postJson } from '../../../../common/api/client/apiClient';
import { USER_API_URLS } from '../apiUrls';
import type { NormalUserResponse, UserLoginPayload } from '../../types/types';

export function loginNormalUser(payload: UserLoginPayload): Promise<NormalUserResponse> {
  return postJson<NormalUserResponse, UserLoginPayload>(USER_API_URLS.NORMAL.LOGIN, payload);
}

export function registerNormalUser(payload: UserLoginPayload): Promise<NormalUserResponse> {
  return postJson<NormalUserResponse, UserLoginPayload>(USER_API_URLS.NORMAL.BASE, payload);
}
