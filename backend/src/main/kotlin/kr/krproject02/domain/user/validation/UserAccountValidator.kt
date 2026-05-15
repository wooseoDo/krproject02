package kr.krproject02.domain.user.validation

import kr.krproject02.domain.user.constants.UserConstants
import kr.krproject02.domain.user.error.UserErrorCode
import kr.krproject02.domain.user.error.UserException
import kr.krproject02.domain.user.service.UserBirthDateCalculator
import org.springframework.stereotype.Component

@Component
class UserAccountValidator(
    private val userBirthDateCalculator: UserBirthDateCalculator,
) {
    fun validateRegistration(
        birthDate: String?,
        password: String?,
    ) {
        validateBirthDate(birthDate)
        validatePassword(password)
    }

    fun validateLogin(
        birthDate: String?,
        password: String?,
    ) {
        validateBirthDate(birthDate)
        validatePassword(password)
    }

    private fun validateBirthDate(birthDate: String?) {
        if (birthDate.isNullOrBlank() || userBirthDateCalculator.parseBirthDate(birthDate) == null) {
            throw UserException(UserErrorCode.INVALID_BIRTH_DATE)
        }
    }

    private fun validatePassword(password: String?) {
        if (
            password.isNullOrBlank() ||
            password.length < UserConstants.MIN_PASSWORD_LENGTH ||
            password.length > UserConstants.MAX_PASSWORD_LENGTH ||
            !password.matches(Regex(UserConstants.PASSWORD_PATTERN))
        ) {
            throw UserException(UserErrorCode.INVALID_PASSWORD)
        }
    }
}
