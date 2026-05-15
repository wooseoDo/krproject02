package kr.krproject02.domain.user.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import kr.krproject02.domain.user.dto.admin.AdminUserLoginRequest
import kr.krproject02.domain.user.dto.admin.AdminUserResponse
import org.springframework.web.bind.annotation.RequestBody

@Tag(name = "관리자 사용자", description = "관리자 로그인 API")
interface AdminUserApi {
    @Operation(summary = "관리자 로그인", description = "관리자 scope에서 생년월일과 비밀번호를 검증합니다.")
    fun login(
        @Valid @RequestBody request: AdminUserLoginRequest,
    ): AdminUserResponse
}
