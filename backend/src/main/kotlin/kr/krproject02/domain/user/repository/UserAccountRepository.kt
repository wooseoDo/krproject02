package kr.krproject02.domain.user.repository

import kr.krproject02.domain.user.constants.UserRole
import kr.krproject02.domain.user.entity.UserAccount
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserAccountRepository : JpaRepository<UserAccount, UUID>, UserAccountRepositoryCustom {
    fun findFirstByBirthDateAndRoleAndActiveTrueAndDeletedFalse(
        birthDate: String,
        role: UserRole,
    ): UserAccount?
}

