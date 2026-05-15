package kr.krproject02.domain.user.constants

object UserConstants {
    // BCrypt 비용 계수. 값이 높을수록 안전하지만 로그인/가입 처리 시간이 늘어난다.
    const val BCRYPT_STRENGTH = 10

    const val BIRTH_DATE_LENGTH = 6
    const val BIRTH_DATE_PATTERN = "^[0-9]{6}$"

    const val MIN_PASSWORD_LENGTH = 4
    const val MAX_PASSWORD_LENGTH = 8
    const val PASSWORD_PATTERN = "^[A-Za-z0-9\\p{Punct}]{4,8}$"
}
