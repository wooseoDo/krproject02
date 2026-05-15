package kr.krproject02.domain.user.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import kr.krproject02.domain.user.dto.normal.NormalUserLoginRequest
import kr.krproject02.domain.user.dto.normal.NormalUserRegisterRequest
import kr.krproject02.domain.user.dto.normal.NormalUserResponse
import org.springframework.web.bind.annotation.RequestBody

@Tag(name = "일반 사용자", description = "일반 사용자 회원등록 및 로그인 API")
interface NormalUserApi {
    @Operation(summary = "일반 사용자 회원등록", description = "실제 존재하는 생년월일 6자리와 비밀번호를 받아 일반 사용자 계정을 생성합니다.")
    fun register(
        @Valid @RequestBody request: NormalUserRegisterRequest,
    ): NormalUserResponse

    @Operation(summary = "일반 사용자 로그인", description = "일반 사용자 scope에서 생년월일과 비밀번호를 검증합니다.")
    fun login(
        @Valid @RequestBody request: NormalUserLoginRequest,
    ): NormalUserResponse
}
