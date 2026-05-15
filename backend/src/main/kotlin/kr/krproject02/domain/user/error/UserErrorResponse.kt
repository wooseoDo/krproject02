package kr.krproject02.domain.user.error

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "사용자 도메인 에러 응답")
data class UserErrorResponse(
    @field:Schema(description = "에러 코드", example = "USER_001")
    val code: String,

    @field:Schema(description = "에러 메시지", example = "생년월일은 실제 존재하는 숫자 6자리 날짜여야 합니다.")
    val message: String,
)
