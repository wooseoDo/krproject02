# Users Domain README

이 문서는 `src/domain/users` 도메인을 처음 읽는 사람이 로그인 기능의 구조와 타입 정의를 이해할 수 있도록 정리한 문서입니다.

## 도메인 목적

`users` 도메인은 일반 사용자와 관리자의 로그인, 로그인 후 세션 저장, 대시보드 진입을 담당합니다.

현재 지원하는 화면 경로는 다음과 같습니다.

- 사용자 로그인: `/login`
- 사용자 대시보드: `/dashboard`
- 관리자 로그인: `/admin/login`
- 관리자 대시보드: `/admin/dashboard`

## 디렉터리 구조

```text
src/domain/users/
  api/
    apiUrls.ts
    normal/api.ts
    admin/api.ts
  components/
    shared/LoginForm.tsx
    shared/DashboardView.tsx
  config/
    loginFormConfig.ts
    loginFormConfig.test.ts
  constants/
    messages.ts
    loginErrors.ts
    queryKeys.ts
  hooks/
    normal/useNormalUserLogin.ts
    admin/useAdminUserLogin.ts
  model/
    authSession.ts
  pages/
    normal/UserLoginPage.tsx
    normal/UserDashboardPage.tsx
    admin/AdminLoginPage.tsx
    admin/AdminDashboardPage.tsx
  types/
    types.ts
    errors.ts
    types.test.ts
```

## 파일별 책임

`pages`는 라우팅 단위의 화면 조립만 담당합니다.

예를 들어 `UserLoginPage.tsx`는 직접 input UI를 그리지 않고, `LoginForm`에 제목, 설명, submit 함수 같은 설정값을 넘깁니다.

`components`는 재사용 가능한 UI 조각입니다.

- `LoginForm.tsx`: 일반 사용자와 관리자 로그인 화면에서 함께 쓰는 폼 UI
- `DashboardView.tsx`: 사용자와 관리자 대시보드에서 함께 쓰는 대시보드 UI

`api`는 백엔드 URL 호출만 담당합니다.

- 일반 사용자 로그인: `POST /api/normal/users/login`
- 일반 사용자 신규 등록: `POST /api/normal/users`
- 관리자 로그인: `POST /api/admin/users/login`

`hooks`는 로그인 시나리오를 담당합니다.

- 일반 사용자는 로그인 실패가 401이면 신규 등록을 한 번 더 시도합니다.
- 관리자는 로그인 실패 시 신규 등록하지 않습니다.

`model/authSession.ts`는 세션 스토리지 저장, 읽기, 삭제를 담당합니다.

`config/loginFormConfig.ts`는 로그인 폼의 기본값, 입력값 정규화, Zod 검증을 담당합니다.

## 로그인 프로세스

### 일반 사용자

1. `/login` 진입
2. `LoginForm`에서 생년월일, 비밀번호 입력
3. `normalizeLoginPayload`로 입력값 정리
4. `loginFormSchema`로 Zod 검증
5. `submitNormalUserLogin` 호출
6. `POST /api/normal/users/login` 요청
7. 성공하면 세션 저장 후 `/dashboard` 이동
8. 401이면 `POST /api/normal/users`로 신규 등록
9. 등록 성공 시 세션 저장 후 `/dashboard` 이동

### 관리자

1. `/admin/login` 진입
2. `LoginForm`에서 생년월일, 비밀번호 입력
3. 입력값 정규화와 Zod 검증
4. `submitAdminUserLogin` 호출
5. `POST /api/admin/users/login` 요청
6. 성공하면 세션 저장 후 `/admin/dashboard` 이동
7. 실패하면 신규 등록하지 않고 오류 메시지 표시

## 세션 저장

로그인 성공 시 `sessionStorage`에 현재 로그인 사용자를 저장합니다.

저장 키는 다음과 같습니다.

```ts
krproject02.auth.user
```

저장 타입은 `AuthSessionUser`입니다.

```ts
export type AuthSessionUser = NormalUserResponse & {
  scope: UserScope;
};
```

로그아웃 시 `clearAuthenticatedUser()`가 이 키를 삭제합니다.

## 타입 설명

### `export type`을 쓰는 이유

TypeScript에는 런타임에 남는 값과 타입 검사에만 쓰이는 타입이 있습니다.

`type`은 타입 검사에만 쓰이고, 브라우저에서 실행되는 JavaScript 코드에는 남지 않습니다.

```ts
export type UserScope = 'normal' | 'admin';
```

