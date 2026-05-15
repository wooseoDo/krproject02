# 새 기능 모듈 추가 가이드

이 가이드는 새로운 기능 모듈을 처음부터 끝까지 추가하는 방법을 단계별로 설명합니다.

## 개요

KPR ERP 시스템은 도메인 주도 설계(DDD)에 영감을 받은 모듈 구조를 사용합니다. 각 기능은 독립적인 도메인 모듈로 구성되며, admin/organization 두 가지 스코프를 지원합니다.

## 체크리스트

새 기능을 추가할 때 다음 체크리스트를 사용하세요:

- [ ] 1단계: 디렉토리 구조 생성
- [ ] 2단계: 타입 정의
- [ ] 3단계: API 함수 작성
- [ ] 4단계: 폼 설정 파일 작성
- [ ] 5단계: Query Keys 정의
- [ ] 6단계: 메시지 상수 정의
- [ ] 7단계: 페이로드 변환 함수 작성
- [ ] 8단계: 커스텀 훅 작성 (Query, Mutations, Table)
- [ ] 9단계: 모달 컴포넌트 작성
- [ ] 10단계: 페이지 컴포넌트 작성
- [ ] 11단계: 라우팅 설정
- [ ] 12단계: 권한 키 추가 (필요시)

---

## 1단계: 디렉토리 구조 생성

예시: `products` 기능을 추가한다고 가정

```bash
src/domain/products/
├── api/
│   ├── admin/
│   │   └── api.ts
│   ├── organization/
│   │   └── api.ts
│   └── apiUrls.ts
├── components/
│   ├── admin/
│   │   └── ProductModal.tsx
│   ├── organization/
│   │   └── ProductModal.tsx
│   └── shared/                    # 스코프 간 공유 컴포넌트 (선택사항)
├── config/
│   ├── productFormConfig.ts
│   └── productTableColumns.tsx    # 테이블 컬럼 정의 (선택사항)
├── constants/
│   ├── queryKeys.ts
│   └── messages.ts
├── hooks/
│   ├── admin/
│   │   ├── useAdminProductMutations.ts
│   │   └── useAdminProductsQuery.ts
│   ├── organization/
│   │   ├── useProductMutations.ts
│   │   └── useProductsQuery.ts
│   └── useProductResourceTable.ts
├── model/
│   └── payload.ts
├── pages/
│   ├── admin/
│   │   └── ProductsPage.tsx
│   └── organization/
│       └── ProductsPage.tsx
└── types/
    ├── types.ts                   # TypeScript 타입 정의
    └── schemas.ts                 # Zod 스키마 (API 응답 검증)
```

---

## 2단계: 타입 정의

`src/domain/products/types/types.ts` 파일을 생성하고 필요한 타입을 정의합니다.

```typescript
// 서버에서 받는 응답 타입
export interface ProductResponse {
    id: string;
    organizationId: string;
    productName: string;
    productCode: string;
    category: string | null;
    unitPrice: number;
    description: string | null;
    isActive: boolean;
    createdAt: string;
    updatedAt: string;
}

// 리스트 아이템 (테이블용 간소화된 타입)
export interface ProductListItem {
    id: string;
    productName: string;
    productCode: string;
    category: string | null;
    unitPrice: number;
    isActive: boolean;
}

// 생성 요청 페이로드
export interface ProductCreatePayload {
    organizationId: string;
    productName: string;
    productCode: string;
    category: string | null;
    unitPrice: number;
    description: string | null;
    isActive: boolean;
}

// 수정 요청 페이로드
export interface ProductUpdatePayload {
    productName: string;
    category: string | null;
    unitPrice: number;
    description: string | null;
    isActive: boolean;
}

// 페이지네이션 응답
export interface ProductPageResponse {
    content: ProductListItem[];
    totalElements: number;
    totalPages: number;
    size: number;
    number: number;
}
```

---

## 3단계: API 함수 작성

### 3-1. API URL 정의

`src/domain/products/api/apiUrls.ts`:

