package kr.krproject02.domain.survey.mapper

import kr.krproject02.domain.survey.constants.SurveyQuestionType
import kr.krproject02.domain.survey.constants.SurveyStatus
import kr.krproject02.domain.survey.dto.admin.AdminSurveyCreateRequest
import kr.krproject02.domain.survey.dto.admin.AdminSurveyQuestionOptionRequest
import kr.krproject02.domain.survey.dto.admin.AdminSurveyQuestionRequest
import kr.krproject02.domain.survey.dto.admin.AdminSurveySectionRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AdminSurveyMapperTest {
    @Test
    fun `create request is converted to jsonb schema with status and estimated time`() {
        val schema = AdminSurveyMapper.toSurveySchema(createRequest())

        assertThat(schema).contains("\"status\": \"PUBLISHED\"")
        assertThat(schema).contains("\"maxScore\": 30")
        assertThat(schema).contains("\"estimatedTimeSec\": 600")
        assertThat(schema).contains("\"type\": \"LIKERT\"")
        assertThat(schema).contains("\"sections\"")
    }

    private fun createRequest(): AdminSurveyCreateRequest =
        AdminSurveyCreateRequest(
            title = "직무 스트레스 자가진단 조사지",
            category = "심리/직무",
            description = "업무 환경에서 느끼는 스트레스 정도를 확인하는 조사지",
            status = SurveyStatus.PUBLISHED,
            maxScore = 30,
            estimatedTimeSec = 600,
            sections = listOf(
                AdminSurveySectionRequest(
                    title = "업무 부담",
                    targetAverageScore = 7.0,
                    questions = List(5) {
                        AdminSurveyQuestionRequest(
                            questionType = SurveyQuestionType.LIKERT,
                            title = "최근 2주 동안 업무량이 감당하기 어렵다고 느낀 적이 있다.",
                            score = 6,
                            options = listOf(
                                AdminSurveyQuestionOptionRequest("전혀 아니다", 0),
                                AdminSurveyQuestionOptionRequest("매우 그렇다", 5),
                            ),
                        )
                    },
                ),
            ),
        )
}
