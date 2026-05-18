# CRUD 패턴 가이드

이 가이드는 KPR ERP 시스템에서 CRUD(Create, Read, Update, Delete) 작업을 구현하는 표준 패턴을 설명합니다.

## 개요

모든 리소스의 CRUD 작업은 `useCRUDMutations` 훅을 통해 표준화되어 있습니다. 이를 통해:

- 일관된 에러 처리
- 자동 쿼리 무효화
- 통일된 토스트 메시지
- 최소한의 보일러플레이트 코드

## useCRUDMutations 훅

### 기본 사용법

```typescript
import { useCRUDMutations } from '@/common/hooks/crud/useCRUDMutations';
import { productQueryKeys } from '../constants/queryKeys';
import { PRODUCT_MESSAGES } from '../constants/messages';
import * as productApi from '../api/organization/api';
import type { ProductResponse, ProductCreatePayload, ProductUpdatePayload } from '../types/types';

export function useProductMutations(options: UseProductMutationsOptions = {}) {
    return useCRUDMutations<ProductResponse, ProductCreatePayload, ProductUpdatePayload>(
        {
            queryKeys: [productQueryKeys.organization.all],
            api: {
                create: productApi.createProduct,
                update: productApi.updateProduct,
                delete: productApi.deleteProduct,
            },
            messages: {
                createSuccess: PRODUCT_MESSAGES.CREATE_SUCCESS,
                createError: PRODUCT_MESSAGES.CREATE_ERROR,
                updateSuccess: PRODUCT_MESSAGES.UPDATE_SUCCESS,
                updateError: PRODUCT_MESSAGES.UPDATE_ERROR,
                deleteSuccess: PRODUCT_MESSAGES.DELETE_SUCCESS,
                deleteError: PRODUCT_MESSAGES.DELETE_ERROR,
            },
        },
        options,
    );
}
```

### 타입 매개변수

- `TData`: 서버 응답 타입 (예: `ProductResponse`)
- `TCreatePayload`: 생성 요청 페이로드 타입
- `TUpdatePayload`: 수정 요청 페이로드 타입

### 설정 옵션

#### queryKeys
쿼리 무효화에 사용할 쿼리 키 배열입니다.

```typescript
queryKeys: [productQueryKeys.organization.all]
```

mutation이 성공하면 이 키들에 해당하는 모든 쿼리가 자동으로 무효화됩니다.

#### api
CRUD 작업을 수행하는 API 함수들입니다.

```typescript
api: {
    create: (payload: TCreatePayload) => Promise<TData>,
    update: (id: string, payload: TUpdatePayload) => Promise<TData>,
    delete: (id: string) => Promise<void>,
}
```

**중요**: `update` 함수는 반드시 첫 번째 인자로 `id`를 받아야 합니다.

#### messages
각 작업의 성공/실패 메시지입니다.

```typescript
messages: {
    createSuccess: '상품이 성공적으로 생성되었습니다.',
    createError: '상품 생성에 실패했습니다.',
    updateSuccess: '상품이 성공적으로 수정되었습니다.',
    updateError: '상품 수정에 실패했습니다.',
    deleteSuccess: '상품이 성공적으로 삭제되었습니다.',
    deleteError: '상품 삭제에 실패했습니다.',
}
```

### 콜백 옵션

각 작업이 성공했을 때 실행할 콜백을 제공할 수 있습니다:

```typescript
const mutations = useProductMutations({
    onCreateSuccess: () => {
        console.log('상품이 생성되었습니다!');
        // 모달 닫기, 추가 작업 등
    },
    onUpdateSuccess: () => {
        console.log('상품이 수정되었습니다!');
    },
    onDeleteSuccess: () => {
        console.log('상품이 삭제되었습니다!');
    },
});
```

### 반환값

`useCRUDMutations`는 세 개의 mutation 객체를 반환합니다:

```typescript
const { create, update, delete: deleteMutation } = useProductMutations();

// TanStack Query의 UseMutationResult 타입
create.mutate(payload);
create.mutateAsync(payload);
create.isPending;
create.isError;
create.error;

update.mutate({ id, payload });
update.mutateAsync({ id, payload });
update.isPending;

deleteMutation.mutate(id);
deleteMutation.mutateAsync(id);
deleteMutation.isPending;
```

## 페이지에서 사용하기

### 기본 패턴

