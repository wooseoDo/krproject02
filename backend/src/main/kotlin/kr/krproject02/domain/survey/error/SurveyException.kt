package kr.krproject02.domain.survey.error

class SurveyException(
    val errorCode: SurveyErrorCode,
) : RuntimeException(errorCode.message)
