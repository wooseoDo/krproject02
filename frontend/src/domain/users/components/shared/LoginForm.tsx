import { useState, type FormEvent } from 'react';
import {
  createLoginDefaultValues,
  normalizeLoginPayload,
  validateLoginPayload,
  type LoginFormErrors,
} from '../../config/loginFormConfig';
import { USER_MESSAGES } from '../../constants/messages';
import type { LoginResult, UserLoginPayload } from '../../types/types';

interface LoginFormProps {
  variant: string;
  titleId: string;
  sectionLabel: string;
  title: string;
  description: string;
  submitLabel: string;
  submittingLabel: string;
  successPath: string;
  errorMessage: string;
  onSubmit: (payload: UserLoginPayload) => Promise<LoginResult>;
}

// 전달받은 로그인 설정에 따라 공용 로그인 폼 UI와 제출 흐름을 렌더링합니다.
export function LoginForm({
  variant,
  titleId,
  sectionLabel,
  title,
  description,
  submitLabel,
  submittingLabel,
  successPath,
  errorMessage,
  onSubmit,
}: LoginFormProps) {
  const [values, setValues] = useState<UserLoginPayload>(createLoginDefaultValues);
  const [errors, setErrors] = useState<LoginFormErrors>({});
  const [notice, setNotice] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  // 입력값 검증 후 로그인 요청을 실행하고 성공 시 지정된 경로로 이동합니다.
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
      window.history.pushState({}, '', successPath);
      window.dispatchEvent(new PopStateEvent('popstate'));
    } catch {
      setNotice(errorMessage);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <main className={`login-shell login-shell--${variant}`}>
      <section className="login-panel" aria-labelledby={titleId}>
        <div className="login-copy">
          <p className="section-label">{sectionLabel}</p>
          <h1 id={titleId}>{title}</h1>
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
              aria-describedby={errors.birthDate ? `${titleId}-birthDate-error` : undefined}
            />
            {errors.birthDate && (
              <small id={`${titleId}-birthDate-error`} className="field-error">
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
              aria-describedby={errors.password ? `${titleId}-password-error` : undefined}
            />
            {errors.password && (
              <small id={`${titleId}-password-error`} className="field-error">
                {errors.password}
              </small>
            )}
          </label>

          {notice && <p className="form-notice">{notice}</p>}

          <button className="primary-action" type="submit" disabled={isSubmitting}>
            {isSubmitting ? submittingLabel : submitLabel}
          </button>
        </form>
      </section>
    </main>
  );
}
