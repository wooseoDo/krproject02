import { postJson } from '../../../../common/api/client/apiClient';
import { USER_API_URLS } from '../apiUrls';
import type { NormalUserResponse, UserLoginPayload } from '../../types/types';

// 일반 사용자 로그인 API를 호출합니다.
export function loginNormalUser(payload: UserLoginPayload): Promise<NormalUserResponse> {
  return postJson<NormalUserResponse, UserLoginPayload>(USER_API_URLS.NORMAL.LOGIN, payload);
}

// 일반 사용자 신규 등록 API를 호출합니다.
export function registerNormalUser(payload: UserLoginPayload): Promise<NormalUserResponse> {
  return postJson<NormalUserResponse, UserLoginPayload>(USER_API_URLS.NORMAL.BASE, payload);
}
