package kr.krproject02.domain.user.error

class UserException(
    val errorCode: UserErrorCode,
) : RuntimeException(errorCode.message)

