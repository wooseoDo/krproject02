package kr.krproject02.domain.user.service

import kr.krproject02.domain.user.constants.UserConstants
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class UserPasswordService {
    private val passwordEncoder = BCryptPasswordEncoder(UserConstants.BCRYPT_STRENGTH)

    // 원문 비밀번호는 저장하지 않고 BCrypt 해시만 DB에 보관한다.
    fun encode(rawPassword: String): String = passwordEncoder.encode(rawPassword)

    fun matches(
        rawPassword: String,
        passwordHash: String,
    ): Boolean = passwordEncoder.matches(rawPassword, passwordHash)
}

