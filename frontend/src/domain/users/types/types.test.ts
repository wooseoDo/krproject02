import { describe, expect, it } from 'vitest';
import type {
  AdminUserResponse,
  AuthSessionUser,
  LoginSubmitResult,
  NormalUserResponse,
  UserLoginPayload,
  UserScope,
} from './types';

type IsEqual<TActual, TExpected> =
  (<T>() => T extends TActual ? 1 : 2) extends
  (<T>() => T extends TExpected ? 1 : 2)
    ? true
    : false;

function assertType<TCondition extends true>(value: TCondition) {
  // Compile-time only assertion helper.
  return value;
}

describe('user types', () => {
  it('keeps the user scope contract narrow', () => {
    expect(assertType<IsEqual<UserScope, 'normal' | 'admin'>>(true)).toBe(true);
  });

  it('keeps login payload fields as strings', () => {
    expect(assertType<IsEqual<UserLoginPayload['birthDate'], string>>(true)).toBe(true);
    expect(assertType<IsEqual<UserLoginPayload['password'], string>>(true)).toBe(true);
  });

  it('keeps API date values as serialized strings', () => {
    expect(assertType<IsEqual<NormalUserResponse['createdAt'], string>>(true)).toBe(true);
    expect(assertType<IsEqual<AdminUserResponse['createdAt'], string>>(true)).toBe(true);
  });

  it('uses the same response shape for normal and admin login success', () => {
    expect(assertType<IsEqual<AdminUserResponse, NormalUserResponse>>(true)).toBe(true);
  });

  it('keeps the session model separate from the raw API response', () => {
    expect(assertType<IsEqual<AuthSessionUser['scope'], UserScope>>(true)).toBe(true);
  });

  it('keeps submit result focused on UI feedback only', () => {
    expect(assertType<IsEqual<LoginSubmitResult, { created: boolean }>>(true)).toBe(true);
  });

  it('accepts normal and admin session users', () => {
    const normalUser: AuthSessionUser = {
      scope: 'normal',
      userId: null,
      birthDate: '950312',
      createdAt: '2026-05-18T10:20:30+09:00',
    };

    const adminUser: AuthSessionUser = {
      scope: 'admin',
      userId: '019b1000-0000-7000-8000-000000000001',
      birthDate: '900101',
      createdAt: '2026-05-18T10:20:30+09:00',
    };

    expect(normalUser.scope).toBe('normal');
    expect(adminUser.scope).toBe('admin');
  });

  it('rejects Date objects for API response date values at compile time', () => {
    const responseWithDateObject: NormalUserResponse = {
      userId: null,
      birthDate: '950312',
      // @ts-expect-error API JSON date values must stay string until explicitly mapped.
      createdAt: new Date(),
    };

    expect(responseWithDateObject.createdAt).toBeInstanceOf(Date);
  });
});