```typescript
export const PRODUCT_API_URLS = {
    ADMIN: {
        BASE: '/admin/products',
        DETAIL: (id: string) => `/admin/products/${id}`,
    },
    ORGANIZATION: {
        BASE: '/organization/products',
        DETAIL: (id: string) => `/organization/products/${id}`,
    },
} as const;
```

### 3-2. Organization API

`src/domain/products/api/organization/api.ts`:

```typescript
import { api } from '@/common/api/client/apiClient';
import type {
    ProductResponse,
    ProductListItem,
    ProductCreatePayload,
    ProductUpdatePayload,
    ProductPageResponse,
} from '@/domain/products/types/types';
import { PRODUCT_API_URLS } from '../apiUrls';
import type { PaginationParams, SortParams } from '@/common/types/api/pagination';

export async function fetchProducts(params: PaginationParams & SortParams): Promise<ProductPageResponse> {
    const response = await api.get<ProductPageResponse>(PRODUCT_API_URLS.ORGANIZATION.BASE, { params });
    return response.data;
}

export async function fetchProduct(id: string): Promise<ProductResponse> {
    const response = await api.get<ProductResponse>(PRODUCT_API_URLS.ORGANIZATION.DETAIL(id));
    return response.data;
}

export async function createProduct(payload: ProductCreatePayload): Promise<ProductResponse> {
    const response = await api.post<ProductResponse>(PRODUCT_API_URLS.ORGANIZATION.BASE, payload);
    return response.data;
}

export async function updateProduct(id: string, payload: ProductUpdatePayload): Promise<ProductResponse> {
    const response = await api.put<ProductResponse>(PRODUCT_API_URLS.ORGANIZATION.DETAIL(id), payload);
    return response.data;
}

export async function deleteProduct(id: string): Promise<void> {
    await api.delete(PRODUCT_API_URLS.ORGANIZATION.DETAIL(id));
}
```

### 3-3. Admin API

`src/domain/products/api/admin/api.ts` - 구조는 동일하되 URL만 ADMIN으로 변경

---

## 4단계: 폼 설정 파일 작성

`src/domain/products/config/productFormConfig.ts`:

```typescript
import { z } from 'zod';
import type { ModalMode } from '@/common/hooks/modal/useModalForm';
import { requiredTrimmedString, optionalString } from '@/common/utils/validation/schemas';
import type { ProductResponse, ProductCreatePayload, ProductUpdatePayload } from '../types/types';

// Zod 스키마 정의
const productFormSchema = z.object({
    productName: requiredTrimmedString('상품명', { max: 200 }),
    productCode: requiredTrimmedString('상품코드', { max: 50 }),
    category: optionalString('카테고리', { max: 100, emptyValue: 'null' }),
    unitPrice: z.coerce.number().min(0, '단가는 0 이상이어야 합니다.'),
    description: optionalString('설명', { max: 1000, emptyValue: 'null' }),
    isActive: z.boolean(),
});

export type ProductFormValues = z.infer<typeof productFormSchema>;

export interface ProductFormSchemaOptions {
    mode: ModalMode;
}

// 스키마 빌더
export const buildProductFormSchema = ({ mode }: ProductFormSchemaOptions) => {
    // 생성 모드일 때만 productCode 수정 가능하도록 처리하려면
    // 여기서 스키마를 동적으로 변경할 수 있음
    return productFormSchema;
};

// 기본값 생성 함수
export const createProductDefaultValues = (): ProductFormValues => ({
    productName: '',
    productCode: '',
    category: '',
    unitPrice: 0,
    description: '',
    isActive: true,
});

// 엔티티 → 폼 값 매핑
export const mapProductResponseToFormValues = (product: ProductResponse): ProductFormValues => ({
    productName: product.productName,
    productCode: product.productCode,
    category: product.category ?? '',
    unitPrice: product.unitPrice,
    description: product.description ?? '',
    isActive: product.isActive,
});

// 폼 값 → 생성 페이로드 변환
export const buildProductCreatePayload = (
    values: ProductFormValues,
    organizationId: string,
): ProductCreatePayload => ({
    organizationId,
    productName: values.productName,
    productCode: values.productCode,
    category: values.category || null,
    unitPrice: values.unitPrice,
    description: values.description || null,
    isActive: values.isActive,
});

// 폼 값 → 수정 페이로드 변환
export const buildProductUpdatePayload = (values: ProductFormValues): ProductUpdatePayload => ({
    productName: values.productName,
    category: values.category || null,
    unitPrice: values.unitPrice,
    description: values.description || null,
    isActive: values.isActive,
});
```

