package kr.krproject02.domain.user.error

import org.springframework.http.HttpStatus

enum class UserErrorCode(
    val httpStatus: HttpStatus,
    val code: String,
    val message: String,
) {
    INVALID_BIRTH_DATE(HttpStatus.BAD_REQUEST, "USER_001", "생년월일은 실제 존재하는 숫자 6자리 날짜여야 합니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "USER_002", "비밀번호는 4자리 이상 8자리 이하로 입력해야 합니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "USER_003", "생년월일 또는 비밀번호가 올바르지 않습니다."),
}
