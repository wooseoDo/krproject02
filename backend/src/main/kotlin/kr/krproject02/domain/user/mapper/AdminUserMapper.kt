package kr.krproject02.domain.user.mapper

import kr.krproject02.domain.user.dto.admin.AdminUserResponse
import kr.krproject02.domain.user.entity.UserAccount

object AdminUserMapper {
    fun toResponse(userAccount: UserAccount): AdminUserResponse =
        AdminUserResponse(
            userId = userAccount.userId,
            birthDate = userAccount.birthDate,
            active = userAccount.active,
            createdAt = userAccount.createdAt,
        )
}
