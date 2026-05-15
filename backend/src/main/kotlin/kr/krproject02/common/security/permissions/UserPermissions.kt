package kr.krproject02.common.security.permissions

object UserPermissions {
    // 일반 사용자 scope 권한
    const val USER_CREATE = "USER_CREATE"
    const val USER_LOGIN = "USER_LOGIN"

    // 관리자 scope 권한
    const val ADMIN_USER_LOGIN = "ADMIN_USER_LOGIN"
}
