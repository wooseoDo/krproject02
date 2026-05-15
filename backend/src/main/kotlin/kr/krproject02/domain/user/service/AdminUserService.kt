package kr.krproject02.domain.user.service

import kr.krproject02.domain.user.constants.UserRole
import kr.krproject02.domain.user.dto.admin.AdminUserLoginRequest
import kr.krproject02.domain.user.dto.admin.AdminUserResponse
import kr.krproject02.domain.user.error.UserErrorCode
import kr.krproject02.domain.user.error.UserException
import kr.krproject02.domain.user.mapper.AdminUserMapper
import kr.krproject02.domain.user.repository.UserAccountRepository
import kr.krproject02.domain.user.validation.UserAccountValidator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminUserService(
    private val userAccountRepository: UserAccountRepository,
    private val userPasswordService: UserPasswordService,
    private val userAccountValidator: UserAccountValidator,
) {
    @Transactional
    fun login(request: AdminUserLoginRequest): AdminUserResponse {
        userAccountValidator.validateLogin(request.birthDate, request.password)
        // 관리자 계정은 시드된 단일 ADMIN scope 계정만 조회 대상으로 삼는다.
        val userAccount = userAccountRepository
            .findFirstByBirthDateAndRoleAndActiveTrueAndDeletedFalse(request.birthDate, UserRole.ADMIN)
            ?.takeIf { account -> userPasswordService.matches(request.password, account.passwordHash) }
            ?: throw UserException(UserErrorCode.INVALID_CREDENTIALS)
        userAccount.recordLogin()
        return AdminUserMapper.toResponse(userAccount)
    }
}

