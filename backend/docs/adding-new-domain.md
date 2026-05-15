# 새로운 도메인 추가 가이드

이 문서는 KPR ERP 시스템에 새로운 도메인(비즈니스 엔티티)을 추가할 때 따라야 할 상세한 가이드입니다.
Customer 도메인을 참조 예시로 사용합니다.

---

## 퀵 체크리스트

새 도메인 추가 시 아래 컴포넌트들을 확인하세요.

| 컴포넌트 | 필수 | 파일 위치 | 설명 |
|----------|:----:|-----------|------|
| **Entity** | ✅ | `entity/{Domain}.kt` | JPA 엔티티, SoftDelete 상속 |
| **Repository** | ✅ | `repository/{Domain}Repository.kt` | JPA + Custom + Impl |
| **Service** | ✅ | `service/{Domain}Service.kt` | 일반 서비스 facade |
| **Query/Command Service** | ⚪ | `service/{Domain}QueryService.kt`, `service/{Domain}CommandService.kt` | 조회/변경 책임 분리 (복잡한 도메인 권장) |
| **Admin Service** | ✅ | `service/Admin{Domain}Service.kt` | 관리자용 facade (조직 격리 없음) |
| **Admin Query/Command Service** | ⚪ | `service/Admin{Domain}QueryService.kt`, `service/Admin{Domain}CommandService.kt` | 관리자 조회/변경 책임 분리 |
| **Controller** | ✅ | `controller/{Domain}Controller.kt` | REST API 엔드포인트 |
| **Admin Controller** | ✅ | `controller/Admin{Domain}Controller.kt` | 관리자용 엔드포인트 |
| **DTO** | ✅ | `dto/{Domain}*Request.kt`, `*Response.kt` | Create/Update/Search/Response |
| **Mapper** | ✅ | `mapper/{Domain}Mapper.kt` | Entity ↔ DTO 변환 (object) |
| **Error** | ✅ | `error/{Domain}ErrorCode.kt`, `Exception.kt` | 에러 코드 및 예외 |
| **Validator** | ✅ | `validation/{Domain}Validator.kt` | 비즈니스 규칙 검증 |
| **API** | ✅ | `api/{Domain}Api.kt` | OpenAPI 문서화 인터페이스 |
| **Constants** | ⚪ | `constants/{Domain}Constants.kt` | 필드 길이 상수 (권장) |
| **Event** | ⚪ | `event/{Domain}*Event.kt` | 연쇄 삭제 필요 시 |

### 네이밍 표준

| 타입 | 패턴 | 예시 |
|------|------|------|
| 조직별 서비스 | `{Domain}Service` | `CustomerService` |
| 조직별 조회/변경 서비스 | `{Domain}QueryService`, `{Domain}CommandService` | `CustomerQueryService`, `CustomerCommandService` |
| 관리자 서비스 | `Admin{Domain}Service` | `AdminCustomerService` |
| 관리자 조회/변경 서비스 | `Admin{Domain}QueryService`, `Admin{Domain}CommandService` | `AdminCustomerQueryService`, `AdminCustomerCommandService` |
| 시스템 서비스 | `System{Domain}Service` | 시스템 설정 도메인 전용 (예: `SystemPermissionCommandService`) |

### AuditLog 레벨 표준

```kotlin
DELETE  → level = AuditLogLevel.WARN
LOCK    → level = AuditLogLevel.INFO
UNLOCK  → level = AuditLogLevel.INFO
HIDE    → level = AuditLogLevel.INFO
UNHIDE  → level = AuditLogLevel.INFO
CREATE  → level = 기본값 (INFO)
UPDATE  → level = 기본값 (INFO)
```

---

