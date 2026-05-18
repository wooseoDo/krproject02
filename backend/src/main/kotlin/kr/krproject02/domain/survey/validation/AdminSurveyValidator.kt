package kr.krproject02.domain.survey.validation

import kr.krproject02.domain.survey.constants.SurveyConstants
import kr.krproject02.domain.survey.constants.SurveyStatus
import kr.krproject02.domain.survey.dto.admin.AdminSurveyCreateRequest
import kr.krproject02.domain.survey.error.SurveyErrorCode
import kr.krproject02.domain.survey.error.SurveyException
import org.springframework.stereotype.Component

@Component
class AdminSurveyValidator {
    fun validateListReadable() {
        // 현재 관리자 리스트 조회는 추가 입력값이 없어 검증할 조건이 없다.
    }

    fun validateCreateRequest(request: AdminSurveyCreateRequest) {
        validateStatus(request.status)

        if (request.maxScore < SurveyConstants.MIN_SURVEY_MAX_SCORE) {
            throw SurveyException(SurveyErrorCode.INVALID_MAX_SCORE)
        }
        if (request.estimatedTimeSec < SurveyConstants.MIN_SURVEY_ESTIMATED_TIME_SEC) {
            throw SurveyException(SurveyErrorCode.INVALID_ESTIMATED_TIME)
        }
        if (request.sections.isEmpty()) {
            throw SurveyException(SurveyErrorCode.SECTION_REQUIRED)
        }

        val questionCount = request.sections.sumOf { section -> section.questions.size }
        if (questionCount < SurveyConstants.MIN_SURVEY_QUESTION_COUNT) {
            throw SurveyException(SurveyErrorCode.MIN_QUESTION_COUNT_REQUIRED)
        }

        val totalQuestionScore = request.sections.sumOf { section ->
            section.questions.sumOf { question -> question.score }
        }
        if (request.maxScore != totalQuestionScore) {
            throw SurveyException(SurveyErrorCode.INVALID_MAX_SCORE_TOTAL)
        }

        request.sections.forEach { section ->
            if (section.title.isBlank()) {
                throw SurveyException(SurveyErrorCode.INVALID_SECTION_TITLE)
            }

            section.questions.forEach { question ->
                if (question.title.isBlank()) {
                    throw SurveyException(SurveyErrorCode.INVALID_QUESTION_TITLE)
                }
                if (question.score < SurveyConstants.MIN_SURVEY_QUESTION_SCORE) {
                    throw SurveyException(SurveyErrorCode.INVALID_QUESTION_SCORE)
                }
                if (question.options.size !in SurveyConstants.MIN_SURVEY_OPTION_COUNT..SurveyConstants.MAX_SURVEY_OPTION_COUNT) {
                    throw SurveyException(SurveyErrorCode.INVALID_OPTION_COUNT)
                }
                if (question.options.any { option -> option.optionScore < SurveyConstants.MIN_SURVEY_OPTION_SCORE }) {
                    throw SurveyException(SurveyErrorCode.INVALID_OPTION_SCORE)
                }
            }
        }
    }

    fun validateStatus(status: SurveyStatus) {
        if (status !in SurveyStatus.entries) {
            throw SurveyException(SurveyErrorCode.INVALID_STATUS)
        }
    }
}
