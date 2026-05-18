package kr.krproject02.common.security.permissions

object SurveyPermissions {
    // 일반 사용자 scope: 조사지 조회, 참여, 본인 응답 관리 권한
    const val SURVEY_PAGE_READ = "SURVEY_PAGE_READ"
    const val SURVEY_DETAIL_READ = "SURVEY_DETAIL_READ"
    const val SURVEY_RESPONSE_CREATE = "SURVEY_RESPONSE_CREATE"
    const val SURVEY_RESPONSE_UPDATE = "SURVEY_RESPONSE_UPDATE"
    const val SURVEY_RESPONSE_DELETE = "SURVEY_RESPONSE_DELETE"
    const val SURVEY_RESULT_READ = "SURVEY_RESULT_READ"

    // 관리자 scope: 조사지 생성, 조회, 수정, 삭제, 상태 변경 권한
    const val ADMIN_SURVEY_PAGE_READ = "ADMIN_SURVEY_PAGE_READ"
    const val ADMIN_SURVEY_DETAIL_READ = "ADMIN_SURVEY_DETAIL_READ"
    const val ADMIN_SURVEY_CREATE = "ADMIN_SURVEY_CREATE"
    const val ADMIN_SURVEY_UPDATE = "ADMIN_SURVEY_UPDATE"
    const val ADMIN_SURVEY_DELETE = "ADMIN_SURVEY_DELETE"
    const val ADMIN_SURVEY_LOCK = "ADMIN_SURVEY_LOCK"
    const val ADMIN_SURVEY_PUBLISH = "ADMIN_SURVEY_PUBLISH"
    const val ADMIN_SURVEY_CLOSE = "ADMIN_SURVEY_CLOSE"
}