**참고**: 더 자세한 폼 검증 패턴은 [폼 검증 패턴 가이드](../patterns/form-validation-pattern.md)를 참조하세요.

---

## 5단계: Query Keys 정의

`src/domain/products/constants/queryKeys.ts`:

```typescript
export const productQueryKeys = {
    admin: {
        all: ['admin', 'products'] as const,
        list: (params: unknown) => [...productQueryKeys.admin.all, 'list', params] as const,
        detail: (id: string) => [...productQueryKeys.admin.all, 'detail', id] as const,
        options: () => [...productQueryKeys.admin.all, 'options'] as const,
    },
    organization: {
        all: ['organizations', 'self', 'products'] as const,
        list: (params: unknown) => [...productQueryKeys.organization.all, 'list', params] as const,
        detail: (id: string) => [...productQueryKeys.organization.all, 'detail', id] as const,
        options: () => [...productQueryKeys.organization.all, 'options'] as const,
    },
} as const;
```

**중요**: Query Key 패턴은 API URL 구조와 일치해야 합니다:
- Admin: `['admin', '{entity}', ...]`
- Organization: `['organizations', 'self', '{entity}', ...]`

---

## 6단계: 메시지 상수 정의

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

---

## 7단계: 페이로드 변환 함수 작성 (선택사항)

복잡한 변환 로직이 필요한 경우 `src/domain/products/model/payload.ts`에 별도로 작성:

```typescript
import type { ProductFormValues } from '../config/productFormConfig';
import type { ProductCreatePayload, ProductUpdatePayload } from '../types/types';

export function buildProductCreatePayload(
    values: ProductFormValues,
    organizationId: string,
): ProductCreatePayload {
    return {
        organizationId,
        productName: values.productName.trim(),
        productCode: values.productCode.trim().toUpperCase(),
        category: values.category?.trim() || null,
        unitPrice: values.unitPrice,
        description: values.description?.trim() || null,
        isActive: values.isActive,
    };
}

export function buildProductUpdatePayload(values: ProductFormValues): ProductUpdatePayload {
    return {
        productName: values.productName.trim(),
        category: values.category?.trim() || null,
        unitPrice: values.unitPrice,
        description: values.description?.trim() || null,
        isActive: values.isActive,
    };
}
```

---

## 8단계: 커스텀 훅 작성

### 8-1. Mutations Hook

`src/domain/products/hooks/organization/useProductMutations.ts`:

```typescript
import { useCRUDMutations } from '@/common/hooks/crud/useCRUDMutations';
import { productQueryKeys } from '@/domain/products/constants/queryKeys';
import { PRODUCT_MESSAGES } from '@/domain/products/constants/messages';
import {
    createProduct,
    updateProduct,
    deleteProduct,
} from '@/domain/products/api/organization/api';
import type {
    ProductResponse,
    ProductCreatePayload,
    ProductUpdatePayload,
} from '@/domain/products/types/types';

interface UseProductMutationsOptions {
    onCreateSuccess?: () => void;
    onUpdateSuccess?: () => void;
    onDeleteSuccess?: () => void;
}

export function useProductMutations(options: UseProductMutationsOptions = {}) {
    return useCRUDMutations<ProductResponse, ProductCreatePayload, ProductUpdatePayload>(
        {
            queryKeys: [productQueryKeys.organization.all],
            api: {
                create: createProduct,
                update: updateProduct,
                delete: deleteProduct,
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

// named export만 사용 (default export 금지)
export type ProductMutations = ReturnType<typeof useProductMutations>;
```