```typescript
import { useProductMutations } from '../hooks/organization/useProductMutations';

function ProductsPage() {
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [selectedProduct, setSelectedProduct] = useState<ProductResponse | null>(null);

    const mutations = useProductMutations({
        onCreateSuccess: () => setIsModalOpen(false),
        onUpdateSuccess: () => {
            setIsModalOpen(false);
            setSelectedProduct(null);
        },
    });

    const handleCreate = async (payload: ProductCreatePayload) => {
        try {
            await mutations.create.mutateAsync(payload);
            // onCreateSuccess 콜백이 자동으로 실행됨
        } catch (error) {
            // 에러는 mutation의 onError에서 이미 처리됨 (토스트 표시)
            // 필요한 경우 추가 에러 처리
        }
    };

    const handleUpdate = async (id: string, payload: ProductUpdatePayload) => {
        try {
            await mutations.update.mutateAsync({ id, payload });
        } catch {
            // 에러 처리는 이미 완료됨
        }
    };

    const handleDelete = async (id: string) => {
        try {
            await mutations.delete.mutateAsync(id);
        } catch {
            // 에러 처리는 이미 완료됨
        }
    };

    return (
        <div>
            {/* 생성 버튼 */}
            <button onClick={() => setIsModalOpen(true)}>상품 생성</button>

            {/* 테이블 */}
            <ProductTable
                onEdit={(product) => {
                    setSelectedProduct(product);
                    setIsModalOpen(true);
                }}
                onDelete={handleDelete}
                isDeleting={mutations.delete.isPending}
            />

            {/* 모달 */}
            <ProductModal
                open={isModalOpen}
                mode={selectedProduct ? 'edit' : 'create'}
                initialData={selectedProduct}
                onSubmit={selectedProduct ? handleUpdate : handleCreate}
                onCancel={() => {
                    setIsModalOpen(false);
                    setSelectedProduct(null);
                }}
                isSubmitting={mutations.create.isPending || mutations.update.isPending}
            />
        </div>
    );
}
```

### useResourceListPage 훅과 함께 사용

더 높은 수준의 추상화를 원한다면 `useResourceListPage` 훅을 사용하세요:

```typescript
import { useResourceListPage } from '@/common/hooks/page/useResourceListPage';
import { useProductResourceTable } from '../hooks/useProductResourceTable';
import { useProductsQuery } from '../hooks/organization/useProductsQuery';
import { useProductMutations } from '../hooks/organization/useProductMutations';
import { fetchProduct } from '../api/organization/api';

function ProductsPage() {
    const page = useResourceListPage({
        useTable: () =>
            useProductResourceTable({
                sortFieldMap: PRODUCT_SORT_FIELD_MAP,
                initialPageSize: 10,
            }),
        useQuery: useProductsQuery,
        useMutations: useProductMutations,
        fetchDetail: fetchProduct,
        deleteConfirm: {
            title: '상품 삭제',
            description: '정말 이 상품을 삭제하시겠습니까?',
        },
    });

    const { modal, table, query, mutations, confirmDialog, items, totalRows, isSubmitting, handlers } = page;
    const { handleCreate, handleEdit, handleDelete } = handlers;

    // 모든 CRUD 로직이 자동으로 처리됨!
    return (
        <>
            <MenuLayout
                headerProps={{ onCreateClick: handleCreate, createButtonLabel: '상품 생성' }}
                filterArea={/* ... */}
            >
                <DataTable
                    data={items}
                    columns={columns}
                    // ...
                />
            </MenuLayout>
            <ProductModal
                open={modal.state.open}
                isSubmitting={isSubmitting}
                onCancel={modal.close}
                {...modalProps}
            />
            <ConfirmDialog {...confirmDialog.dialogProps} />
        </>
    );
}
```

## API 함수 작성

### 표준 API 함수 구조

```typescript
import { api } from '@/common/api/client/apiClient';
import type { ProductResponse, ProductCreatePayload, ProductUpdatePayload } from '../types/types';

// 생성
export async function createProduct(payload: ProductCreatePayload): Promise<ProductResponse> {
    const response = await api.post<ProductResponse>('/organization/products', payload);
    return response.data;
}

// 수정 - 반드시 id를 첫 번째 인자로!
export async function updateProduct(id: string, payload: ProductUpdatePayload): Promise<ProductResponse> {
    const response = await api.put<ProductResponse>(`/organization/products/${id}`, payload);
    return response.data;
}

// 삭제
export async function deleteProduct(id: string): Promise<void> {
    await api.delete(`/organization/products/${id}`);
}
```

### 에러 처리

API 함수에서는 에러를 직접 처리하지 않습니다. 에러는 자동으로:

1. `apiClient.ts`의 인터셉터에서 처리
2. `errorMapper`를 통해 애플리케이션 에러로 변환
3. `errorBus`를 통해 전파
4. `useCRUDMutations`의 `onError`에서 토스트로 표시

```typescript
// ❌ 나쁜 예: API 함수에서 에러 처리
export async function createProduct(payload: ProductCreatePayload): Promise<ProductResponse> {
    try {
        const response = await api.post<ProductResponse>('/organization/products', payload);
        return response.data;
    } catch (error) {
        console.error('상품 생성 실패:', error);
        throw error; // 불필요한 코드
    }
}

// ✅ 좋은 예: 에러를 자연스럽게 전파
export async function createProduct(payload: ProductCreatePayload): Promise<ProductResponse> {
    const response = await api.post<ProductResponse>('/organization/products', payload);
    return response.data;
}
```

## 메시지 상수 정의

메시지는 별도의 상수 파일에 정의합니다.

`src/domain/products/constants/messages.ts`:

