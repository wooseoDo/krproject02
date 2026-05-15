package kr.krproject02.domain.user.dto.admin

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import kr.krproject02.domain.user.constants.UserConstants

@Schema(description = "관리자 로그인 요청")
data class AdminUserLoginRequest(
    @field:Schema(description = "생년월일 6자리(YYMMDD)", example = "900101", minLength = 6, maxLength = 6)
    @field:NotBlank
    @field:Pattern(regexp = UserConstants.BIRTH_DATE_PATTERN)
    val birthDate: String,

    @field:Schema(description = "비밀번호, 4~8자리. 영문/숫자/특수문자 단일 또는 조합 가능", example = "4068", minLength = 4, maxLength = 8)
    @field:NotBlank
    @field:Size(min = UserConstants.MIN_PASSWORD_LENGTH, max = UserConstants.MAX_PASSWORD_LENGTH)
    @field:Pattern(regexp = UserConstants.PASSWORD_PATTERN)
    val password: String,
)
