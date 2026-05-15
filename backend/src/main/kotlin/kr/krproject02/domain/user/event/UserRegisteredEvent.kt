package kr.krproject02.domain.user.event

import kr.krproject02.common.core.event.DomainEvent
import kr.krproject02.common.core.utils.UuidV7Utils
import kr.krproject02.domain.user.constants.UserRole
import java.time.Instant
import java.util.UUID

// 일반 사용자 등록 완료 후 후속 처리(감사 로그, 알림 등)를 연결하기 위한 도메인 이벤트.
data class UserRegisteredEvent(
    val userId: UUID,
    val role: UserRole,
    override val occurredAt: Instant = Instant.now(),
    override val eventId: UUID = UuidV7Utils.generate(),
) : DomainEvent
