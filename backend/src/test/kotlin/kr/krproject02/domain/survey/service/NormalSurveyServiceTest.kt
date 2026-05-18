package kr.krproject02.domain.survey.service

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kr.krproject02.domain.survey.constants.SurveyStatus
import kr.krproject02.domain.survey.entity.Survey
import kr.krproject02.domain.survey.repository.SurveyRepository
import kr.krproject02.domain.survey.validation.NormalSurveyValidator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class NormalSurveyServiceTest {
    private lateinit var surveyRepository: SurveyRepository
    private lateinit var normalSurveyValidator: NormalSurveyValidator
    private lateinit var normalSurveyService: NormalSurveyService

    @BeforeEach
    fun setUp() {
        surveyRepository = mockk()
        normalSurveyValidator = mockk()
        normalSurveyService = NormalSurveyService(
            surveyRepository = surveyRepository,
            normalSurveyValidator = normalSurveyValidator,
        )
    }

    @Test
    fun `normal user list excludes locked and closed surveys`() {
        val publishedSurvey = survey(SurveyStatus.PUBLISHED)
        every { normalSurveyValidator.validateListReadable() } just Runs
        every {
            surveyRepository.findAllByStatusNotInAndDeletedFalseOrderByCreatedAtDesc(
                listOf(SurveyStatus.LOCKED, SurveyStatus.CLOSED),
            )
        } returns listOf(publishedSurvey)

        val responses = normalSurveyService.getSurveyList()

        assertThat(responses).hasSize(1)
        assertThat(responses.first().status).isEqualTo(SurveyStatus.PUBLISHED)
        verify(exactly = 1) { normalSurveyValidator.validateListReadable() }
        verify(exactly = 1) {
            surveyRepository.findAllByStatusNotInAndDeletedFalseOrderByCreatedAtDesc(
                listOf(SurveyStatus.LOCKED, SurveyStatus.CLOSED),
            )
        }
    }

    private fun survey(status: SurveyStatus): Survey =
        Survey.create(
            title = "직무 스트레스 자가진단 조사지",
            category = "심리/직무",
            description = "업무 환경에서 느끼는 스트레스 정도를 확인하는 조사지",
            status = status,
            maxScore = 30,
            estimatedTimeSec = 600,
            surveySchema = "{}",
        )
}
