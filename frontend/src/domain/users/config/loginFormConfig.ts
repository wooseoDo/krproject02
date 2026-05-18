import { z } from 'zod';
import { LOGIN_FIELD_ERROR_MESSAGES } from '../constants/loginErrors';
import type { LoginFormErrors } from '../types/errors';
import type { UserLoginPayload } from '../types/types';

function isValidBirthDate(value: string) {
  if (!/^\d{6}$/.test(value)) {
    return false;
  }

  const yearPrefix = Number(value.slice(0, 2));
  const month = Number(value.slice(2, 4));
  const day = Number(value.slice(4, 6));
  const currentYearPrefix = new Date().getFullYear() % 100;
  const fullYear = yearPrefix <= currentYearPrefix ? 2000 + yearPrefix : 1900 + yearPrefix;
  const date = new Date(Date.UTC(fullYear, month - 1, day));

  return (
    date.getUTCFullYear() === fullYear &&
    date.getUTCMonth() === month - 1 &&
    date.getUTCDate() === day
  );
}

export const loginFormSchema = z.object({
  birthDate: z
    .string()
    .refine(isValidBirthDate, LOGIN_FIELD_ERROR_MESSAGES.BIRTH_DATE_FORMAT),
  password: z
    .string()
    .regex(/^[\x21-\x7E]{4,8}$/, LOGIN_FIELD_ERROR_MESSAGES.PASSWORD_FORMAT),
});

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
  const result = loginFormSchema.safeParse(values);

  if (result.success) {
    return {};
  }

  // Convert Zod issues into field-level messages consumed by the login form.
  return result.error.issues.reduce<LoginFormErrors>((errors, issue) => {
    const field = issue.path[0];

    if (field === 'birthDate' || field === 'password') {
      errors[field] = issue.message;
    }

    return errors;
  }, {});
}