## 목차
1. [사전 준비](#1-사전-준비)
2. [Entity 작성](#2-entity-작성)
3. [Database Migration 작성](#3-database-migration-작성)
4. [Repository 작성](#4-repository-작성)
5. [DTO 작성](#5-dto-작성)
6. [Mapper 작성](#6-mapper-작성)
7. [Validator 작성](#7-validator-작성)
8. [Service 작성](#8-service-작성)
9. [Controller 작성](#9-controller-작성)
10. [Error 정의](#10-error-정의)
11. [Event 정의 (선택)](#11-event-정의-선택)
12. [Permission 추가](#12-permission-추가)
13. [테스트 작성](#13-테스트-작성)

---

## 1. 사전 준비

### 1.1 도메인 분석
새로운 도메인을 추가하기 전에 다음 사항을 명확히 하세요:

- **도메인 이름**: 영문 소문자 (예: `customer`, `item`, `order`)
- **비즈니스 요구사항**: 필수 필드, 선택 필드, 제약 조건
- **다른 도메인과의 관계**: N:1, 1:N, N:M 관계 파악
- **조직 격리 필요 여부**: 멀티 테넌트 지원이 필요한가?
- **Soft Delete 필요 여부**: 대부분의 경우 필요

### 1.2 디렉토리 구조 생성
```bash
# 예시: Product 도메인 추가
mkdir -p src/main/kotlin/kr/kpr/kprerpspringboot/domain/product/{entity,repository,service,controller,dto,mapper,validation,error,event,constants}
```

생성할 패키지:
```
domain/product/
  ├── constants/      # 상수 정의
  ├── controller/     # REST API 엔드포인트
  ├── dto/            # Request/Response 객체
  ├── entity/         # JPA 엔티티
  ├── error/          # 도메인별 에러 코드
  ├── event/          # 도메인 이벤트 (선택)
  ├── mapper/         # DTO ↔ Entity 변환
  ├── repository/     # 데이터 액세스
  ├── service/        # 비즈니스 로직
  └── validation/     # 비즈니스 규칙 검증
```

---

## 2. Entity 작성

### 2.1 Constants 정의
먼저 필드 길이 제약을 상수로 정의합니다.

**파일**: `domain/product/constants/ProductConstants.kt`

```kotlin
package kr.kpr.kprerpspringboot.domain.product.constants

object ProductConstants {
    const val MAX_PRODUCT_NAME_LENGTH = 200
    const val MAX_PRODUCT_CODE_LENGTH = 50
    const val MAX_DESCRIPTION_LENGTH = 1000
    const val MAX_CATEGORY_LENGTH = 100
    const val MAX_UNIT_LENGTH = 20
}
```

### 2.2 Entity 클래스 작성

**파일**: `domain/product/entity/Product.kt`

```kotlin
package kr.kpr.kprerpspringboot.domain.product.entity

import jakarta.persistence.*
import kr.kpr.kprerpspringboot.common.entity.SoftDelete
import kr.kpr.kprerpspringboot.common.util.UuidGenerator
import kr.kpr.kprerpspringboot.domain.organization.entity.Organization
import kr.kpr.kprerpspringboot.domain.product.constants.ProductConstants.MAX_CATEGORY_LENGTH
import kr.kpr.kprerpspringboot.domain.product.constants.ProductConstants.MAX_DESCRIPTION_LENGTH
import kr.kpr.kprerpspringboot.domain.product.constants.ProductConstants.MAX_PRODUCT_CODE_LENGTH
import kr.kpr.kprerpspringboot.domain.product.constants.ProductConstants.MAX_PRODUCT_NAME_LENGTH
import kr.kpr.kprerpspringboot.domain.product.constants.ProductConstants.MAX_UNIT_LENGTH
import org.hibernate.annotations.Comment
import java.math.BigDecimal
import java.util.*

@Entity
@Table(
    name = "product",
    indexes = [
        Index(name = "ix_product_organization", columnList = "organization_id"),
        Index(name = "ix_product_code", columnList = "product_code"),
        Index(name = "ix_product_active_true", columnList = "is_active"),
        Index(name = "ix_product_not_deleted", columnList = "is_deleted"),
        Index(name = "ix_product_category", columnList = "category")
    ],
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_product_code_organization",
            columnNames = ["product_code", "organization_id", "is_deleted"]
        )
    ]
)
class Product(
    // 필수 필드
    @Column(nullable = false, length = MAX_PRODUCT_NAME_LENGTH)
    @field:Comment("제품명")
    var productName: String,

    @Column(nullable = false, length = MAX_PRODUCT_CODE_LENGTH)
    @field:Comment("제품 코드")
    var productCode: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    @field:Comment("소속 조직")
    var organization: Organization,

    // 선택 필드
    @Column(length = MAX_DESCRIPTION_LENGTH)
    @field:Comment("제품 설명")
    var description: String? = null,

    @Column(length = MAX_CATEGORY_LENGTH)
    @field:Comment("카테고리")
    var category: String? = null,

    @Column(precision = 15, scale = 2)
    @field:Comment("단가")
    var unitPrice: BigDecimal? = null,

    @Column(length = MAX_UNIT_LENGTH)
    @field:Comment("단위")
    var unit: String? = null,

    @Column(nullable = false)
    @field:Comment("활성 여부")
    var isActive: Boolean = true,

) : SoftDelete() {

    @Id
    @Column(
        name = "id",
        columnDefinition = "uuid",
        updatable = false,
        nullable = false
    )
    @field:Comment("제품 ID")
    var id: UUID? = null
        private set

    @PrePersist
    fun onPrePersist() {
        if (id == null) id = UuidGenerator.generateV7()
    }
}
```

### 2.3 Entity 작성 체크리스트

- [ ] `SoftDelete` 상속
- [ ] UUID v7 Primary Key 사용
- [ ] `@PrePersist`로 ID 자동 생성
- [ ] `@Comment`로 각 필드에 한글 설명
- [ ] `@Index`로 자주 검색하는 필드에 인덱스
- [ ] `@UniqueConstraint`로 유니크 제약 조건 (필요시)
- [ ] Organization과 `@ManyToOne(fetch = FetchType.LAZY)` 관계
- [ ] Constants에서 길이 상수 참조
- [ ] 필수 필드는 `nullable = false`
- [ ] 선택 필드는 nullable + default value

### 2.4 SoftDelete 기능

`SoftDelete` 상속 시 다음 기능이 자동으로 제공됩니다:

| 기능 | 필드 | 설명 |
|------|------|------|
| Soft Delete | `isDeleted`, `deletedAt`, `deletedBy` | 논리적 삭제 |
| Lock | `isLocked`, `lockedAt`, `lockedBy` | 수정 방지 |
| Hide | `isHidden`, `hiddenAt`, `hiddenBy` | 가시성 제어 |
| Audit | `createdAt`, `createdBy`, `updatedAt`, `updatedBy` | 감사 추적 |

```kotlin
// 삭제
entity.markDeleted(by = principal.userId)

// 잠금/해제
entity.lock(by = principal.userId)
entity.unlock()

// 숨김/해제
entity.hide(by = principal.userId)
entity.unhide()
```

### 2.5 주의사항

**DO:**
- ✅ 모든 도메인 엔티티는 `SoftDelete`를 상속
- ✅ UUID v7을 사용 (시간 순서 보장)
- ✅ 자주 검색/필터링하는 필드에 인덱스 추가
- ✅ Organization 관계는 LAZY Loading
- ✅ Comment로 한글 설명 추가 (DB 스키마 문서화)

**DON'T:**
- ❌ Random UUID 사용 금지 (성능 이슈)
- ❌ EAGER Loading 금지 (N+1 쿼리 문제)
- ❌ 하드코딩된 길이 값 사용 금지
- ❌ 양방향 관계 남용 금지 (단방향 우선)

---

## 3. Database Migration 작성

상세한 내용은 [Database Migration 가이드](./database-migration.md)를 참조하세요.

### 3.1 Migration 파일 생성

**파일**: `src/main/resources/db/migration/V001.11__Create_product_table.sql`

```sql
-- 제품(Product) 테이블 생성
CREATE TABLE product (
    id UUID PRIMARY KEY,

    -- 비즈니스 필드
    product_name VARCHAR(200) NOT NULL,
    product_code VARCHAR(50) NOT NULL,
    organization_id UUID NOT NULL,
    description VARCHAR(1000),
    category VARCHAR(100),
    unit_price DECIMAL(15, 2),
    unit VARCHAR(20),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    -- Soft Delete 필드
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMPTZ,
    deleted_by UUID,

    -- 감사(Audit) 필드
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_at TIMESTAMPTZ,
    updated_by UUID,

    -- 외래 키
    CONSTRAINT fk_product_organization FOREIGN KEY (organization_id)
        REFERENCES organization(id) ON DELETE RESTRICT,
    CONSTRAINT fk_product_created_by FOREIGN KEY (created_by)
        REFERENCES user_account(id) ON DELETE SET NULL,
    CONSTRAINT fk_product_updated_by FOREIGN KEY (updated_by)
        REFERENCES user_account(id) ON DELETE SET NULL,
    CONSTRAINT fk_product_deleted_by FOREIGN KEY (deleted_by)
        REFERENCES user_account(id) ON DELETE SET NULL
);

-- 인덱스 생성
CREATE INDEX ix_product_organization ON product(organization_id);
CREATE INDEX ix_product_code ON product(product_code);
CREATE INDEX ix_product_active_true ON product(is_active);
CREATE INDEX ix_product_not_deleted ON product(is_deleted);
CREATE INDEX ix_product_category ON product(category);

-- 유니크 제약 조건 (조직별로 제품 코드는 유일, 삭제된 항목 제외)
CREATE UNIQUE INDEX uk_product_code_organization
    ON product(product_code, organization_id)
    WHERE is_deleted = FALSE;

-- 테이블 및 컬럼 코멘트
COMMENT ON TABLE product IS '제품';
COMMENT ON COLUMN product.id IS '제품 ID';
COMMENT ON COLUMN product.product_name IS '제품명';
COMMENT ON COLUMN product.product_code IS '제품 코드';
COMMENT ON COLUMN product.organization_id IS '소속 조직';
COMMENT ON COLUMN product.description IS '제품 설명';
COMMENT ON COLUMN product.category IS '카테고리';
COMMENT ON COLUMN product.unit_price IS '단가';
COMMENT ON COLUMN product.unit IS '단위';
COMMENT ON COLUMN product.is_active IS '활성 여부';
COMMENT ON COLUMN product.is_deleted IS '삭제 여부';
COMMENT ON COLUMN product.deleted_at IS '삭제 일시';
COMMENT ON COLUMN product.deleted_by IS '삭제한 사용자 ID';
COMMENT ON COLUMN product.created_at IS '생성 일시';
COMMENT ON COLUMN product.created_by IS '생성한 사용자 ID';
COMMENT ON COLUMN product.updated_at IS '수정 일시';
COMMENT ON COLUMN product.updated_by IS '수정한 사용자 ID';
```

### 3.2 Migration 체크리스트

- [ ] 버전 번호 순서 확인 (이전 버전 +1)
- [ ] UUID v7 Primary Key (generated by application in `@PrePersist`)
- [ ] Soft Delete 필드 (`is_deleted`, `deleted_at`, `deleted_by`)
- [ ] Audit 필드 (`created_at`, `created_by`, `updated_at`, `updated_by`)
- [ ] Organization 외래 키 (`ON DELETE RESTRICT`)
- [ ] User Account 외래 키 (`ON DELETE SET NULL`)
- [ ] 인덱스 생성 (검색 필드, 조직 ID, is_deleted 등)
- [ ] 유니크 제약 조건 (필요시, Soft Delete 고려)
- [ ] 테이블 및 컬럼 코멘트 (한글)

---

## 4. Repository 작성

### 4.1 JpaRepository 인터페이스

**파일**: `domain/product/repository/ProductRepository.kt`

```kotlin
package kr.kpr.kprerpspringboot.domain.product.repository

import kr.kpr.kprerpspringboot.domain.product.entity.Product
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ProductRepository : JpaRepository<Product, UUID>, ProductRepositoryCustom {

    // 단일 조회 - Soft Delete 고려
    fun findByIdAndIsDeletedFalse(id: UUID): Product?

    fun findByProductCodeAndIsDeletedFalse(productCode: String): Product?

    // 조직별 조회 - 권한 검증
    fun findByIdAndOrganizationIdAndIsDeletedFalse(
        id: UUID,
        organizationId: UUID
    ): Product?

    fun findByProductCodeAndOrganizationIdAndIsDeletedFalse(
        productCode: String,
        organizationId: UUID
    ): Product?

    // 존재 여부 확인 - 성능 최적화
    fun existsByProductCodeAndOrganizationIdAndIsDeletedFalse(
        productCode: String,
        organizationId: UUID
    ): Boolean
}
```

### 4.2 Custom Repository 인터페이스

**파일**: `domain/product/repository/ProductRepositoryCustom.kt`

```kotlin
package kr.kpr.kprerpspringboot.domain.product.repository

import kr.kpr.kprerpspringboot.domain.product.dto.ProductSearchRequest
import kr.kpr.kprerpspringboot.domain.product.entity.Product
import org.springframework.data.domain.Page

interface ProductRepositoryCustom {
    fun products(request: ProductSearchRequest): Page<Product>
}
```

### 4.3 QueryDSL 구현

**파일**: `domain/product/repository/ProductRepositoryImpl.kt`

```kotlin
package kr.kpr.kprerpspringboot.domain.product.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.kpr.kprerpspringboot.common.persistence.querydsl.buildOrderSpecifiers
import kr.kpr.kprerpspringboot.common.util.applyFilter
import kr.kpr.kprerpspringboot.common.util.applyKeywordSearch
import kr.kpr.kprerpspringboot.domain.organization.entity.QOrganization
import kr.kpr.kprerpspringboot.domain.product.dto.ProductSearchRequest
import kr.kpr.kprerpspringboot.domain.product.entity.Product
import kr.kpr.kprerpspringboot.domain.product.entity.QProduct
import org.springframework.data.domain.Page
import org.springframework.data.support.PageableExecutionUtils

class ProductRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : ProductRepositoryCustom {

    override fun products(request: ProductSearchRequest): Page<Product> {
        val pageable = request.toPageable()
        val product = QProduct.product
        val organization = QOrganization.organization

        // 기본 조건: 소프트 삭제되지 않은 제품
        val predicate = BooleanBuilder()
            .and(product.isDeleted.isFalse)
            // 키워드 검색: 여러 필드에 대한 동적 검색
            .applyKeywordSearch(
                request.searchField,
                request.keyword,
                mapOf(
                    "productname" to { value -> product.productName.containsIgnoreCase(value) },
                    "productcode" to { value -> product.productCode.containsIgnoreCase(value) },
                    "category" to { value -> product.category.containsIgnoreCase(value) },
                    "description" to { value -> product.description.containsIgnoreCase(value) },
                )
            )
            // 필터: 활성 여부
            .applyFilter(request.isActive) { product.isActive.eq(it) }
            // 필터: 조직 ID
            .applyFilter(request.organizationId) { product.organization.id.eq(it) }
            // 필터: 카테고리
            .applyFilter(request.category?.trim()?.takeIf { it.isNotEmpty() }) {
                product.category.eq(it)
            }

        // 정렬 조건 구성
        val orderSpecifiers = buildOrderSpecifiers(
            pageable,
            mapOf(
                "createdAt" to { asc -> if (asc) product.createdAt.asc() else product.createdAt.desc() },
                "updatedAt" to { asc -> if (asc) product.updatedAt.asc() else product.updatedAt.desc() },
                "productName" to { asc -> if (asc) product.productName.asc() else product.productName.desc() },
                "productCode" to { asc -> if (asc) product.productCode.asc() else product.productCode.desc() },
                "unitPrice" to { asc -> if (asc) product.unitPrice.asc() else product.unitPrice.desc() },
                "isActive" to { asc -> if (asc) product.isActive.asc() else product.isActive.desc() },
            ),
            product.createdAt.desc() // 기본 정렬: 최신순
        )

        // 데이터 조회 쿼리
        val query = queryFactory
            .selectFrom(product)
            .leftJoin(product.organization, organization)
            .fetchJoin() // 즉시 로딩 (N+1 방지)
            .where(predicate)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())

        if (orderSpecifiers.isNotEmpty()) {
            query.orderBy(*orderSpecifiers.toTypedArray())
        }

        val content = query.fetch()

        // 카운트 쿼리 (별도)
        val countQuery = queryFactory
            .select(product.count())
            .from(product)
            .where(predicate)

        return PageableExecutionUtils.getPage(content, pageable) {
            countQuery.fetchOne() ?: 0L
        }
    }
}
```

### 4.4 Repository 작성 체크리스트

- [ ] JpaRepository 상속
- [ ] Custom 인터페이스 상속
- [ ] Soft Delete 고려 (`AndIsDeletedFalse`)
- [ ] 조직별 메서드 (`AndOrganizationId`)
- [ ] 존재 여부 확인 (`exists` 메서드)
- [ ] QueryDSL 구현 (BooleanBuilder, fetchJoin)
- [ ] 동적 검색 (applyKeywordSearch)
- [ ] 동적 필터 (applyFilter)
- [ ] 동적 정렬 (buildOrderSpecifiers)
- [ ] 페이지네이션 (PageableExecutionUtils)

---

## 5. DTO 작성

### 5.1 CreateRequest

**파일**: `domain/product/dto/ProductCreateRequest.kt`

```kotlin
package kr.kpr.kprerpspringboot.domain.product.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import kr.kpr.kprerpspringboot.domain.product.constants.ProductConstants.MAX_CATEGORY_LENGTH
import kr.kpr.kprerpspringboot.domain.product.constants.ProductConstants.MAX_DESCRIPTION_LENGTH
import kr.kpr.kprerpspringboot.domain.product.constants.ProductConstants.MAX_PRODUCT_CODE_LENGTH
import kr.kpr.kprerpspringboot.domain.product.constants.ProductConstants.MAX_PRODUCT_NAME_LENGTH
import kr.kpr.kprerpspringboot.domain.product.constants.ProductConstants.MAX_UNIT_LENGTH
import java.math.BigDecimal
import java.util.*

data class ProductCreateRequest(
    // 필수 필드
    @field:NotBlank(message = "제품명을 입력해 주세요.")
    @field:Size(max = MAX_PRODUCT_NAME_LENGTH, message = "제품명은 ${MAX_PRODUCT_NAME_LENGTH}자 이하이어야 합니다.")
    val productName: String,

    @field:NotBlank(message = "제품 코드를 입력해 주세요.")
    @field:Size(max = MAX_PRODUCT_CODE_LENGTH, message = "제품 코드는 ${MAX_PRODUCT_CODE_LENGTH}자 이하이어야 합니다.")
    val productCode: String,

    @field:NotNull(message = "소속 조직을 선택해 주세요.")
    val organizationId: UUID,

    // 선택 필드
    @field:Size(max = MAX_DESCRIPTION_LENGTH, message = "제품 설명은 ${MAX_DESCRIPTION_LENGTH}자 이하이어야 합니다.")
    val description: String? = null,

    @field:Size(max = MAX_CATEGORY_LENGTH, message = "카테고리는 ${MAX_CATEGORY_LENGTH}자 이하이어야 합니다.")
    val category: String? = null,

    val unitPrice: BigDecimal? = null,

    @field:Size(max = MAX_UNIT_LENGTH, message = "단위는 ${MAX_UNIT_LENGTH}자 이하이어야 합니다.")
    val unit: String? = null,

    val isActive: Boolean = true,
)
```

### 5.2 UpdateRequest

**파일**: `domain/product/dto/ProductUpdateRequest.kt`

```kotlin
package kr.kpr.kprerpspringboot.domain.product.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import kr.kpr.kprerpspringboot.domain.product.constants.ProductConstants.MAX_CATEGORY_LENGTH
import kr.kpr.kprerpspringboot.domain.product.constants.ProductConstants.MAX_DESCRIPTION_LENGTH
import kr.kpr.kprerpspringboot.domain.product.constants.ProductConstants.MAX_PRODUCT_NAME_LENGTH
import kr.kpr.kprerpspringboot.domain.product.constants.ProductConstants.MAX_UNIT_LENGTH
import java.math.BigDecimal

data class ProductUpdateRequest(
    // 필수 필드 (productCode는 불변이므로 제외)
    @field:NotBlank(message = "제품명을 입력해 주세요.")
    @field:Size(max = MAX_PRODUCT_NAME_LENGTH, message = "제품명은 ${MAX_PRODUCT_NAME_LENGTH}자 이하이어야 합니다.")
    val productName: String,

    // 선택 필드
    @field:Size(max = MAX_DESCRIPTION_LENGTH, message = "제품 설명은 ${MAX_DESCRIPTION_LENGTH}자 이하이어야 합니다.")
    val description: String? = null,

    @field:Size(max = MAX_CATEGORY_LENGTH, message = "카테고리는 ${MAX_CATEGORY_LENGTH}자 이하이어야 합니다.")
    val category: String? = null,

    val unitPrice: BigDecimal? = null,

    @field:Size(max = MAX_UNIT_LENGTH, message = "단위는 ${MAX_UNIT_LENGTH}자 이하이어야 합니다.")
    val unit: String? = null,

    val isActive: Boolean = true,
)
```

### 5.3 Response

**파일**: `domain/product/dto/ProductResponse.kt`

```kotlin
package kr.kpr.kprerpspringboot.domain.product.dto

import java.math.BigDecimal
import java.time.Instant
import java.util.*

data class ProductResponse(
    val id: UUID,
    val productName: String,
    val productCode: String,
    val organizationId: UUID,
    val organizationName: String,
    val description: String?,
    val category: String?,
    val unitPrice: BigDecimal?,
    val unit: String?,
    val isActive: Boolean,
    val createdAt: Instant,
    val createdBy: UUID?,
    val updatedAt: Instant?,
    val updatedBy: UUID?,
)
```

### 5.4 ListItemResponse

**파일**: `domain/product/dto/ProductListItemResponse.kt`

```kotlin
package kr.kpr.kprerpspringboot.domain.product.dto

import java.math.BigDecimal
import java.time.Instant
import java.util.*

data class ProductListItemResponse(
    val id: UUID,
    val productName: String,
    val productCode: String,
    val organizationName: String,
    val category: String?,
    val unitPrice: BigDecimal?,
    val unit: String?,
    val isActive: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant?,
)
```

### 5.5 SearchRequest

**파일**: `domain/product/dto/ProductSearchRequest.kt`

```kotlin
package kr.kpr.kprerpspringboot.domain.product.dto

import jakarta.validation.constraints.Min
import kr.kpr.kprerpspringboot.common.dto.PageableSearchRequest
import kr.kpr.kprerpspringboot.common.validation.ValidPageSize
import org.springframework.data.domain.Sort
import java.util.*

data class ProductSearchRequest(
    val keyword: String? = null,
    val searchField: String? = null,
    val isActive: Boolean? = null,
    val organizationId: UUID? = null,
    val category: String? = null,

    @field:Min(0)
    override val page: Int = 0,

    @field:ValidPageSize
    override val size: Int = 10,

    override val sort: String? = null,
) : PageableSearchRequest() {

    override val defaultSort: Sort = Sort.by(Sort.Order.desc("createdAt"))

    override fun sortableFields(): Map<String, String> = PRODUCT_SORTABLE_FIELDS
}

private val PRODUCT_SORTABLE_FIELDS = mapOf(
    "createdAt" to "createdAt",
    "updatedAt" to "updatedAt",
    "productName" to "productName",
    "productCode" to "productCode",
    "unitPrice" to "unitPrice",
    "isActive" to "isActive",
)
```

### 5.6 DTO 작성 체크리스트

- [ ] CreateRequest: 필수 필드 검증, organizationId 포함
- [ ] UpdateRequest: 불변 필드 제외 (ID, 코드 등)
- [ ] Response: 모든 필드 + 감사 필드
- [ ] ListItemResponse: 필요한 필드만 선택 (성능)
- [ ] SearchRequest: PageableSearchRequest 상속, 필터 추가
- [ ] 상수 참조 (@Size의 max 값)
- [ ] 적절한 Validation 어노테이션 (@NotBlank, @NotNull 등)
- [ ] 한글 에러 메시지

---

## 6. Mapper 작성

**파일**: `domain/product/mapper/ProductMapper.kt`

```kotlin
package kr.kpr.kprerpspringboot.domain.product.mapper

import kr.kpr.kprerpspringboot.common.util.requireEntityField
import kr.kpr.kprerpspringboot.common.util.requireEntityId
import kr.kpr.kprerpspringboot.domain.product.dto.ProductListItemResponse
import kr.kpr.kprerpspringboot.domain.product.dto.ProductResponse
import kr.kpr.kprerpspringboot.domain.product.entity.Product
import java.time.Instant
import java.util.*

object ProductMapper {

    // Entity -> DetailResponse 매핑
    fun toResponse(entity: Product): ProductResponse {
        return ProductResponse(
            id = requireEntityId<Product, UUID>(entity.id),
            productName = entity.productName,
            productCode = entity.productCode,
            organizationId = requireEntityId<Product, UUID>(entity.organization.id),
            organizationName = entity.organization.name,
            description = entity.description,
            category = entity.category,
            unitPrice = entity.unitPrice,
            unit = entity.unit,
            isActive = entity.isActive,
            createdAt = requireEntityField<Product, Instant>(entity.createdAt, "createdAt"),
            createdBy = entity.createdBy,
            updatedAt = entity.updatedAt,
            updatedBy = entity.updatedBy,
        )
    }

    // Entity -> ListItemResponse 매핑 (필드 선택)
    fun toListItem(entity: Product): ProductListItemResponse {
        return ProductListItemResponse(
            id = requireEntityId<Product, UUID>(entity.id),
            productName = entity.productName,
            productCode = entity.productCode,
            organizationName = entity.organization.name,
            category = entity.category,
            unitPrice = entity.unitPrice,
            unit = entity.unit,
            isActive = entity.isActive,
            createdAt = requireEntityField<Product, Instant>(entity.createdAt, "createdAt"),
            updatedAt = entity.updatedAt,
        )
    }
}
```

### 6.1 Mapper 작성 체크리스트

- [ ] Object로 선언 (Singleton)
- [ ] toResponse 메서드 (상세 조회용)
- [ ] toListItem 메서드 (목록 조회용)
- [ ] requireEntityId로 null 체크
- [ ] requireEntityField로 필수 필드 null 체크
- [ ] Organization 이름 매핑

---

## 7. Validator 작성

**파일**: `domain/product/validation/ProductValidator.kt`

```kotlin
package kr.kpr.kprerpspringboot.domain.product.validation

import kr.kpr.kprerpspringboot.domain.organization.repository.OrganizationRepository
import kr.kpr.kprerpspringboot.domain.product.entity.Product
import kr.kpr.kprerpspringboot.domain.product.error.ProductErrorCode
import kr.kpr.kprerpspringboot.domain.product.error.ProductException
import kr.kpr.kprerpspringboot.domain.product.repository.ProductRepository
import org.springframework.stereotype.Component
import java.util.*

@Component
class ProductValidator(
    private val productRepository: ProductRepository,
    private val organizationRepository: OrganizationRepository,
) {

    // 조직 존재 여부 검증
    fun validateOrganizationExists(organizationId: UUID) {
        if (!organizationRepository.existsByIdAndIsActiveTrueAndIsDeletedFalse(organizationId)) {
            throw ProductException(ProductErrorCode.INVALID_ORGANIZATION)
        }
    }

    // 제품 코드 중복 검증 (조직별)
    fun validateProductCodeUnique(productCode: String, organizationId: UUID) {
        if (productRepository.existsByProductCodeAndOrganizationIdAndIsDeletedFalse(
                productCode, organizationId
            )) {
            throw ProductException(ProductErrorCode.PRODUCT_CODE_CONFLICT)
        }
    }

    // 제품 조회 (관리자 scope)
    fun getProductOrThrowForAdmin(id: UUID): Product {
        return productRepository.findByIdAndIsDeletedFalse(id)
            ?: throw ProductException(ProductErrorCode.NOT_FOUND)
    }

    // 제품 조회 (조직별, 권한 검증)
    fun getProductOrThrow(id: UUID, organizationId: UUID): Product {
        return productRepository.findByIdAndOrganizationIdAndIsDeletedFalse(id, organizationId)
            ?: throw ProductException(ProductErrorCode.ACCESS_DENIED)
    }
}
```

### 7.1 Validator 작성 체크리스트

- [ ] @Component 선언
- [ ] Repository 의존성 주입
- [ ] validateOrganizationExists 메서드
- [ ] validate{Unique Field}Unique 메서드
- [ ] getEntityOrThrow(id, organizationId) 메서드 (일반 조직 scope용)
- [ ] getEntityOrThrowForAdmin(id) 메서드 (관리자 scope용)
- [ ] 신규 관리자 scope 조회 helper에 `WithoutOrgCheck` 이름을 사용하지 않음
- [ ] 적절한 Exception 발생

---

## 8. Service 작성

현재 코드베이스는 단순 도메인은 `{Domain}Service`와 `Admin{Domain}Service`만으로 구현할 수 있지만,
조회 조건, 변경 규칙, 잠금/숨김, 연쇄 삭제가 커지는 도메인은 facade 서비스가
`{Domain}QueryService` / `{Domain}CommandService`에 위임하는 구조를 우선 검토합니다.
예시는 단일 서비스로 흐름을 보여주며, 실제 구현 시 Customer, WorkFile, Recruitment 도메인의 분리 구조를 함께 참고하세요.

### 8.1 조직별 Service

**파일**: `domain/product/service/ProductService.kt`

```kotlin
package kr.kpr.kprerpspringboot.domain.product.service

import kr.kpr.kprerpspringboot.common.annotation.AuditLog
import kr.kpr.kprerpspringboot.common.annotation.AuditLogLevel
import kr.kpr.kprerpspringboot.common.dto.PageResponse
import kr.kpr.kprerpspringboot.common.extension.normalize
import kr.kpr.kprerpspringboot.common.security.UserPrincipal
import kr.kpr.kprerpspringboot.domain.organization.repository.OrganizationRepository
import kr.kpr.kprerpspringboot.domain.product.dto.*
import kr.kpr.kprerpspringboot.domain.product.entity.Product
import kr.kpr.kprerpspringboot.domain.product.event.ProductDeletedEvent
import kr.kpr.kprerpspringboot.domain.product.mapper.ProductMapper
import kr.kpr.kprerpspringboot.domain.product.repository.ProductRepository
import kr.kpr.kprerpspringboot.domain.product.validation.ProductValidator
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional(readOnly = true)
class ProductService(
    private val productRepository: ProductRepository,
    private val organizationRepository: OrganizationRepository,
    private val productValidator: ProductValidator,
    private val eventPublisher: ApplicationEventPublisher,
) {

    // 검색
    fun searchProducts(
        organizationId: UUID,
        request: ProductSearchRequest
    ): PageResponse<ProductListItemResponse> {
        val modifiedRequest = request.copy(organizationId = organizationId)
        val page = productRepository.products(modifiedRequest)
        return PageResponse.of(page, ProductMapper::toListItem)
    }

    // 단일 조회
    fun getProduct(id: UUID, organizationId: UUID): ProductResponse {
        val product = productValidator.getProductOrThrow(id, organizationId)
        return ProductMapper.toResponse(product)
    }

    // 생성
    @AuditLog(action = "CREATE", resource = "PRODUCT")
    @Transactional
    fun createProduct(
        request: ProductCreateRequest,
        organizationId: UUID
    ): ProductResponse {
        val modifiedRequest = request.copy(organizationId = organizationId)

        // 1. 비즈니스 규칙 검증
        productValidator.validateProductCodeUnique(
            modifiedRequest.productCode,
            modifiedRequest.organizationId
        )

        // 2. 관계 엔티티 로드
        val organization = organizationRepository.findByIdOrNull(modifiedRequest.organizationId)!!

        // 3. 엔티티 생성
        val product = Product(
            productName = modifiedRequest.productName,
            productCode = modifiedRequest.productCode,
            organization = organization,
            description = modifiedRequest.description.normalize(),
            category = modifiedRequest.category.normalize(),
            unitPrice = modifiedRequest.unitPrice,
            unit = modifiedRequest.unit.normalize(),
            isActive = modifiedRequest.isActive,
        )

        // 4. 저장 및 반환
        val saved = productRepository.save(product)
        return ProductMapper.toResponse(saved)
    }

    // 수정
    @AuditLog(action = "UPDATE", resource = "PRODUCT")
    @Transactional
    fun updateProduct(
        id: UUID,
        request: ProductUpdateRequest,
        organizationId: UUID
    ): ProductResponse {
        // 1. 기존 엔티티 조회 및 권한 검증
        val product = productValidator.getProductOrThrow(id, organizationId)

        // 2. 필드 업데이트
        product.apply {
            productName = request.productName
            description = request.description.normalize()
            category = request.category.normalize()
            unitPrice = request.unitPrice
            unit = request.unit.normalize()
            isActive = request.isActive
        }

        // 3. 반환 (트랜잭션 커밋 시 자동 저장)
        return ProductMapper.toResponse(product)
    }

    // 삭제
    @AuditLog(action = "DELETE", resource = "PRODUCT", level = AuditLogLevel.WARN)
    @Transactional
    fun deleteProduct(
        id: UUID,
        organizationId: UUID,
        principal: UserPrincipal
    ) {
        // 1. 기존 엔티티 조회 및 권한 검증
        val product = productValidator.getProductOrThrow(id, organizationId)

        // 2. 소프트 삭제
        product.markDeleted(by = principal.userId)

        // 3. 도메인 이벤트 발행
        eventPublisher.publishEvent(
            ProductDeletedEvent(
                productId = id,
                deletedBy = principal.userId
            )
        )
    }
}
```

### 8.2 관리자 Service (선택)

**파일**: `domain/product/service/AdminProductService.kt`

```kotlin
package kr.kpr.kprerpspringboot.domain.product.service

import kr.kpr.kprerpspringboot.common.annotation.AuditLog
import kr.kpr.kprerpspringboot.common.annotation.AuditLogLevel
import kr.kpr.kprerpspringboot.common.dto.PageResponse
import kr.kpr.kprerpspringboot.common.extension.normalize
import kr.kpr.kprerpspringboot.common.security.UserPrincipal
import kr.kpr.kprerpspringboot.domain.organization.repository.OrganizationRepository
import kr.kpr.kprerpspringboot.domain.product.dto.*
import kr.kpr.kprerpspringboot.domain.product.entity.Product
import kr.kpr.kprerpspringboot.domain.product.event.ProductDeletedEvent
import kr.kpr.kprerpspringboot.domain.product.mapper.ProductMapper
import kr.kpr.kprerpspringboot.domain.product.repository.ProductRepository
import kr.kpr.kprerpspringboot.domain.product.validation.ProductValidator
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional(readOnly = true)
class AdminProductService(
    private val productRepository: ProductRepository,
    private val organizationRepository: OrganizationRepository,
    private val productValidator: ProductValidator,
    private val eventPublisher: ApplicationEventPublisher,
) {

    // 검색: 모든 조직의 제품 조회
    fun searchProducts(request: ProductSearchRequest): PageResponse<ProductListItemResponse> {
        val page = productRepository.products(request)
        return PageResponse.of(page, ProductMapper::toListItem)
    }

    // 단일 조회: 관리자 scope
    fun getProduct(id: UUID): ProductResponse {
        val product = productValidator.getProductOrThrowForAdmin(id)
        return ProductMapper.toResponse(product)
    }

    // 생성/수정/삭제 메서드들도 유사하게 구현
    // organizationId 파라미터 없이 request.organizationId 직접 사용
}
```

### 8.3 Service 작성 체크리스트

- [ ] @Service 선언
- [ ] 클래스 레벨 @Transactional(readOnly = true)
- [ ] 쓰기 메서드에 @Transactional
- [ ] 생성/수정/삭제에 @AuditLog
- [ ] Validator 호출로 검증
- [ ] `.normalize()` 확장 함수로 공백 제거
- [ ] organizationId 강제 주입 (보안)
- [ ] 이벤트 발행 (삭제 시)
- [ ] PageResponse.of로 변환

---

## 9. Controller 작성

### 9.1 조직별 Controller

**파일**: `domain/product/controller/ProductController.kt`

```kotlin
package kr.kpr.kprerpspringboot.domain.product.controller

import jakarta.validation.Valid
import kr.kpr.kprerpspringboot.common.dto.PageResponse
import kr.kpr.kprerpspringboot.common.security.UserPrincipal
import kr.kpr.kprerpspringboot.common.security.requireOrganizationId
import kr.kpr.kprerpspringboot.domain.product.dto.*
import kr.kpr.kprerpspringboot.domain.product.service.ProductService
import kr.kpr.kprerpspringboot.common.security.permissions.ProductPermissions.PRODUCT_CREATE
import kr.kpr.kprerpspringboot.common.security.permissions.ProductPermissions.PRODUCT_DELETE
import kr.kpr.kprerpspringboot.common.security.permissions.ProductPermissions.PRODUCT_PAGE_READ
import kr.kpr.kprerpspringboot.common.security.permissions.ProductPermissions.PRODUCT_UPDATE
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/organizations/self/products")
class ProductController(
    private val productService: ProductService,
) {

    // GET /api/v1/organizations/self/products
    @GetMapping
    @PreAuthorize("hasAuthority('$PRODUCT_PAGE_READ')")
    fun searchMyOrganizationProducts(
        @Valid request: ProductSearchRequest,
        @AuthenticationPrincipal principal: UserPrincipal,
    ): ResponseEntity<PageResponse<ProductListItemResponse>> {
        val organizationId = principal.requireOrganizationId()
        val response = productService.searchProducts(organizationId, request)
        return ResponseEntity.ok(response)
    }

    // GET /api/v1/organizations/self/products/{id}
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('$PRODUCT_PAGE_READ')")
    fun getMyOrganizationProduct(
        @PathVariable id: UUID,
        @AuthenticationPrincipal principal: UserPrincipal,
    ): ResponseEntity<ProductResponse> {
        val organizationId = principal.requireOrganizationId()
        val response = productService.getProduct(id, organizationId)
        return ResponseEntity.ok(response)
    }

    // POST /api/v1/organizations/self/products
    @PostMapping
    @PreAuthorize("hasAuthority('$PRODUCT_CREATE')")
    fun createMyOrganizationProduct(
        @Valid @RequestBody request: ProductCreateRequest,
        @AuthenticationPrincipal principal: UserPrincipal,
    ): ResponseEntity<ProductResponse> {
        val organizationId = principal.requireOrganizationId()
        val response = productService.createProduct(request, organizationId)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response)
    }

    // PUT /api/v1/organizations/self/products/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('$PRODUCT_UPDATE')")
    fun updateMyOrganizationProduct(
        @PathVariable id: UUID,
        @Valid @RequestBody request: ProductUpdateRequest,
        @AuthenticationPrincipal principal: UserPrincipal,
    ): ResponseEntity<ProductResponse> {
        val organizationId = principal.requireOrganizationId()
        val response = productService.updateProduct(id, request, organizationId)
        return ResponseEntity.ok(response)
    }

    // DELETE /api/v1/organizations/self/products/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('$PRODUCT_DELETE')")
    fun deleteMyOrganizationProduct(
        @PathVariable id: UUID,
        @AuthenticationPrincipal principal: UserPrincipal,
    ): ResponseEntity<Void> {
        val organizationId = principal.requireOrganizationId()
        productService.deleteProduct(id, organizationId, principal)
        return ResponseEntity
            .noContent()
            .build()
    }
}
```

### 9.2 관리자 Controller (선택)

**파일**: `domain/product/controller/AdminProductController.kt`

```kotlin
package kr.kpr.kprerpspringboot.domain.product.controller

import jakarta.validation.Valid
import kr.kpr.kprerpspringboot.common.dto.PageResponse
import kr.kpr.kprerpspringboot.common.security.UserPrincipal
import kr.kpr.kprerpspringboot.domain.product.dto.*
import kr.kpr.kprerpspringboot.domain.product.service.AdminProductService
import kr.kpr.kprerpspringboot.common.security.permissions.ProductPermissions.ADMIN_PRODUCT_CREATE
import kr.kpr.kprerpspringboot.common.security.permissions.ProductPermissions.ADMIN_PRODUCT_DELETE
import kr.kpr.kprerpspringboot.common.security.permissions.ProductPermissions.ADMIN_PRODUCT_PAGE_READ
import kr.kpr.kprerpspringboot.common.security.permissions.ProductPermissions.ADMIN_PRODUCT_UPDATE
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/admin/products")
class AdminProductController(
    private val adminProductService: AdminProductService,
) {

    // GET /api/v1/admin/products
    @GetMapping
    @PreAuthorize("hasAuthority('$ADMIN_PRODUCT_PAGE_READ')")
    fun searchAllProducts(
        @Valid request: ProductSearchRequest,
    ): ResponseEntity<PageResponse<ProductListItemResponse>> {
        val response = adminProductService.searchProducts(request)
        return ResponseEntity.ok(response)
    }

    // 나머지 메서드들도 유사하게 구현
    // organizationId 파라미터 없이 직접 처리
}
```

### 9.3 Controller 작성 체크리스트

- [ ] @RestController 선언
- [ ] @RequestMapping으로 기본 경로 설정
- [ ] 조직별: `/api/v1/organizations/self/{domain}`
- [ ] 관리자: `/api/v1/admin/{domain}`
- [ ] @PreAuthorize로 권한 검증
- [ ] @Valid로 DTO 검증
- [ ] @AuthenticationPrincipal로 사용자 정보 주입
- [ ] principal.requireOrganizationId()로 조직 ID 추출
- [ ] 적절한 HTTP 상태 코드 (201, 200, 204)
- [ ] ResponseEntity 반환

---

## 10. Error 정의

### 10.1 ErrorCode Enum

**파일**: `domain/product/error/ProductErrorCode.kt`

```kotlin
package kr.kpr.kprerpspringboot.domain.product.error

import kr.kpr.kprerpspringboot.common.error.CommonErrorCode
import kr.kpr.kprerpspringboot.common.error.ErrorCode
import org.springframework.http.HttpStatus

enum class ProductErrorCode(
    override val httpStatus: HttpStatus,
    override val code: String,
    override val title: String
) : ErrorCode {

    // 제품 코드 중복
    PRODUCT_CODE_CONFLICT(
        HttpStatus.CONFLICT,
        "PROD_003",
        Messages.PRODUCT_CODE_CONFLICT
    ),

    // 조직 존재하지 않음
    INVALID_ORGANIZATION(
        HttpStatus.BAD_REQUEST,
        "PROD_004",
        Messages.INVALID_ORGANIZATION
    );

    override val type: String
        get() = "urn:problem:product:$name"

    private object Messages {
        const val PRODUCT_CODE_CONFLICT = "이미 사용 중인 제품 코드입니다"
        const val INVALID_ORGANIZATION = "소속 조직을 찾을 수 없습니다"
    }

    companion object {
        // 표준 오류: 제품을 찾을 수 없음
        val NOT_FOUND: ErrorCode = CommonErrorCode.NOT_FOUND.forDomain("product", "제품")

        // 표준 오류: 제품 접근 권한 없음
        val ACCESS_DENIED: ErrorCode = CommonErrorCode.FORBIDDEN.forDomain("product", "제품")
    }
}
```

### 10.2 Exception 클래스

**파일**: `domain/product/error/ProductException.kt`

```kotlin
package kr.kpr.kprerpspringboot.domain.product.error

import kr.kpr.kprerpspringboot.common.error.BaseException
import kr.kpr.kprerpspringboot.common.error.ErrorCode

class ProductException(
    errorCode: ErrorCode,
    detail: Any? = null,
    cause: Throwable? = null
) : BaseException(errorCode, detail, cause)
```

### 10.3 Error 작성 체크리스트

- [ ] Enum으로 ErrorCode 구현
- [ ] HttpStatus 지정
- [ ] 도메인별 코드 (예: PROD_XXX)
- [ ] 한글 에러 메시지
- [ ] type URI 생성
- [ ] companion object에 NOT_FOUND, ACCESS_DENIED
- [ ] BaseException 상속한 Exception 클래스

---

## 11. Event 정의 (선택)

### 11.1 DomainEvent 구현

**파일**: `domain/product/event/ProductDeletedEvent.kt`

```kotlin
package kr.kpr.kprerpspringboot.domain.product.event

import kr.kpr.kprerpspringboot.common.event.DomainEvent
import java.time.Instant
import java.util.*

data class ProductDeletedEvent(
    val productId: UUID,
    val deletedBy: UUID,
    override val occurredAt: Instant = Instant.now(),
    override val eventId: UUID = UUID.randomUUID()
) : DomainEvent
```

### 11.2 Event 발행

Service의 delete 메서드에서:

```kotlin
@Transactional
fun deleteProduct(id: UUID, organizationId: UUID, principal: UserPrincipal) {
    val product = productValidator.getProductOrThrow(id, organizationId)
    product.markDeleted(by = principal.userId)

    // 이벤트 발행
    eventPublisher.publishEvent(
        ProductDeletedEvent(
            productId = id,
            deletedBy = principal.userId
        )
    )
}
```

### 11.3 Event Listener 예시

다른 도메인에서 연쇄 삭제가 필요한 경우:

```kotlin
@Component
class ProductDeletedEventListener(
    private val productItemService: ProductItemService
) {

    @EventListener
    @Async("primaryExecutor")
    fun handleProductDeleted(event: ProductDeletedEvent) {
        // 관련 엔티티 삭제
        productItemService.deleteByProductId(event.productId)
    }
}
```

### 11.4 Event 작성 체크리스트

- [ ] DomainEvent 인터페이스 구현
- [ ] eventId, occurredAt 필드
- [ ] 도메인 특화 정보 (예: productId, deletedBy)
- [ ] Service에서 발행
- [ ] Listener에서 비동기 처리 (@Async)

---

## 12. Permission 추가

### 12.1 도메인별 Permission 상수 추가

**파일**: `common/security/permissions/ProductPermissions.kt`

도메인별 권한 상수 파일을 추가합니다.

```kotlin
package kr.kpr.kprerpspringboot.common.security.permissions

object ProductPermissions {
    const val PRODUCT_PAGE_READ = "PRODUCT_PAGE_READ"
    const val PRODUCT_LOOKUP_READ = "PRODUCT_LOOKUP_READ"
    const val PRODUCT_CREATE = "PRODUCT_CREATE"
    const val PRODUCT_UPDATE = "PRODUCT_UPDATE"
    const val PRODUCT_DELETE = "PRODUCT_DELETE"
    const val PRODUCT_LOCK = "PRODUCT_LOCK"
    const val PRODUCT_HIDE = "PRODUCT_HIDE"

    const val ADMIN_PRODUCT_PAGE_READ = "ADMIN_PRODUCT_PAGE_READ"
    const val ADMIN_PRODUCT_LOOKUP_READ = "ADMIN_PRODUCT_LOOKUP_READ"
    const val ADMIN_PRODUCT_CREATE = "ADMIN_PRODUCT_CREATE"
    const val ADMIN_PRODUCT_UPDATE = "ADMIN_PRODUCT_UPDATE"
    const val ADMIN_PRODUCT_DELETE = "ADMIN_PRODUCT_DELETE"
    const val ADMIN_PRODUCT_LOCK = "ADMIN_PRODUCT_LOCK"
    const val ADMIN_PRODUCT_HIDE = "ADMIN_PRODUCT_HIDE"
}
```

### 12.2 Migration으로 Permission 추가

**파일**: `src/main/resources/db/migration/V002.XX__seed_domain_product.sql`

```sql
-- 제품 권한 추가
INSERT INTO permission (id, key, name, description, scope, type, resource, is_active, created_at, created_by, created_by_name)
VALUES
    (uuid_generate_v4(), 'PRODUCT_PAGE_READ', '제품 페이지 조회', '제품 목록 및 상세 페이지 조회', 'ORGANIZATION', 'FUNCTIONAL', 'PRODUCT', TRUE, now(), '00000000-0000-0000-0000-000000000000'::uuid, '시스템'),
    (uuid_generate_v4(), 'PRODUCT_LOOKUP_READ', '제품 참조 조회', '제품 옵션 및 참조 조회', 'ORGANIZATION', 'FUNCTIONAL', 'PRODUCT', TRUE, now(), '00000000-0000-0000-0000-000000000000'::uuid, '시스템'),
    (uuid_generate_v4(), 'PRODUCT_CREATE', '제품 생성', '제품 생성', 'ORGANIZATION', 'FUNCTIONAL', 'PRODUCT', TRUE, now(), '00000000-0000-0000-0000-000000000000'::uuid, '시스템'),
    (uuid_generate_v4(), 'PRODUCT_UPDATE', '제품 수정', '제품 정보 수정', 'ORGANIZATION', 'FUNCTIONAL', 'PRODUCT', TRUE, now(), '00000000-0000-0000-0000-000000000000'::uuid, '시스템'),
    (uuid_generate_v4(), 'PRODUCT_DELETE', '제품 삭제', '제품 삭제', 'ORGANIZATION', 'FUNCTIONAL', 'PRODUCT', TRUE, now(), '00000000-0000-0000-0000-000000000000'::uuid, '시스템'),
    (uuid_generate_v4(), 'ADMIN_PRODUCT_PAGE_READ', '전체 제품 페이지 조회', '모든 기관의 제품 목록 및 상세 페이지 조회', 'ADMIN', 'FUNCTIONAL', 'PRODUCT', TRUE, now(), '00000000-0000-0000-0000-000000000000'::uuid, '시스템'),
    (uuid_generate_v4(), 'ADMIN_PRODUCT_UPDATE', '전체 제품 수정', '모든 기관의 제품 정보 수정', 'ADMIN', 'FUNCTIONAL', 'PRODUCT', TRUE, now(), '00000000-0000-0000-0000-000000000000'::uuid, '시스템');

-- 기존 역할에 권한 부여 (예: ORG_OWNER 역할)
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM role r
CROSS JOIN permission p
WHERE r.key = 'ORG_OWNER'
  AND p.key IN ('PRODUCT_PAGE_READ', 'PRODUCT_LOOKUP_READ', 'PRODUCT_CREATE', 'PRODUCT_UPDATE', 'PRODUCT_DELETE')
  AND NOT EXISTS (
      SELECT 1 FROM role_permission rp
      WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );
```

### 12.3 Permission 추가 체크리스트

- [ ] `common/security/permissions/{Domain}Permissions.kt`에 상수 추가
- [ ] Migration 파일 작성
- [ ] PAGE_READ, LOOKUP_READ, CREATE, UPDATE, DELETE, LOCK, HIDE 권한 검토
- [ ] 일반 권한과 `ADMIN_` 권한 구분
- [ ] scope 지정 (SYSTEM, ADMIN, ORGANIZATION)
- [ ] resource 지정
- [ ] 기존 역할에 권한 부여

---

## 13. 테스트 작성

상세한 내용은 [Testing Guidelines](./testing-guidelines.md)를 참조하세요.

### 13.1 Service 테스트 예시

**파일**: `src/test/kotlin/kr/kpr/kprerpspringboot/domain/product/service/ProductServiceTest.kt`

```kotlin
package kr.kpr.kprerpspringboot.domain.product.service

import io.mockk.*
import kr.kpr.kprerpspringboot.domain.organization.entity.Organization
import kr.kpr.kprerpspringboot.domain.organization.repository.OrganizationRepository
import kr.kpr.kprerpspringboot.domain.product.dto.ProductCreateRequest
import kr.kpr.kprerpspringboot.domain.product.entity.Product
import kr.kpr.kprerpspringboot.domain.product.error.ProductErrorCode
import kr.kpr.kprerpspringboot.domain.product.error.ProductException
import kr.kpr.kprerpspringboot.domain.product.repository.ProductRepository
import kr.kpr.kprerpspringboot.domain.product.validation.ProductValidator
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import java.util.*

class ProductServiceTest {

    private lateinit var productService: ProductService
    private lateinit var productRepository: ProductRepository
    private lateinit var organizationRepository: OrganizationRepository
    private lateinit var productValidator: ProductValidator
    private lateinit var eventPublisher: ApplicationEventPublisher

    @BeforeEach
    fun setUp() {
        productRepository = mockk()
        organizationRepository = mockk()
        productValidator = mockk()
        eventPublisher = mockk()

        productService = ProductService(
            productRepository,
            organizationRepository,
            productValidator,
            eventPublisher
        )
    }

    @Test
    fun `제품 생성 성공`() {
        // Given
        val organizationId = UUID.randomUUID()
        val request = ProductCreateRequest(
            productName = "테스트 제품",
            productCode = "TEST001",
            organizationId = organizationId
        )

        val organization = mockk<Organization> {
            every { id } returns organizationId
            every { name } returns "테스트 조직"
        }

        val savedProduct = mockk<Product> {
            every { id } returns UUID.randomUUID()
            every { productName } returns request.productName
            every { productCode } returns request.productCode
            every { this@mockk.organization } returns organization
            every { isActive } returns true
            every { createdAt } returns java.time.Instant.now()
            every { createdBy } returns null
            every { updatedAt } returns null
            every { updatedBy } returns null
        }

        every { productValidator.validateProductCodeUnique(any(), any()) } just Runs
        every { organizationRepository.findByIdOrNull(organizationId) } returns organization
        every { productRepository.save(any()) } returns savedProduct

        // When
        val response = productService.createProduct(request, organizationId)

        // Then
        assertNotNull(response)
        assertEquals(request.productName, response.productName)
        assertEquals(request.productCode, response.productCode)

        verify(exactly = 1) { productValidator.validateProductCodeUnique(request.productCode, organizationId) }
        verify(exactly = 1) { productRepository.save(any()) }
    }

    @Test
    fun `제품 코드 중복 시 예외 발생`() {
        // Given
        val organizationId = UUID.randomUUID()
        val request = ProductCreateRequest(
            productName = "테스트 제품",
            productCode = "TEST001",
            organizationId = organizationId
        )

        every { productValidator.validateProductCodeUnique(any(), any()) } throws
            ProductException(ProductErrorCode.PRODUCT_CODE_CONFLICT)

        // When & Then
        val exception = assertThrows<ProductException> {
            productService.createProduct(request, organizationId)
        }

        assertEquals(ProductErrorCode.PRODUCT_CODE_CONFLICT, exception.errorCode)
        verify(exactly = 0) { productRepository.save(any()) }
    }
}
```

### 13.2 테스트 작성 체크리스트

- [ ] Service 단위 테스트
- [ ] MockK로 의존성 모킹
- [ ] 성공 케이스 테스트
- [ ] 예외 케이스 테스트
- [ ] verify로 메서드 호출 검증
- [ ] Architecture 테스트 (ArchUnit)

---

## 최종 체크리스트

새로운 도메인 추가 시 다음 항목들을 확인하세요:

### 필수 단계
- [ ] 1. Constants 정의 (`constants/{Domain}Constants.kt`)
- [ ] 2. Entity 작성 (SoftDelete, UUID v7, 인덱스)
- [ ] 3. Database Migration 작성 (`V001.XX__Create_{domain}_table.sql`)
- [ ] 4. Repository 작성 (JPA + Custom + QueryDSL)
- [ ] 5. DTO 작성 (Create/Update/Response/ListItem/Search)
- [ ] 6. Mapper 작성 (object)
- [ ] 7. Validator 작성 (`{Domain}Validator.kt`)
- [ ] 8. Service 작성 (`{Domain}Service` + `Admin{Domain}Service`)
- [ ] 9. Controller 작성 (`{Domain}Controller` + `Admin{Domain}Controller`)
- [ ] 10. ErrorCode 및 Exception 작성
- [ ] 11. API Interface 작성 (`{Domain}Api.kt`)
- [ ] 12. Permission 추가 (도메인별 Permissions + Migration)
- [ ] 13. 테스트 작성

### 선택 단계
- [ ] Event 작성 (연쇄 삭제 필요 시)
- [ ] EventListener 작성 (다른 도메인에서)
- [ ] Lock/Hide 기능 구현 (AuditLog level 확인)

### 검증 단계
- [ ] ./gradlew.bat build 성공
- [ ] ./gradlew.bat unitTest 통과
- [ ] Architecture 테스트 통과
- [ ] API 테스트 (Postman/curl)

---

## 참고 자료

- [Database Migration 가이드](./database-migration.md)
- [Security 가이드](./security-guidelines.md)
- [Testing 가이드](./testing-guidelines.md)
- [Coding Standards](./coding-standards.md)

---

## 도움이 필요한 경우

- Customer 도메인 코드를 참조하세요 (`domain/customer/`)
- CLAUDE.md 파일의 아키텍처 섹션을 확인하세요
- Architecture 테스트가 실패하면 레이어 의존성을 확인하세요
