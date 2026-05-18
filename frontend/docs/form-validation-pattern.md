# 폼 검증 패턴 가이드

이 가이드는 KPR ERP 시스템에서 Zod를 사용한 폼 검증 패턴을 설명합니다.

## 개요

모든 폼 검증은 Zod 스키마를 사용하며, 다음을 제공합니다:

- 타입 안전한 폼 값
- 일관된 에러 메시지
- 재사용 가능한 검증 로직
- React Hook Form과의 완벽한 통합

## 폼 설정 파일 구조

각 도메인의 `config` 폴더에 폼 설정 파일을 생성합니다.

### 기본 템플릿

`src/domain/{feature}/config/{feature}FormConfig.ts`:

```typescript
import { z } from 'zod';
import type { ModalMode } from '@/common/hooks/modal/useModalForm';
import { requiredTrimmedString, optionalString } from '@/common/utils/validation/schemas';
import type { FeatureResponse, FeatureCreatePayload, FeatureUpdatePayload } from '../types/types';

// 1. Zod 스키마 정의
const featureFormSchema = z.object({
    name: requiredTrimmedString('이름', { max: 200 }),
    description: optionalString('설명', { max: 1000, emptyValue: 'null' }),
    isActive: z.boolean(),
});

// 2. 타입 추론
export type FeatureFormValues = z.infer<typeof featureFormSchema>;

// 3. 스키마 옵션 인터페이스
export interface FeatureFormSchemaOptions {
    mode: ModalMode;
}

// 4. 스키마 빌더
export const buildFeatureFormSchema = ({ mode }: FeatureFormSchemaOptions) => {
    // 모드에 따라 동적으로 스키마 조정 가능
    return featureFormSchema;
};

// 5. 기본값 생성 함수
export const createFeatureDefaultValues = (): FeatureFormValues => ({
    name: '',
    description: '',
    isActive: true,
});

// 6. 엔티티 → 폼 값 매핑
export const mapFeatureResponseToFormValues = (feature: FeatureResponse): FeatureFormValues => ({
    name: feature.name,
    description: feature.description ?? '',
    isActive: feature.isActive,
});

// 7. 폼 값 → 생성 페이로드
export const buildFeatureCreatePayload = (values: FeatureFormValues): FeatureCreatePayload => ({
    name: values.name,
    description: values.description || null,
    isActive: values.isActive,
});

// 8. 폼 값 → 수정 페이로드
export const buildFeatureUpdatePayload = (values: FeatureFormValues): FeatureUpdatePayload => ({
    name: values.name,
    description: values.description || null,
    isActive: values.isActive,
});
```

## 재사용 가능한 검증 스키마

`src/common/utils/validation/schemas.ts`에 공통 검증 스키마가 정의되어 있습니다.

### requiredTrimmedString

필수 문자열 필드 (자동 trim 적용):

```typescript
import { requiredTrimmedString } from '@/common/utils/validation/schemas';

const schema = z.object({
    name: requiredTrimmedString('이름', {
        min: 2,        // 최소 길이 (선택)
        max: 100,      // 최대 길이 (선택)
        pattern: /^[가-힣a-zA-Z]+$/,  // 정규식 (선택)
        patternMessage: '한글 또는 영문만 입력 가능합니다.',  // 패턴 에러 메시지 (선택)
    }),
});
```

**생성되는 에러 메시지:**
- 비어있을 때: "이름을(를) 입력해 주세요."
- 최소 길이 미달: "이름은(는) 최소 2자 이상이어야 합니다."
- 최대 길이 초과: "이름은(는) 최대 100자까지 입력 가능합니다."
- 패턴 불일치: "한글 또는 영문만 입력 가능합니다."

### optionalString

선택적 문자열 필드:

```typescript
import { optionalString } from '@/common/utils/validation/schemas';

const schema = z.object({
    description: optionalString('설명', {
        max: 1000,
        emptyValue: 'null',  // 'null' | 'undefined' (기본값: 'undefined')
    }),
});
```

**emptyValue 옵션:**
- `'undefined'`: 빈 문자열을 `undefined`로 변환 (기본값)
- `'null'`: 빈 문자열을 `null`로 변환 (백엔드가 null을 요구하는 경우)

### 이메일 검증

```typescript
import { emailSchema } from '@/common/utils/validation/schemas';

const schema = z.object({
    email: emailSchema('이메일'),
});
```

### 전화번호 검증

```typescript
import { phoneNumberSchema, optionalPhoneNumberSchema } from '@/common/utils/validation/schemas';

const schema = z.object({
    phone: phoneNumberSchema('전화번호'),           // 필수
    fax: optionalPhoneNumberSchema('팩스번호'),     // 선택
});
```

**지원 형식:**
- `010-1234-5678`
- `02-123-4567`
- `031-123-4567`

### 비밀번호 검증

```typescript
import { passwordSchema, confirmPasswordSchema } from '@/common/utils/validation/schemas';

const schema = z.object({
    password: passwordSchema('비밀번호'),
    passwordConfirm: confirmPasswordSchema('password', '비밀번호 확인'),
});
```