**참고**: CRUD 패턴에 대한 자세한 내용은 [CRUD 패턴 가이드](../patterns/crud-pattern.md)를 참조하세요.

### 8-2. Query Hook

`src/domain/products/hooks/organization/useProductsQuery.ts`:

```typescript
import { useQuery } from '@tanstack/react-query';
import { fetchProducts } from '@/domain/products/api/organization/api';
import { productQueryKeys } from '@/domain/products/constants/queryKeys';
import type { PaginationParams, SortParams } from '@/common/types/api/pagination';

export function useProductsQuery(params: PaginationParams & SortParams) {
    return useQuery({
        queryKey: productQueryKeys.organization.list(params),
        queryFn: () => fetchProducts(params),
        staleTime: 1000 * 60 * 5, // 5분
    });
}
```

### 8-3. Table Hook

`src/domain/products/hooks/useProductResourceTable.ts`:

```typescript
import { useServerTableParams } from '@/common/hooks/useServerTableParams';

interface UseProductResourceTableOptions {
    sortFieldMap: Record<string, string>;
    initialPageSize?: number;
}

export function useProductResourceTable({ sortFieldMap, initialPageSize = 10 }: UseProductResourceTableOptions) {
    const table = useServerTableParams({
        sortFieldMap,
        initialPageSize,
        enableFilters: true,
        enableSearch: true,
    });

    return {
        ...table,
        queryInput: {
            page: table.tableParams.pagination.page,
            size: table.tableParams.pagination.size,
            sort: table.tableParams.sort.column
                ? `${table.tableParams.sort.column},${table.tableParams.sort.direction}`
                : undefined,
            keyword: table.tableParams.keyword || undefined,
            searchField: table.searchField !== 'all' ? table.searchField : undefined,
            isActive: table.statusFilter === 'all' ? undefined : table.statusFilter === 'active',
        },
    };
}

export type UseProductResourceTableResult = ReturnType<typeof useProductResourceTable>;
```

---

## 9단계: 모달 컴포넌트 작성

`src/domain/products/components/organization/ProductModal.tsx`:

