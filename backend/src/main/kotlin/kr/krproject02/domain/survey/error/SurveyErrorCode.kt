package kr.krproject02.domain.survey.error

import org.springframework.http.HttpStatus

enum class SurveyErrorCode(
    val httpStatus: HttpStatus,
    val code: String,
    val message: String,
) {
    NOT_FOUND(HttpStatus.NOT_FOUND, "SURVEY_001", "조사지를 찾을 수 없습니다."),
    SECTION_REQUIRED(HttpStatus.BAD_REQUEST, "SURVEY_002", "조사지 항목은 최소 1개 이상 필요합니다."),
    MIN_QUESTION_COUNT_REQUIRED(HttpStatus.BAD_REQUEST, "SURVEY_003", "조사지 문항은 최소 5개 이상 필요합니다."),
    INVALID_SECTION_TITLE(HttpStatus.BAD_REQUEST, "SURVEY_004", "조사지 항목 제목을 입력해야 합니다."),
    INVALID_QUESTION_TITLE(HttpStatus.BAD_REQUEST, "SURVEY_005", "조사지 문항 제목을 입력해야 합니다."),
    INVALID_OPTION_COUNT(HttpStatus.BAD_REQUEST, "SURVEY_006", "문항 선택지는 2개 이상 5개 이하로 입력해야 합니다."),
    INVALID_MAX_SCORE_TOTAL(HttpStatus.BAD_REQUEST, "SURVEY_007", "조사지 최대 점수는 문항 배점 총합과 같아야 합니다."),
    INVALID_OPTION_SCORE(HttpStatus.BAD_REQUEST, "SURVEY_008", "선택지 점수는 0점 이상이어야 합니다."),
    INVALID_MAX_SCORE(HttpStatus.BAD_REQUEST, "SURVEY_009", "조사지 최대 점수는 1점 이상이어야 합니다."),
    INVALID_QUESTION_SCORE(HttpStatus.BAD_REQUEST, "SURVEY_010", "문항 배점은 1점 이상이어야 합니다."),
    INVALID_STATUS(HttpStatus.BAD_REQUEST, "SURVEY_011", "조사지 상태값은 DRAFT, PUBLISHED, LOCKED, CLOSED 중 하나여야 합니다."),
    INVALID_ESTIMATED_TIME(HttpStatus.BAD_REQUEST, "SURVEY_012", "예상 소요 시간은 1초 이상이어야 합니다."),
}