**비밀번호 규칙:**
- 최소 8자
- 영문, 숫자, 특수문자 중 2가지 이상 조합

### 날짜 검증

```typescript
import { requiredDateSchema, optionalDateSchema } from '@/common/utils/validation/schemas';

const schema = z.object({
    startDate: requiredDateSchema('시작일'),
    endDate: optionalDateSchema('종료일'),
});
```

**형식:** `YYYY-MM-DD`

### 로그인 ID 검증

```typescript
import { loginIdSchema } from '@/common/utils/validation/schemas';

const schema = z.object({
    loginId: loginIdSchema(),
});
```

**규칙:**
- 4~100자
- 영문 소문자, 숫자, 언더스코어만 사용 가능

### 주민등록번호 검증

```typescript
import { residentRegistrationSchema } from '@/common/utils/validation/schemas';

const schema = z.object({
    residentNumber: residentRegistrationSchema('주민등록번호'),
});
```

**특징:**
- 체크섬 검증 포함
- 형식: `123456-1234567`

## 모드별 동적 스키마

생성/수정 모드에 따라 스키마를 동적으로 변경할 수 있습니다.

### 예시 1: 생성 시에만 필수인 필드

```typescript
export const buildFeatureFormSchema = ({ mode }: FeatureFormSchemaOptions) => {
    return z.object({
        code: mode === 'create'
            ? requiredTrimmedString('코드', { max: 50 })
            : z.string().optional(),  // 수정 시에는 표시하지 않음
        name: requiredTrimmedString('이름', { max: 200 }),
    });
};
```

### 예시 2: 모드별 다른 검증 규칙

```typescript
export const buildUserFormSchema = ({ mode }: UserFormSchemaOptions) => {
    const baseSchema = {
        loginId: loginIdSchema(),
        name: requiredTrimmedString('이름', { max: 100 }),
    };

    if (mode === 'create') {
        // 생성 모드: 비밀번호 필수
        return z.object({
            ...baseSchema,
            password: passwordSchema('비밀번호'),
            passwordConfirm: confirmPasswordSchema('password'),
        });
    } else {
        // 수정 모드: 비밀번호 선택 (변경할 때만 입력)
        return z.object({
            ...baseSchema,
            password: optionalPasswordSchema('새 비밀번호'),
            passwordConfirm: z.string().optional(),
        }).refine((data) => {
            if (data.password && data.password !== data.passwordConfirm) {
                return false;
            }
            return true;
        }, {
            message: '비밀번호가 일치하지 않습니다.',
            path: ['passwordConfirm'],
        });
    }
};
```

## 커스텀 검증

### superRefine을 사용한 복잡한 검증

```typescript
const schema = z.object({
    startDate: requiredDateSchema('시작일'),
    endDate: requiredDateSchema('종료일'),
}).superRefine((data, ctx) => {
    if (data.endDate < data.startDate) {
        ctx.addIssue({
            code: z.ZodIssueCode.custom,
            message: '종료일은 시작일보다 이후여야 합니다.',
            path: ['endDate'],
        });
    }
});
```

### refine을 사용한 간단한 검증

```typescript
const schema = z.object({
    minPrice: z.coerce.number().min(0),
    maxPrice: z.coerce.number().min(0),
}).refine((data) => data.maxPrice >= data.minPrice, {
    message: '최대 가격은 최소 가격보다 크거나 같아야 합니다.',
    path: ['maxPrice'],
});
```

### 비동기 검증 (서버 검증)

```typescript
const schema = z.object({
    email: emailSchema('이메일'),
}).refine(async (data) => {
    const response = await api.get(`/check-email?email=${data.email}`);
    return response.data.available;
}, {
    message: '이미 사용 중인 이메일입니다.',
    path: ['email'],
});
```

## 배열과 객체 검증

### 배열 검증

```typescript
const schema = z.object({
    tags: z.array(z.string()).min(1, '최소 1개의 태그를 입력해 주세요.'),
    permissionIds: z.array(z.uuid()).optional(),
});
```

### 중첩 객체 검증

```typescript
const addressSchema = z.object({
    city: requiredTrimmedString('시/도', { max: 50 }),
    district: requiredTrimmedString('구/군', { max: 50 }),
    street: optionalString('상세주소', { max: 200 }),
});

const schema = z.object({
    name: requiredTrimmedString('이름', { max: 100 }),
    address: addressSchema,
});
```

## 조건부 필드

### 다른 필드에 따라 필수 여부 결정

```typescript
const schema = z.object({
    hasDiscount: z.boolean(),
    discountRate: z.coerce.number().optional(),
}).refine((data) => {
    if (data.hasDiscount && !data.discountRate) {
        return false;
    }
    return true;
}, {
    message: '할인율을 입력해 주세요.',
    path: ['discountRate'],
});
```

### discriminatedUnion 사용

