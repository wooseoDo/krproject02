package kr.krproject02.domain.user.dto.admin

import io.swagger.v3.oas.annotations.media.Schema
import java.time.OffsetDateTime
import java.util.UUID

@Schema(description = "관리자 사용자 응답")
data class AdminUserResponse(
    @field:Schema(description = "사용자 UUID")
    val userId: UUID?,

    @field:Schema(description = "생년월일 6자리(YYMMDD)", example = "900101")
    val birthDate: String,

    @field:Schema(description = "계정 활성 여부")
    val active: Boolean,

    @field:Schema(description = "계정 생성일시")
    val createdAt: OffsetDateTime,
)
