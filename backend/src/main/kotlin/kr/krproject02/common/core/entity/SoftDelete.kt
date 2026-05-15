package kr.krproject02.common.core.entity

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import org.hibernate.annotations.Comment
import java.time.OffsetDateTime
import java.util.UUID

@MappedSuperclass
abstract class SoftDelete {
    @Column(name = "created_at", nullable = false)
    @field:Comment("생성일시")
    open var createdAt: OffsetDateTime = OffsetDateTime.now()
        protected set

    @Column(name = "created_by")
    @field:Comment("생성자 사용자 UUID")
    open var createdBy: UUID? = null
        protected set

    @Column(name = "updated_at")
    @field:Comment("수정일시")
    open var updatedAt: OffsetDateTime? = null
        protected set

    @Column(name = "updated_by")
    @field:Comment("수정자 사용자 UUID")
    open var updatedBy: UUID? = null
        protected set

    @Column(name = "is_deleted", nullable = false)
    @field:Comment("논리 삭제 여부")
    open var deleted: Boolean = false
        protected set

    @Column(name = "deleted_at")
    @field:Comment("삭제일시")
    open var deletedAt: OffsetDateTime? = null
        protected set

    @Column(name = "deleted_by")
    @field:Comment("삭제 처리자 사용자 UUID")
    open var deletedBy: UUID? = null
        protected set

    // 실제 레코드는 유지하고 삭제 상태와 감사 필드만 갱신한다.
    fun markDeleted(by: UUID? = null) {
        deleted = true
        deletedAt = OffsetDateTime.now()
        deletedBy = by
        updatedAt = deletedAt
        updatedBy = by
    }

    // 도메인 내부 상태 변경 시 updated_* 감사 필드를 일관되게 갱신한다.
    protected fun markUpdated(by: UUID? = null) {
        updatedAt = OffsetDateTime.now()
        updatedBy = by
    }
}