```typescript
export const PRODUCT_MESSAGES = {
    CREATE_SUCCESS: '상품이 성공적으로 생성되었습니다.',
    CREATE_ERROR: '상품 생성에 실패했습니다.',
    UPDATE_SUCCESS: '상품이 성공적으로 수정되었습니다.',
    UPDATE_ERROR: '상품 수정에 실패했습니다.',
    DELETE_SUCCESS: '상품이 성공적으로 삭제되었습니다.',
    DELETE_ERROR: '상품 삭제에 실패했습니다.',
} as const;
```

## 쿼리 무효화

### 자동 무효화

`useCRUDMutations`에 전달한 `queryKeys`에 해당하는 모든 쿼리가 자동으로 무효화됩니다:

```typescript
const mutations = useCRUDMutations({
    queryKeys: [
        productQueryKeys.organization.all,  // ['products', 'organization']
    ],
    // ...
});

// mutation 성공 시 다음 쿼리들이 모두 무효화됨:
// - ['products', 'organization']
// - ['products', 'organization', 'list', ...]
// - ['products', 'organization', 'detail', '123']
```

### 추가 무효화가 필요한 경우

다른 리소스에도 영향을 주는 경우, 콜백에서 수동으로 무효화:

```typescript
const queryClient = useQueryClient();

const mutations = useProductMutations({
    onCreateSuccess: () => {
        // 상품이 생성되면 카테고리 목록도 갱신
        queryClient.invalidateQueries({ queryKey: categoryQueryKeys.all });
    },
});
```

## 낙관적 업데이트 (선택사항)

빠른 UI 반응이 필요한 경우 낙관적 업데이트를 구현할 수 있습니다:

```typescript
import { useMutation, useQueryClient } from '@tanstack/react-query';

export function useProductMutations() {
    const queryClient = useQueryClient();

    const update = useMutation({
        mutationFn: ({ id, payload }: { id: string; payload: ProductUpdatePayload }) =>
            updateProduct(id, payload),
        onMutate: async ({ id, payload }) => {
            // 진행 중인 쿼리 취소
            await queryClient.cancelQueries({ queryKey: productQueryKeys.organization.all });

            // 이전 값 저장
            const previousData = queryClient.getQueryData(productQueryKeys.organization.all);

            // 낙관적 업데이트
            queryClient.setQueryData(productQueryKeys.organization.detail(id), (old: ProductResponse) => ({
                ...old,
                ...payload,
            }));

            return { previousData };
        },
        onError: (err, variables, context) => {
            // 에러 발생 시 롤백
            if (context?.previousData) {
                queryClient.setQueryData(productQueryKeys.organization.all, context.previousData);
            }
        },
        onSettled: () => {
            // 완료 후 무효화
            queryClient.invalidateQueries({ queryKey: productQueryKeys.organization.all });
        },
    });

    return { update };
}
```

## 모범 사례

### 1. 타입 안전성 보장

```typescript
// ✅ 좋은 예: 명시적 타입
export function useProductMutations() {
    return useCRUDMutations<ProductResponse, ProductCreatePayload, ProductUpdatePayload>(
        // ...
    );
}

// ✅ 타입 내보내기
export type ProductMutations = ReturnType<typeof useProductMutations>;
```

### 2. 콜백은 필요할 때만

```typescript
// ✅ 좋은 예: 모달 닫기 같은 UI 작업만
const mutations = useProductMutations({
    onCreateSuccess: () => setIsModalOpen(false),
});

// ❌ 나쁜 예: 로깅, 분석 등 불필요한 작업
const mutations = useProductMutations({
    onCreateSuccess: () => {
        console.log('상품 생성됨');
        analytics.track('product_created');
        // 이런 작업은 API 함수나 별도 미들웨어에서 처리
    },
});
```

### 3. 에러 처리는 중앙화

```typescript
// ✅ 좋은 예: mutation의 자동 에러 처리 활용
try {
    await mutations.create.mutateAsync(payload);
} catch {
    // 에러는 이미 토스트로 표시됨
    // 추가 UI 업데이트만 수행
}

// ❌ 나쁜 예: 중복 에러 처리
try {
    await mutations.create.mutateAsync(payload);
} catch (error) {
    showSnackbar('상품 생성 실패', { type: 'error' }); // 중복!
}
```

### 4. 로딩 상태 활용

```typescript
const mutations = useProductMutations();

// ✅ 좋은 예: isPending 활용
<button disabled={mutations.create.isPending}>
    {mutations.create.isPending ? '생성 중...' : '생성'}
</button>

// 삭제 버튼은 개별적으로 disable
<IconButton
    disabled={mutations.delete.isPending}
    onClick={() => handleDelete(id)}
>
    <DeleteIcon />
</IconButton>
```

## 관련 가이드

- [새 기능 모듈 추가 가이드](../guides/adding-new-feature.md)
- [폼 검증 패턴 가이드](./form-validation-pattern.md)
- [코딩 스타일 가이드](../conventions/code-style.md)