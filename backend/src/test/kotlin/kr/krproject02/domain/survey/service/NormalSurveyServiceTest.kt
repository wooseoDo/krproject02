package kr.krproject02.domain.survey.service

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kr.krproject02.domain.survey.constants.SurveyStatus
import kr.krproject02.domain.survey.dto.SurveyListQuery
import kr.krproject02.domain.survey.repository.SurveyListItemProjection
import kr.krproject02.domain.survey.repository.SurveyRepository
import kr.krproject02.domain.survey.validation.NormalSurveyValidator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.UUID

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
        val query = SurveyListQuery(
            page = 1,
            size = 10,
            title = null,
            maxScore = null,
            estimatedTimeSec = null,
            surveyVersion = null,
            status = null,
            releasedAtFrom = null,
            releasedAtTo = null,
        )
        every { normalSurveyValidator.validateListReadable() } just Runs
        every { surveyRepository.countSurveyListPage(true, any(), any(), any(), any(), any(), any(), any()) } returns 1L
        every {
            surveyRepository.findSurveyListPage(true, any(), any(), any(), any(), any(), any(), any(), 10, 0)
        } returns listOf(surveyProjection(SurveyStatus.PUBLISHED))

        val response = normalSurveyService.getSurveyList(query)

        assertThat(response.items).hasSize(1)
        assertThat(response.items.first().status).isEqualTo(SurveyStatus.PUBLISHED)
        assertThat(response.totalItems).isEqualTo(1)
        verify(exactly = 1) { normalSurveyValidator.validateListReadable() }
        verify(exactly = 1) { surveyRepository.countSurveyListPage(true, any(), any(), any(), any(), any(), any(), any()) }
        verify(exactly = 1) {
            surveyRepository.findSurveyListPage(true, any(), any(), any(), any(), any(), any(), any(), 10, 0)
        }
    }

    private fun surveyProjection(status: SurveyStatus): SurveyListItemProjection =
        object : SurveyListItemProjection {
            override val surveyId: UUID = UUID.randomUUID()
            override val title: String = "Work Stress Survey"
            override val surveyVersion: Int = 1
            override val status: String = status.name
            override val maxScore: Int = 30
            override val category: String = "health/work"
            override val estimatedTimeSec: Int = 600
            override val createdAt: Instant = Instant.now()
        }
}
