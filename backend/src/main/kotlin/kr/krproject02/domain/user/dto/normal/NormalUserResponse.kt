package kr.krproject02.domain.user.dto.normal

import io.swagger.v3.oas.annotations.media.Schema
import java.time.OffsetDateTime
import java.util.UUID

@Schema(description = "일반 사용자 응답")
data class NormalUserResponse(
    @field:Schema(description = "사용자 UUID")
    val userId: UUID?,

    @field:Schema(description = "생년월일 6자리(YYMMDD)", example = "950312")
    val birthDate: String,

    @field:Schema(description = "계정 생성일시")
    val createdAt: OffsetDateTime,
)
