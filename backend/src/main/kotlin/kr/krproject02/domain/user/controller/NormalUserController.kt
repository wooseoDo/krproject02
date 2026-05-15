package kr.krproject02.domain.user.controller

import jakarta.validation.Valid
import kr.krproject02.domain.user.api.NormalUserApi
import kr.krproject02.domain.user.dto.normal.NormalUserLoginRequest
import kr.krproject02.domain.user.dto.normal.NormalUserRegisterRequest
import kr.krproject02.domain.user.dto.normal.NormalUserResponse
import kr.krproject02.domain.user.service.NormalUserService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/normal/users")
class NormalUserController(
    private val normalUserService: NormalUserService,
) : NormalUserApi {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    override fun register(
        @Valid @RequestBody request: NormalUserRegisterRequest,
    ): NormalUserResponse =
        normalUserService.register(request)

    @PostMapping("/login")
    override fun login(
        @Valid @RequestBody request: NormalUserLoginRequest,
    ): NormalUserResponse =
        normalUserService.login(request)
}
