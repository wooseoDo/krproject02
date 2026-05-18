package kr.krproject02.domain.survey.validation

import kr.krproject02.domain.survey.constants.SurveyQuestionType
import kr.krproject02.domain.survey.constants.SurveyStatus
import kr.krproject02.domain.survey.dto.admin.AdminSurveyCreateRequest
import kr.krproject02.domain.survey.dto.admin.AdminSurveyQuestionOptionRequest
import kr.krproject02.domain.survey.dto.admin.AdminSurveyQuestionRequest
import kr.krproject02.domain.survey.dto.admin.AdminSurveySectionRequest
import kr.krproject02.domain.survey.error.SurveyErrorCode
import kr.krproject02.domain.survey.error.SurveyException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class AdminSurveyValidatorTest {
    private val validator = AdminSurveyValidator()

    @Test
    fun `valid admin survey create request passes validation`() {
        assertDoesNotThrow {
            validator.validateCreateRequest(validRequest())
        }
    }

    @Test
    fun `defined survey status enum values pass validation`() {
        SurveyStatus.entries.forEach { status ->
            assertDoesNotThrow {
                validator.validateStatus(status)
            }
        }
    }

    @Test
    fun `unknown survey status string fails before request binding`() {
        assertThrows<IllegalArgumentException> {
            SurveyStatus.valueOf("UNKNOWN")
        }
    }

    @Test
    fun `estimated time less than one second fails`() {
        val exception = assertThrows<SurveyException> {
            validator.validateCreateRequest(validRequest(estimatedTimeSec = 0))
        }

        assertThat(exception.errorCode).isEqualTo(SurveyErrorCode.INVALID_ESTIMATED_TIME)
    }

    @Test
    fun `question score total different from max score fails`() {
        val exception = assertThrows<SurveyException> {
            validator.validateCreateRequest(validRequest(maxScore = 31))
        }

        assertThat(exception.errorCode).isEqualTo(SurveyErrorCode.INVALID_MAX_SCORE_TOTAL)
    }

    @Test
    fun `survey must have at least five questions`() {
        val exception = assertThrows<SurveyException> {
            validator.validateCreateRequest(
                validRequest(
                    sections = listOf(
                        section(
                            questions = listOf(
                                question(score = 10),
                                question(score = 10),
                            ),
                        ),
                    ),
                    maxScore = 20,
                ),
            )
        }

        assertThat(exception.errorCode).isEqualTo(SurveyErrorCode.MIN_QUESTION_COUNT_REQUIRED)
    }

    @Test
    fun `options must be between two and five`() {
        val exception = assertThrows<SurveyException> {
            validator.validateCreateRequest(
                validRequest(
                    sections = listOf(
                        section(
                            questions = listOf(
                                question(score = 6, options = listOf(option("하나"))),
                                question(score = 6),
                                question(score = 6),
                                question(score = 6),
                                question(score = 6),
                            ),
                        ),
                    ),
                ),
            )
        }

        assertThat(exception.errorCode).isEqualTo(SurveyErrorCode.INVALID_OPTION_COUNT)
    }

    private fun validRequest(
        status: SurveyStatus = SurveyStatus.DRAFT,
        maxScore: Int = 30,
        estimatedTimeSec: Int = 600,
        sections: List<AdminSurveySectionRequest> = listOf(
            section(
                questions = listOf(
                    question(score = 6),
                    question(score = 6),
                    question(score = 6),
                    question(score = 6),
                    question(score = 6),
                ),
            ),
        ),
    ): AdminSurveyCreateRequest =
        AdminSurveyCreateRequest(
            title = "직무 스트레스 자가진단 조사지",
            category = "심리/직무",
            description = "업무 환경에서 느끼는 스트레스 정도를 확인하는 조사지",
            status = status,
            maxScore = maxScore,
            estimatedTimeSec = estimatedTimeSec,
            sections = sections,
        )

    private fun section(
        questions: List<AdminSurveyQuestionRequest>,
    ): AdminSurveySectionRequest =
        AdminSurveySectionRequest(
            title = "업무 부담",
            targetAverageScore = 7.0,
            questions = questions,
        )

    private fun question(
        score: Int = 6,
        options: List<AdminSurveyQuestionOptionRequest> = listOf(
            option("전혀 아니다", 0),
            option("매우 그렇다", 5),
        ),
    ): AdminSurveyQuestionRequest =
        AdminSurveyQuestionRequest(
            questionType = SurveyQuestionType.LIKERT,
            title = "최근 2주 동안 업무량이 감당하기 어렵다고 느낀 적이 있다.",
            score = score,
            options = options,
        )

    private fun option(
        optionLabel: String,
        optionScore: Int = 0,
    ): AdminSurveyQuestionOptionRequest =
        AdminSurveyQuestionOptionRequest(
            optionLabel = optionLabel,
            optionScore = optionScore,
        )
}
