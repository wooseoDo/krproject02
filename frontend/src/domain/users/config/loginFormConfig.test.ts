import { describe, expect, it } from 'vitest';
import { LOGIN_FIELD_ERROR_MESSAGES } from '../constants/loginErrors';
import {
  createLoginDefaultValues,
  normalizeLoginPayload,
  validateLoginPayload,
} from './loginFormConfig';

describe('loginFormConfig', () => {
  it('creates empty login form values', () => {
    expect(createLoginDefaultValues()).toEqual({
      birthDate: '',
      password: '',
    });
  });

  it('normalizes birth date and password to the backend payload shape', () => {
    expect(
      normalizeLoginPayload({
        birthDate: '950312abc',
        password: '123456789',
      }),
    ).toEqual({
      birthDate: '950312',
      password: '12345678',
    });
  });

  it('returns no field errors for a valid login payload', () => {
    expect(
      validateLoginPayload({
        birthDate: '950312',
        password: 'Ab12!',
      }),
    ).toEqual({});
  });

  it('returns field errors for an invalid login payload', () => {
    expect(
      validateLoginPayload({
        birthDate: '95031',
        password: 'abc',
      }),
    ).toEqual({
      birthDate: LOGIN_FIELD_ERROR_MESSAGES.BIRTH_DATE_FORMAT,
      password: LOGIN_FIELD_ERROR_MESSAGES.PASSWORD_FORMAT,
    });
  });

  it('returns a birth date error for a non-existent calendar date', () => {
    expect(
      validateLoginPayload({
        birthDate: '990230',
        password: 'Ab12!',
      }),
    ).toEqual({
      birthDate: LOGIN_FIELD_ERROR_MESSAGES.BIRTH_DATE_FORMAT,
    });
  });
});
