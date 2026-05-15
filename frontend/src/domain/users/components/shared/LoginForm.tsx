import { useState, type FormEvent } from 'react';
import {
  createLoginDefaultValues,
  normalizeLoginPayload,
  validateLoginPayload,
  type LoginFormErrors,
} from '../../config/loginFormConfig';
import { USER_MESSAGES } from '../../constants/messages';
import type { LoginResult, UserLoginPayload, UserScope } from '../../types/types';

interface LoginFormProps {
  scope: UserScope;
  title: string;
  description: string;
  submitLabel: string;
  onSubmit: (payload: UserLoginPayload) => Promise<LoginResult>;
}

export function LoginForm({ scope, title, description, submitLabel, onSubmit }: LoginFormProps) {
  const [values, setValues] = useState<UserLoginPayload>(createLoginDefaultValues);
  const [errors, setErrors] = useState<LoginFormErrors>({});
  const [notice, setNotice] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const dashboardPath = scope === 'admin' ? '/admin/dashboard' : '/dashboard';
  const alternateLoginPath = scope === 'admin' ? '/login' : '/admin/login';
  const alternateLoginLabel = scope === 'admin' ? '사용자 로그인' : '관리자 로그인';

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    const nextValues = normalizeLoginPayload(values);
    const nextErrors = validateLoginPayload(nextValues);
    setValues(nextValues);
    setErrors(nextErrors);
    setNotice('');

    if (Object.keys(nextErrors).length > 0) {
      setNotice(USER_MESSAGES.INVALID_INPUT);
      return;
    }

    setIsSubmitting(true);
    try {
      const result = await onSubmit(nextValues);
      setNotice(result.created ? USER_MESSAGES.REGISTERED : USER_MESSAGES.LOGGED_IN);
      window.history.pushState({}, '', dashboardPath);
      window.dispatchEvent(new PopStateEvent('popstate'));
    } catch {
      setNotice(scope === 'admin' ? USER_MESSAGES.ADMIN_LOGIN_ERROR : USER_MESSAGES.NORMAL_LOGIN_ERROR);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <main className={`login-shell login-shell--${scope}`}>
      <section className="login-panel" aria-labelledby={`${scope}-login-title`}>
        <div className="login-copy">
          <p className="section-label">{scope === 'admin' ? 'ADMIN ACCESS' : 'USER ACCESS'}</p>
          <h1 id={`${scope}-login-title`}>{title}</h1>
          <p>{description}</p>
        </div>

        <form className="login-form" onSubmit={handleSubmit} noValidate>
          <label className="field-group">
            <span>생년월일</span>
            <input
              inputMode="numeric"
              maxLength={6}
              placeholder="YYMMDD"
              value={values.birthDate}
              onChange={(event) =>
                setValues((current) => ({
                  ...current,
                  birthDate: event.target.value.replace(/\D/g, '').slice(0, 6),
                }))
              }
              aria-invalid={Boolean(errors.birthDate)}
              aria-describedby={errors.birthDate ? `${scope}-birthDate-error` : undefined}
            />
            {errors.birthDate && (
              <small id={`${scope}-birthDate-error`} className="field-error">
                {errors.birthDate}
              </small>
            )}
          </label>

          <label className="field-group">
            <span>비밀번호</span>
            <input
              type="password"
              maxLength={8}
              placeholder="4~8자리"
              value={values.password}
              onChange={(event) =>
                setValues((current) => ({
                  ...current,
                  password: event.target.value.slice(0, 8),
                }))
              }
              aria-invalid={Boolean(errors.password)}
              aria-describedby={errors.password ? `${scope}-password-error` : undefined}
            />
            {errors.password && (
              <small id={`${scope}-password-error`} className="field-error">
                {errors.password}
              </small>
            )}
          </label>

          {notice && <p className="form-notice">{notice}</p>}

          <button className="primary-action" type="submit" disabled={isSubmitting}>
            {isSubmitting ? '확인 중' : submitLabel}
          </button>
        </form>

        <a className="alternate-link" href={alternateLoginPath}>
          {alternateLoginLabel}
        </a>
      </section>
    </main>
  );
}
