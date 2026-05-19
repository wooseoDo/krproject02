package kr.krproject02.common.core.entity

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import kr.krproject02.common.core.utils.DateTimeUtils
import org.hibernate.annotations.Comment
import java.time.OffsetDateTime
import java.util.UUID

@MappedSuperclass
abstract class SoftDelete {
    @Column(name = "created_at", nullable = false)
    @field:Comment("생성 일시")
    open var createdAt: OffsetDateTime = DateTimeUtils.nowKorea()
        protected set

    @Column(name = "created_by")
    @field:Comment("생성한 사용자 UUID")
    open var createdBy: UUID? = null
        protected set

    @Column(name = "updated_at")
    @field:Comment("수정 일시")
    open var updatedAt: OffsetDateTime? = null
        protected set

    @Column(name = "updated_by")
    @field:Comment("수정한 사용자 UUID")
    open var updatedBy: UUID? = null
        protected set

    @Column(name = "is_deleted", nullable = false)
    @field:Comment("논리 삭제 여부")
    open var deleted: Boolean = false
        protected set

    @Column(name = "deleted_at")
    @field:Comment("삭제 일시")
    open var deletedAt: OffsetDateTime? = null
        protected set

    @Column(name = "deleted_by")
    @field:Comment("삭제 처리한 사용자 UUID")
    open var deletedBy: UUID? = null
        protected set

    @PrePersist
    fun onSoftDeletePrePersist() {
        createdAt = DateTimeUtils.nowKorea()
    }

    @PreUpdate
    fun onSoftDeletePreUpdate() {
        updatedAt = DateTimeUtils.nowKorea()
    }

    fun markDeleted(by: UUID? = null) {
        val now = DateTimeUtils.nowKorea()
        deleted = true
        deletedAt = now
        deletedBy = by
        updatedAt = now
        updatedBy = by
    }

    protected fun markUpdated(by: UUID? = null) {
        updatedAt = DateTimeUtils.nowKorea()
        updatedBy = by
    }
}