이 코드는 `UserScope`라는 타입을 다른 파일에서 쓸 수 있게 내보냅니다.

값을 내보낼 때는 `export const`, `export function`을 씁니다.

```ts
export const USER_MESSAGES = {};
export function validateLoginPayload() {}
```

타입을 내보낼 때는 `export type`을 쓰면 의도가 명확합니다.

```ts
export type AuthSessionUser = NormalUserResponse & {
  scope: UserScope;
};
```

### `import type`을 쓰는 이유

타입만 가져올 때는 `import type`을 사용합니다.

```ts
import type { UserLoginPayload } from '../../types/types';
```

이렇게 쓰면 번들러와 TypeScript가 “이 import는 실행 코드가 아니라 타입용”이라고 알 수 있습니다.

반대로 실제 함수나 값을 가져올 때는 일반 `import`를 씁니다.

```ts
import { loginNormalUser } from '../../api/normal/api';
```

### `NormalUserResponse & { scope: UserScope }` 뜻

`&`는 intersection type이라고 부릅니다.

쉽게 말하면 “두 타입을 합친 타입”입니다.

```ts
export interface NormalUserResponse {
  userId: string | null;
  birthDate: string;
  createdAt: string;
}

export type AuthSessionUser = NormalUserResponse & {
  scope: UserScope;
};
```

위 타입은 아래와 같은 형태와 같습니다.

```ts
type AuthSessionUser = {
  userId: string | null;
  birthDate: string;
  createdAt: string;
  scope: 'normal' | 'admin';
};
```

즉 백엔드 로그인 응답에 프론트에서 필요한 `scope`만 추가해서 세션 모델로 쓰는 것입니다.

## 주요 타입

```ts
export type UserScope = 'normal' | 'admin';
```

일반 사용자와 관리자를 구분합니다.

```ts
export interface UserLoginPayload {
  birthDate: string;
  password: string;
}
```

로그인 요청 payload입니다.

```ts
export interface NormalUserResponse {
  userId: string | null;
  birthDate: string;
  createdAt: string;
}
```

백엔드 로그인 성공 응답입니다.

`createdAt`은 `Date`가 아니라 `string`입니다. JSON에는 Date 타입이 없고, 백엔드의 `OffsetDateTime`은 문자열로 내려오기 때문입니다.

```ts
export type AdminUserResponse = NormalUserResponse;
```

프론트에서는 관리자 로그인 성공 응답도 일반 로그인 성공 응답과 같은 형태로 사용합니다.

로그인에 성공했다면 이미 활성 계정이므로 `active`는 세션 모델에 저장하지 않습니다.

```ts
export interface LoginSubmitResult {
  created: boolean;
}
```

로그인 submit 후 UI가 알아야 하는 결과입니다.

일반 사용자는 기존 계정이 없으면 신규 등록될 수 있으므로 `created` 값으로 메시지를 구분합니다.

## Zod 검증

로그인 폼 검증은 `loginFormConfig.ts`의 `loginFormSchema`가 담당합니다.

검증 규칙은 다음과 같습니다.

- 생년월일: `YYMMDD` 숫자 6자리이며 실제 존재하는 날짜
- 비밀번호: 영문, 숫자, 특수문자 기반 4~8자리

검증 결과는 UI에서 쓰기 쉬운 `LoginFormErrors` 형태로 바꿉니다.

```ts
export interface LoginFormErrors {
  birthDate?: string;
  password?: string;
}
```

## 테스트

테스트는 Vitest를 사용합니다.

실행 명령:

```bash
npm test
```

현재 테스트 파일은 다음 두 개입니다.

- `config/loginFormConfig.test.ts`
- `types/types.test.ts`

`package.json`의 test 스크립트는 다음과 같습니다.

```json
"test": "vitest run --typecheck"
```

`--typecheck`를 붙인 이유는 런타임 테스트뿐 아니라 타입 계약도 같이 확인하기 위해서입니다.

## 확장할 때 주의할 점

- `pages`에 UI를 직접 많이 그리지 말고, 재사용 가능한 부분은 `components`로 내립니다.
- 백엔드 응답 타입과 프론트 세션 타입을 섞지 않습니다.
- API 응답의 날짜는 먼저 `string`으로 받고, 화면에서 표시할 때만 `new Date(...)`로 변환합니다.
- 사용하지 않는 백엔드 필드는 프론트 타입에 억지로 들고 오지 않습니다.
- 타입만 필요한 import는 `import type`을 사용합니다.
