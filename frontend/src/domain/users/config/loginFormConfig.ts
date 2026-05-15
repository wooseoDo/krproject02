import type { UserLoginPayload } from '../types/types';

export interface LoginFormErrors {
  birthDate?: string;
  password?: string;
}

export const createLoginDefaultValues = (): UserLoginPayload => ({
  birthDate: '',
  password: '',
});

export function normalizeLoginPayload(values: UserLoginPayload): UserLoginPayload {
  return {
    birthDate: values.birthDate.replace(/\D/g, '').slice(0, 6),
    password: values.password.trim().slice(0, 8),
  };
}

export function validateLoginPayload(values: UserLoginPayload): LoginFormErrors {
  const errors: LoginFormErrors = {};

  if (!/^\d{6}$/.test(values.birthDate)) {
    errors.birthDate = 'YYMMDD 형식의 숫자 6자리를 입력해 주세요.';
  }

  if (!/^[A-Za-z0-9!-\/:-@[-`{-~]{4,8}$/.test(values.password)) {
    errors.password = '비밀번호는 영문, 숫자, 특수문자 조합 4~8자리입니다.';
  }

  return errors;
}
