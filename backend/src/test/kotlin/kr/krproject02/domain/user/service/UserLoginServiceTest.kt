package kr.krproject02.domain.user.service

import kr.krproject02.domain.user.constants.UserRole
import kr.krproject02.domain.user.dto.admin.AdminUserLoginRequest
import kr.krproject02.domain.user.dto.normal.NormalUserLoginRequest
import kr.krproject02.domain.user.entity.UserAccount
import kr.krproject02.domain.user.error.UserErrorCode
import kr.krproject02.domain.user.error.UserException
import kr.krproject02.domain.user.repository.UserAccountRepository
import kr.krproject02.domain.user.validation.UserAccountValidator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.springframework.context.ApplicationEventPublisher

class UserLoginServiceTest {
    private val userPasswordService = UserPasswordService()
    private val userBirthDateCalculator = UserBirthDateCalculator()
    private val userAccountValidator = UserAccountValidator(userBirthDateCalculator)

    private val userAccountRepository = Mockito.mock(UserAccountRepository::class.java)
    private val eventPublisher = Mockito.mock(ApplicationEventPublisher::class.java)

    private val normalUserService = NormalUserService(
        userAccountRepository = userAccountRepository,
        userPasswordService = userPasswordService,
        userAccountValidator = userAccountValidator,
        eventPublisher = eventPublisher,
    )

    private val adminUserService = AdminUserService(
        userAccountRepository = userAccountRepository,
        userPasswordService = userPasswordService,
        userAccountValidator = userAccountValidator,
    )

    @Test
    fun `일반 사용자는 생년월일과 비밀번호가 일치하면 로그인한다`() {
        val userAccount = normalUserAccount(birthDate = "950312", rawPassword = "4068")
        Mockito.`when`(
            userAccountRepository.findFirstByBirthDateAndRoleAndActiveTrueAndDeletedFalse("950312", UserRole.NORMAL),
        ).thenReturn(userAccount)

        val response = normalUserService.login(NormalUserLoginRequest(birthDate = "950312", password = "4068"))

        assertThat(response.birthDate).isEqualTo("950312")
        assertThat(userAccount.lastLoginAt).isNotNull()
    }

    @Test
    fun `일반 사용자 로그인은 비밀번호가 다르면 실패한다`() {
        val userAccount = normalUserAccount(birthDate = "950312", rawPassword = "4068")
        Mockito.`when`(
            userAccountRepository.findFirstByBirthDateAndRoleAndActiveTrueAndDeletedFalse("950312", UserRole.NORMAL),
        ).thenReturn(userAccount)

        val exception = assertThrows<UserException> {
            normalUserService.login(NormalUserLoginRequest(birthDate = "950312", password = "0000"))
        }

        assertThat(exception.errorCode).isEqualTo(UserErrorCode.INVALID_CREDENTIALS)
    }

    @Test
    fun `관리자 로그인은 ADMIN scope 계정만 조회한다`() {
        val adminAccount = adminUserAccount(birthDate = "900101", rawPassword = "4068")
        Mockito.`when`(
            userAccountRepository.findFirstByBirthDateAndRoleAndActiveTrueAndDeletedFalse("900101", UserRole.ADMIN),
        ).thenReturn(adminAccount)

        val response = adminUserService.login(AdminUserLoginRequest(birthDate = "900101", password = "4068"))

        assertThat(response.birthDate).isEqualTo("900101")
        Mockito.verify(userAccountRepository)
            .findFirstByBirthDateAndRoleAndActiveTrueAndDeletedFalse("900101", UserRole.ADMIN)
    }

    @Test
    fun `로그인은 실제 존재하지 않는 생년월일이면 조회 전에 실패한다`() {
        val exception = assertThrows<UserException> {
            normalUserService.login(NormalUserLoginRequest(birthDate = "999999", password = "4068"))
        }

        assertThat(exception.errorCode).isEqualTo(UserErrorCode.INVALID_BIRTH_DATE)
        Mockito.verifyNoInteractions(userAccountRepository)
    }

    private fun normalUserAccount(
        birthDate: String,
        rawPassword: String,
    ): UserAccount =
        UserAccount.create(
            birthDate = birthDate,
            passwordHash = userPasswordService.encode(rawPassword),
            role = UserRole.NORMAL,
        )

    private fun adminUserAccount(
        birthDate: String,
        rawPassword: String,
    ): UserAccount =
        UserAccount.create(
            birthDate = birthDate,
            passwordHash = userPasswordService.encode(rawPassword),
            role = UserRole.ADMIN,
        )
}
