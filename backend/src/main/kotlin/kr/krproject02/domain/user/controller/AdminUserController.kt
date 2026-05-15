package kr.krproject02.domain.user.controller

import jakarta.validation.Valid
import kr.krproject02.domain.user.api.AdminUserApi
import kr.krproject02.domain.user.dto.admin.AdminUserLoginRequest
import kr.krproject02.domain.user.dto.admin.AdminUserResponse
import kr.krproject02.domain.user.service.AdminUserService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin/users")
class AdminUserController(
    private val adminUserService: AdminUserService,
) : AdminUserApi {
    @PostMapping("/login")
    override fun login(
        @Valid @RequestBody request: AdminUserLoginRequest,
    ): AdminUserResponse =
        adminUserService.login(request)
}