```typescript
import { ModalSection } from '@/common/components/modal/ModalSection';
import { FormTextField } from '@/common/components/form/FormTextField';
import { FormTextarea } from '@/common/components/form/FormTextarea';
import { FormSwitch } from '@/common/components/form/FormSwitch';
import { ResourceModal, type ResourceModalChildrenProps } from '@/common/components/modal/ResourceModal';
import { PERMISSION_KEYS } from '@/common/constants/permissionKeys';
import { useCanAccess } from '@/common/hooks/useCanAccess';
import type { ProductCreatePayload, ProductResponse, ProductUpdatePayload } from '@/domain/products/types/types';
import {
    buildProductFormSchema,
    createProductDefaultValues,
    type ProductFormValues,
    mapProductResponseToFormValues,
    buildProductCreatePayload,
    buildProductUpdatePayload,
} from '@/domain/products/config/productFormConfig';

const confirmDialogOptions = {
    create: {
        title: '상품 등록 확인',
        description: '상품을 등록하시겠습니까?',
        confirmLabel: '등록',
    },
    edit: {
        title: '상품 수정 확인',
        description: '상품 정보를 수정하시겠습니까?',
        confirmLabel: '수정',
    },
} as const;

type BaseProps = {
    open: boolean;
    isSubmitting?: boolean;
    onCancel: () => void;
};

type CreateProps = BaseProps & {
    mode: 'create';
    initialData?: null;
    organizationId: string;
    onSubmit: (payload: ProductCreatePayload) => void | Promise<void>;
};

type EditProps = BaseProps & {
    mode: 'edit';
    initialData: ProductResponse;
    onSubmit: (payload: ProductUpdatePayload) => void | Promise<void>;
};

type ProductModalProps = CreateProps | EditProps;

export function ProductModal(props: ProductModalProps) {
    const { open, isSubmitting = false, onCancel } = props;
    const mode = props.mode;

    const canUpdate = useCanAccess(PERMISSION_KEYS.PRODUCT_UPDATE);
    const isReadOnly = mode === 'edit' && !canUpdate;

    if (mode === 'edit' && !canUpdate) {
        return null;
    }

    const config = {
        maxWidth: 'md' as const,
        title: {
            create: '상품 생성',
            edit: '상품 수정',
        },
        description: {
            create: '새로운 상품 정보를 입력해 주세요.',
            edit: '상품 정보를 수정해 주세요.',
        },
        buildSchema: buildProductFormSchema,
        createDefaultValues,
        mapEntityToFormValues: mapProductResponseToFormValues,
        confirmOptions: confirmDialogOptions,
    };

    const renderContent = ({ control, isCreate }: ResourceModalChildrenProps<ProductFormValues>) => (
        <>
            <ModalSection title="기본 정보" description="상품의 기본 정보를 입력해 주세요.">
                <FormTextField
                    control={control}
                    name="productName"
                    label="상품명"
                    placeholder="상품명을 입력하세요"
                    disabled={isReadOnly}
                    required
                />

                <FormTextField
                    control={control}
                    name="productCode"
                    label="상품코드"
                    placeholder="예: PROD-001"
                    disabled={isReadOnly || !isCreate}
                    required
                    helperText={isCreate ? '상품코드 (생성 후 변경 불가)' : undefined}
                />

                <FormTextField
                    control={control}
                    name="category"
                    label="카테고리"
                    placeholder="카테고리를 입력하세요"
                    disabled={isReadOnly}
                />

                <FormTextField
                    control={control}
                    name="unitPrice"
                    label="단가"
                    type="number"
                    placeholder="0"
                    disabled={isReadOnly}
                    required
                />

                <FormTextarea
                    control={control}
                    name="description"
                    label="설명"
                    placeholder="상품 설명을 입력하세요"
                    disabled={isReadOnly}
                    minRows={3}
                />

                <FormSwitch control={control} name="isActive" label="활성 상태" disabled={isReadOnly} />
            </ModalSection>
        </>
    );

    if (mode === 'create') {
        return (
            <ResourceModal<ProductFormValues, ProductResponse, ProductCreatePayload, ProductUpdatePayload>
                mode="create"
                open={open}
                onSubmit={props.onSubmit}
                onCancel={onCancel}
                isSubmitting={isSubmitting}
                buildCreatePayload={(values) => buildProductCreatePayload(values, props.organizationId)}
                config={config}
            >
                {renderContent}
            </ResourceModal>
        );
    }

    return (
        <ResourceModal<ProductFormValues, ProductResponse, ProductCreatePayload, ProductUpdatePayload>
            mode="edit"
            open={open}
            initialData={props.initialData}
            onSubmit={props.onSubmit}
            onCancel={onCancel}
            isSubmitting={isSubmitting}
            buildUpdatePayload={buildProductUpdatePayload}
            config={config}
        >
            {renderContent}
        </ResourceModal>
    );
}
```

---

## 10단계: 페이지 컴포넌트 작성

`src/domain/products/pages/organization/ProductsPage.tsx`:

```typescript
import { Alert, Chip, CircularProgress, IconButton, Stack, Typography } from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import { MenuLayout } from '@/layouts/components/MenuLayout';
import { DataTable, type DataTableColumn } from '@/common/components/table/DataTable';
import { ResourceSearchFilterToolbar, type SearchFieldOption } from '@/common/components/filters/ResourceSearchFilterToolbar';
import { ConfirmDialog } from '@/common/components/modal/ConfirmDialog';
import { useAuth } from '@/domain/auth/hooks/useAuth';
import { PERMISSION_KEYS } from '@/common/constants/permissionKeys';
import { useCanAccess } from '@/common/hooks/useCanAccess';
import { fetchProduct } from '@/domain/products/api/organization/api';
import type { ProductCreatePayload, ProductListItem, ProductResponse, ProductUpdatePayload } from '@/domain/products/types/types';
import { ProductModal } from '@/domain/products/components/organization/ProductModal';
import { useProductResourceTable } from '@/domain/products/hooks/useProductResourceTable';
import { useProductsQuery } from '@/domain/products/hooks/organization/useProductsQuery';
import { useProductMutations } from '@/domain/products/hooks/organization/useProductMutations';
import { useResourceListPage } from '@/common/hooks/page/useResourceListPage';

const searchFieldOptions: SearchFieldOption[] = [
    { value: 'all', label: '전체' },
    { value: 'productName', label: '상품명' },
    { value: 'productCode', label: '상품코드' },
];

const PRODUCT_SORT_FIELD_MAP: Record<string, string> = {
    productName: 'productName',
    productCode: 'productCode',
    unitPrice: 'unitPrice',
    createdAt: 'createdAt',
};

interface ProductsPageContentProps {
    organizationId: string;
}

function ProductsPageContent({ organizationId }: ProductsPageContentProps) {
    const canCreate = useCanAccess(PERMISSION_KEYS.PRODUCT_CREATE);
    const canUpdate = useCanAccess(PERMISSION_KEYS.PRODUCT_UPDATE);
    const canDelete = useCanAccess(PERMISSION_KEYS.PRODUCT_DELETE);

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

    const columns: DataTableColumn<ProductListItem>[] = [
        {
            id: 'productName',
            header: '상품명',
            cell: (product) => product.productName,
            sortable: true,
        },
        {
            id: 'productCode',
            header: '상품코드',
            cell: (product) => product.productCode,
            sortable: true,
        },
        {
            id: 'category',
            header: '카테고리',
            cell: (product) => product.category || '-',
        },
        {
            id: 'unitPrice',
            header: '단가',
            cell: (product) => `${product.unitPrice.toLocaleString()}원`,
            sortable: true,
            align: 'right',
        },
        {
            id: 'isActive',
            header: '상태',
            cell: (product) => (
                <Chip
                    label={product.isActive ? '활성' : '비활성'}
                    color={product.isActive ? 'success' : 'default'}
                    size="small"
                />
            ),
        },
        {
            id: 'actions',
            header: '작업',
            align: 'right',
            cell: (product) => (
                <Stack direction="row" spacing={1} justifyContent="flex-end">
                    {canUpdate && (
                        <IconButton size="small" onClick={() => handleEdit(product)}>
                            <EditIcon fontSize="small" />
                        </IconButton>
                    )}
                    {canDelete && (
                        <IconButton
                            size="small"
                            color="error"
                            disabled={mutations.delete.isPending}
                            onClick={() => handleDelete(product.id)}
                        >
                            <DeleteIcon fontSize="small" />
                        </IconButton>
                    )}
                </Stack>
            ),
        },
    ];

    if (query.isLoading) {
        return (
            <Stack alignItems="center" spacing={2} sx={{ py: 8 }}>
                <CircularProgress size={48} thickness={4} />
                <Typography variant="body2" color="text.secondary">
                    상품 목록을 불러오는 중입니다...
                </Typography>
            </Stack>
        );
    }

    if (query.isError) {
        return <Alert severity="error">상품 목록을 불러오지 못했습니다. 새로고침 후 다시 시도해 주세요.</Alert>;
    }

    const productModalProps =
        modal.state.mode === 'create'
            ? {
                  mode: 'create' as const,
                  initialData: null,
                  organizationId,
                  onSubmit: async (payload: ProductCreatePayload) => {
                      await mutations.create.mutateAsync(payload);
                  },
              }
            : {
                  mode: 'edit' as const,
                  initialData: modal.state.target!,
                  onSubmit: async (payload: ProductUpdatePayload) => {
                      await mutations.update.mutateAsync({ id: modal.state.target!.id, payload });
                  },
              };

    return (
        <>
            <MenuLayout
                headerProps={canCreate ? { onCreateClick: handleCreate, createButtonLabel: '상품 생성' } : undefined}
                filterArea={
                    <ResourceSearchFilterToolbar
                        keyword={table.tableParams.keyword}
                        onKeywordChange={(e) => table.handleKeywordChange(e.target.value)}
                        searchPlaceholder="상품명 또는 상품코드 검색"
                        searchField={table.searchField ?? 'all'}
                        onSearchFieldChange={(e) => table.handleSearchFieldChange?.(e.target.value)}
                        searchFieldOptions={searchFieldOptions}
                        filters={[]}
                        onResetFilters={table.handleResetFilters}
                        hasActiveFilters={table.hasActiveFilters}
                        rowsPerPage={table.tableParams.pagination.size}
                        onRowsPerPageChange={(e) => table.handleRowsPerPageChange(Number(e.target.value))}
                        rowsPerPageOptions={[10, 20, 50]}
                    />
                }
            >
                <DataTable
                    data={items}
                    columns={columns}
                    keyExtractor={(product) => product.id}
                    size="small"
                    enablePagination
                    paginationMode="server"
                    paginationState={{
                        page: table.tableParams.pagination.page,
                        rowsPerPage: table.tableParams.pagination.size,
                    }}
                    onPaginationChange={table.tableParams.handlePaginationChange}
                    sortState={table.tableParams.sort}
                    onSortChange={table.tableParams.handleSortChange}
                    rowsPerPageOptions={[10, 20, 50]}
                    totalRowCount={totalRows}
                    emptyContent={
                        <Typography variant="body2" color="text.secondary">
                            등록된 상품이 없습니다.
                        </Typography>
                    }
                />
            </MenuLayout>
            <ProductModal open={modal.state.open} isSubmitting={isSubmitting} onCancel={modal.close} {...productModalProps} />
            <ConfirmDialog {...confirmDialog.dialogProps} />
        </>
    );
}

export default function ProductsPage() {
    const { userSummary } = useAuth();
    const organizationId = userSummary?.organizationId;

    if (!organizationId) {
        return <Alert severity="error">조직 정보를 불러올 수 없습니다. 다시 로그인해 주세요.</Alert>;
    }

    return <ProductsPageContent organizationId={organizationId} />;
}
```

