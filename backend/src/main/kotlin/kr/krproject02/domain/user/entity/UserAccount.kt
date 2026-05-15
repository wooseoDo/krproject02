package kr.krproject02.domain.user.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.PrePersist
import jakarta.persistence.Table
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import kr.krproject02.common.core.entity.SoftDelete
import kr.krproject02.common.core.utils.UuidV7Utils
import kr.krproject02.domain.user.constants.UserConstants
import kr.krproject02.domain.user.constants.UserRole
import org.hibernate.annotations.Comment
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(
    name = "user_account",
    indexes = [
        Index(name = "ix_user_account_birth_date", columnList = "birth_date"),
        Index(name = "ix_user_account_role", columnList = "role"),
        Index(name = "ix_user_account_is_active", columnList = "is_active"),
        Index(name = "ix_user_account_created_at", columnList = "created_at"),
    ],
)
open class UserAccount protected constructor(
    @Id
    @Column(name = "user_id", columnDefinition = "uuid", updatable = false, nullable = false)
    @field:Comment("사용자 UUID")
    open var userId: UUID? = null,

    @field:Size(min = UserConstants.BIRTH_DATE_LENGTH, max = UserConstants.BIRTH_DATE_LENGTH)
    @field:Pattern(regexp = UserConstants.BIRTH_DATE_PATTERN)
    @Column(name = "birth_date", nullable = false, length = UserConstants.BIRTH_DATE_LENGTH)
    @field:Comment("생년월일 6자리, YYMMDD 형식")
    open var birthDate: String = "",

    @Column(name = "password_hash", nullable = false)
    @field:Comment("BCrypt 비밀번호 해시값")
    open var passwordHash: String = "",

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @field:Comment("사용자 역할")
    open var role: UserRole = UserRole.NORMAL,

    @Column(name = "is_active", nullable = false)
    @field:Comment("계정 활성 여부")
    open var active: Boolean = true,

    @Column(name = "last_login_at")
    @field:Comment("마지막 로그인 일시")
    open var lastLoginAt: OffsetDateTime? = null,
) : SoftDelete() {
    @PrePersist
    fun onPrePersist() {
        if (userId == null) {
            userId = UuidV7Utils.generate()
        }
    }

    fun recordLogin() {
        lastLoginAt = OffsetDateTime.now()
        markUpdated()
    }

    companion object {
        fun create(
            birthDate: String,
            passwordHash: String,
            role: UserRole,
        ): UserAccount =
            UserAccount(
                birthDate = birthDate,
                passwordHash = passwordHash,
                role = role,
            )
    }
}
