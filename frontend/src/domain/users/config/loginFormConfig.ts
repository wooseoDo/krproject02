import type { UserLoginPayload } from '../types/types';

export interface LoginFormErrors {
  birthDate?: string;
  password?: string;
}

// 로그인 폼의 초기 입력값을 생성합니다.
export const createLoginDefaultValues = (): UserLoginPayload => ({
  birthDate: '',
  password: '',
});

// 로그인 폼 입력값을 API 요청에 맞는 형식으로 정리합니다.
export function normalizeLoginPayload(values: UserLoginPayload): UserLoginPayload {
  return {
    birthDate: values.birthDate.replace(/\D/g, '').slice(0, 6),
    password: values.password.trim().slice(0, 8),
  };
}

// 로그인 폼 입력값의 형식 오류를 검증합니다.
export function validateLoginPayload(values: UserLoginPayload): LoginFormErrors {
  const errors: LoginFormErrors = {};

  if (!/^\d{6}$/.test(values.birthDate)) {
    errors.birthDate = 'YYMMDD 형식의 숫자 6자리를 입력해 주세요.';
  }

  if (!/^[\x21-\x7E]{4,8}$/.test(values.password)) {
    errors.password = '비밀번호는 영문, 숫자, 특수문자 조합 4~8자리입니다.';
  }

  return errors;
}