---

## 11단계: 라우팅 설정

`src/router/AppRouter.tsx`에 라우트를 추가합니다:

```typescript
// 1. lazy import 추가
const ProductsPage = lazy(() => import('@/domain/products/pages/organization/ProductsPage'));

// 2. 리다이렉트 옵션에 추가 (필요시)
const ProductManagementIndexRedirect = createIndexRedirect([
    { path: 'products', permissions: [PERMISSION_KEYS.PRODUCT_READ] },
]);

// 3. Routes 안에 경로 추가
<Route
    path="product-management"
    element={
        <PermissionGuard required={[PERMISSION_KEYS.PRODUCT_READ]}>
            <Outlet />
        </PermissionGuard>
    }
>
    <Route index element={<ProductManagementIndexRedirect />} />
    <Route
        path="products"
        element={
            <PermissionGuard required={PERMISSION_KEYS.PRODUCT_READ}>
                <ProductsPage />
            </PermissionGuard>
        }
    />
</Route>
```

---

## 12단계: 권한 키 추가

`src/common/constants/permissionKeys.ts`에 권한 키를 추가합니다:

```typescript
export const PERMISSION_KEYS = {
    // ... 기존 권한들
    PRODUCT_READ: 'PRODUCT_READ' as const,
    PRODUCT_CREATE: 'PRODUCT_CREATE' as const,
    PRODUCT_UPDATE: 'PRODUCT_UPDATE' as const,
    PRODUCT_DELETE: 'PRODUCT_DELETE' as const,
};
```

---

## 완료!

위 단계를 모두 완료하면 새로운 기능 모듈이 완성됩니다.

## 다음 단계

- Admin 스코프가 필요한 경우, organization과 동일한 구조로 admin 폴더에 추가
- 테스트 코드 작성 (각 컴포넌트/훅에 `__tests__` 디렉토리)
- 복잡한 비즈니스 로직이 있다면 별도 서비스 레이어 추가

## 관련 가이드

- [CRUD 패턴 가이드](../patterns/crud-pattern.md)
- [폼 검증 패턴 가이드](../patterns/form-validation-pattern.md)
- [코딩 스타일 가이드](../conventions/code-style.md)