```typescript
const schema = z.discriminatedUnion('type', [
    z.object({
        type: z.literal('fixed'),
        amount: z.coerce.number().min(0, '금액은 0 이상이어야 합니다.'),
    }),
    z.object({
        type: z.literal('percentage'),
        rate: z.coerce.number().min(0).max(100, '비율은 0~100 사이여야 합니다.'),
    }),
]);
```

## 데이터 변환 (Transform)

### 문자열 정규화

```typescript
const schema = z.object({
    code: z.string().trim().toUpperCase(),  // 대문자로 변환
    phone: z.string().transform((val) => val.replace(/[^0-9]/g, '')),  // 숫자만 추출
});
```

### 빈 문자열 처리

```typescript
const schema = z.object({
    description: z.string().transform((val) => val.length === 0 ? null : val),
});
```

### 배열 중복 제거

```typescript
import { uniqueValues } from '@/common/utils/normalization/form';

const schema = z.object({
    tags: z.array(z.string()).transform((values) => uniqueValues(values)),
});
```

## 에러 메시지 커스터마이징

### 기본 에러 메시지

```typescript
const schema = z.object({
    age: z.coerce.number({
        required_error: '나이를 입력해 주세요.',
        invalid_type_error: '올바른 숫자를 입력해 주세요.',
    }).min(0, '나이는 0 이상이어야 합니다.'),
});
```

### 전역 에러 메시지 설정

```typescript
import { z } from 'zod';

z.setErrorMap((issue, ctx) => {
    if (issue.code === z.ZodIssueCode.invalid_type) {
        if (issue.expected === 'number') {
            return { message: '올바른 숫자를 입력해 주세요.' };
        }
    }
    return { message: ctx.defaultError };
});
```

## React Hook Form 통합

### ResourceModal에서 사용

```typescript
import { ResourceModal } from '@/common/components/modal/ResourceModal';
import { buildFeatureFormSchema, createFeatureDefaultValues } from '../config/featureFormConfig';

const config = {
    buildSchema: buildFeatureFormSchema,
    createDefaultValues,
    mapEntityToFormValues,
    // ...
};

<ResourceModal config={config} {...props}>
    {({ control }) => (
        <FormTextField
            control={control}
            name="name"
            label="이름"
            required
        />
    )}
</ResourceModal>
```

### 수동으로 useForm 사용

```typescript
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { buildFeatureFormSchema } from '../config/featureFormConfig';

const schema = buildFeatureFormSchema({ mode: 'create' });

const form = useForm<FeatureFormValues>({
    resolver: zodResolver(schema),
    defaultValues: createFeatureDefaultValues(),
    mode: 'onChange',  // 실시간 검증
});
```

## 모범 사례

### 1. 스키마를 config 파일에 중앙화

```typescript
// ✅ 좋은 예: config 파일에 모든 검증 로직
// src/domain/products/config/productFormConfig.ts
export const buildProductFormSchema = ({ mode }) => { /* ... */ };

// ❌ 나쁜 예: 컴포넌트에 검증 로직
function ProductModal() {
    const schema = z.object({ /* ... */ });  // 컴포넌트마다 중복
}
```

### 2. 공통 스키마 재사용

```typescript
// ✅ 좋은 예: 재사용 가능한 스키마 활용
import { emailSchema, phoneNumberSchema } from '@/common/utils/validation/schemas';

const schema = z.object({
    email: emailSchema('이메일'),
    phone: phoneNumberSchema('전화번호'),
});

// ❌ 나쁜 예: 매번 새로 작성
const schema = z.object({
    email: z.string().email('이메일 형식이 올바르지 않습니다.'),
    phone: z.string().regex(/^[0-9-]+$/, '전화번호 형식이 올바르지 않습니다.'),
});
```

### 3. 타입 추론 활용

```typescript
// ✅ 좋은 예: z.infer로 타입 자동 생성
const schema = z.object({ name: z.string() });
export type FormValues = z.infer<typeof schema>;

// ❌ 나쁜 예: 수동으로 타입 정의 (동기화 문제 발생)
export interface FormValues {
    name: string;
}
```

### 4. 에러 메시지는 한국어로 명확하게

```typescript
// ✅ 좋은 예: 사용자 친화적 메시지
z.string().min(1, '이름을 입력해 주세요.')

// ❌ 나쁜 예: 기술적이거나 불친절한 메시지
z.string().min(1, 'Required')
z.string().min(1, 'name is required')
```

### 5. emptyValue 옵션 올바르게 사용

```typescript
// ✅ 좋은 예: 백엔드 API 스펙에 맞춤
// 백엔드가 null을 요구하는 경우
optionalString('설명', { emptyValue: 'null' })

// 백엔드가 undefined/생략을 요구하는 경우
optionalString('설명', { emptyValue: 'undefined' })

// ❌ 나쁜 예: 일관성 없이 사용
optionalString('설명')  // 기본값이 뭔지 명확하지 않음
```

## 관련 가이드

- [새 기능 모듈 추가 가이드](../guides/adding-new-feature.md)
- [CRUD 패턴 가이드](./crud-pattern.md)
- [코딩 스타일 가이드](../conventions/code-style.md)