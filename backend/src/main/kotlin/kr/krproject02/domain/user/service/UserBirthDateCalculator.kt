package kr.krproject02.domain.user.service

import kr.krproject02.domain.user.constants.UserConstants
import org.springframework.stereotype.Component
import java.time.DateTimeException
import java.time.LocalDate

@Component
class UserBirthDateCalculator {
    fun parseBirthDate(birthDate: String): LocalDate? {
        if (!birthDate.matches(Regex(UserConstants.BIRTH_DATE_PATTERN))) {
            return null
        }

        val yearPrefix = birthDate.substring(0, 2).toInt()
        val month = birthDate.substring(2, 4).toInt()
        val day = birthDate.substring(4, 6).toInt()
        val fullYear = resolveFullYear(yearPrefix)

        return try {
            LocalDate.of(fullYear, month, day)
        } catch (_: DateTimeException) {
            null
        }
    }

    // YYMMDD 입력은 현재 연도의 뒤 2자리를 기준으로 1900년대/2000년대를 구분한다.
    private fun resolveFullYear(yearPrefix: Int): Int {
        val currentYearPrefix = LocalDate.now().year % 100
        return if (yearPrefix <= currentYearPrefix) {
            2000 + yearPrefix
        } else {
            1900 + yearPrefix
        }
    }
}
