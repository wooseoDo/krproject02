package kr.krproject02.domain.user.mapper

import kr.krproject02.domain.user.dto.normal.NormalUserResponse
import kr.krproject02.domain.user.entity.UserAccount

object NormalUserMapper {
    fun toResponse(userAccount: UserAccount): NormalUserResponse =
        NormalUserResponse(
            userId = userAccount.userId,
            birthDate = userAccount.birthDate,
            createdAt = userAccount.createdAt,
        )
}
