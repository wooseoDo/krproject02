package kr.krproject02.domain.survey.error

import org.springframework.http.HttpStatus

enum class SurveyErrorCode(
    val httpStatus: HttpStatus,
    val code: String,
    val message: String,
) {
    NOT_FOUND(HttpStatus.NOT_FOUND, "SURVEY_001", "조사지를 찾을 수 없습니다."),
}
