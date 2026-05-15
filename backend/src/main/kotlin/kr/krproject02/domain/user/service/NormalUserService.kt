package kr.krproject02.domain.user.service

import kr.krproject02.domain.user.constants.UserRole
import kr.krproject02.domain.user.dto.normal.NormalUserLoginRequest
import kr.krproject02.domain.user.dto.normal.NormalUserRegisterRequest
import kr.krproject02.domain.user.dto.normal.NormalUserResponse
import kr.krproject02.domain.user.entity.UserAccount
import kr.krproject02.domain.user.error.UserErrorCode
import kr.krproject02.domain.user.error.UserException
import kr.krproject02.domain.user.event.UserRegisteredEvent
import kr.krproject02.domain.user.mapper.NormalUserMapper
import kr.krproject02.domain.user.repository.UserAccountRepository
import kr.krproject02.domain.user.validation.UserAccountValidator
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NormalUserService(
    private val userAccountRepository: UserAccountRepository,
    private val userPasswordService: UserPasswordService,
    private val userAccountValidator: UserAccountValidator,
    private val eventPublisher: ApplicationEventPublisher,
) {
    @Transactional
    fun register(request: NormalUserRegisterRequest): NormalUserResponse {
        userAccountValidator.validateRegistration(request.birthDate, request.password)
        val userAccount = UserAccount.create(
            birthDate = request.birthDate,
            passwordHash = userPasswordService.encode(request.password),
            role = UserRole.NORMAL,
        )
        val savedUserAccount = userAccountRepository.save(userAccount)
        // 가입 이후 알림/감사 로그 같은 후속 처리를 도메인 이벤트로 분리한다.
        savedUserAccount.userId?.let { userId ->
            eventPublisher.publishEvent(UserRegisteredEvent(userId = userId, role = savedUserAccount.role))
        }
        return NormalUserMapper.toResponse(savedUserAccount)
    }

    @Transactional
    fun login(request: NormalUserLoginRequest): NormalUserResponse {
        userAccountValidator.validateLogin(request.birthDate, request.password)
        // 같은 생년월일이 있더라도 일반 사용자 scope 안에서만 비밀번호를 검증한다.
        val userAccount = userAccountRepository
            .findFirstByBirthDateAndRoleAndActiveTrueAndDeletedFalse(request.birthDate, UserRole.NORMAL)
            ?.takeIf { account -> userPasswordService.matches(request.password, account.passwordHash) }
            ?: throw UserException(UserErrorCode.INVALID_CREDENTIALS)
        userAccount.recordLogin()
        return NormalUserMapper.toResponse(userAccount)
    }
}

