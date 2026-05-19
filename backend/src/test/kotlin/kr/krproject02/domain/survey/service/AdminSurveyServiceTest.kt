package kr.krproject02.domain.survey.service

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kr.krproject02.domain.survey.constants.SurveyQuestionType
import kr.krproject02.domain.survey.constants.SurveyStatus
import kr.krproject02.domain.survey.dto.SurveyListQuery
import kr.krproject02.domain.survey.dto.admin.AdminSurveyCreateRequest
import kr.krproject02.domain.survey.dto.admin.AdminSurveyQuestionOptionRequest
import kr.krproject02.domain.survey.dto.admin.AdminSurveyQuestionRequest
import kr.krproject02.domain.survey.dto.admin.AdminSurveySectionRequest
import kr.krproject02.domain.survey.dto.admin.AdminSurveyStatusUpdateRequest
import kr.krproject02.domain.survey.entity.Survey
import kr.krproject02.domain.survey.entity.SurveyQuestion
import kr.krproject02.domain.survey.entity.SurveyQuestionOption
import kr.krproject02.domain.survey.entity.SurveySection
import kr.krproject02.domain.survey.error.SurveyErrorCode
import kr.krproject02.domain.survey.error.SurveyException
import kr.krproject02.domain.survey.repository.SurveyListItemProjection
import kr.krproject02.domain.survey.repository.SurveyQuestionOptionRepository
import kr.krproject02.domain.survey.repository.SurveyQuestionRepository
import kr.krproject02.domain.survey.repository.SurveyRepository
import kr.krproject02.domain.survey.repository.SurveySectionRepository
import kr.krproject02.domain.survey.validation.AdminSurveyValidator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Instant
import java.util.UUID

class AdminSurveyServiceTest {
    private lateinit var surveyRepository: SurveyRepository
    private lateinit var surveySectionRepository: SurveySectionRepository
    private lateinit var surveyQuestionRepository: SurveyQuestionRepository
    private lateinit var surveyQuestionOptionRepository: SurveyQuestionOptionRepository
    private lateinit var adminSurveyValidator: AdminSurveyValidator
    private lateinit var adminSurveyService: AdminSurveyService

    @BeforeEach
    fun setUp() {
        surveyRepository = mockk()
        surveySectionRepository = mockk()
        surveyQuestionRepository = mockk()
        surveyQuestionOptionRepository = mockk()
        adminSurveyValidator = mockk()
        adminSurveyService = AdminSurveyService(
            surveyRepository = surveyRepository,
            surveySectionRepository = surveySectionRepository,
            surveyQuestionRepository = surveyQuestionRepository,
            surveyQuestionOptionRepository = surveyQuestionOptionRepository,
            adminSurveyValidator = adminSurveyValidator,
        )
    }

    @Test
    fun `get survey list for admin succeeds`() {
        val query = surveyListQuery()
        every { adminSurveyValidator.validateListReadable() } just Runs
        every { surveyRepository.countSurveyListPage(false, any(), any(), any(), any(), any(), any(), any()) } returns 1L
        every {
            surveyRepository.findSurveyListPage(false, any(), any(), any(), any(), any(), any(), any(), 10, 0)
        } returns listOf(surveyProjection())

        val response = adminSurveyService.getSurveyList(query)

        assertThat(response.items).hasSize(1)
        assertThat(response.items.first().title).isEqualTo("Work Stress Survey")
        assertThat(response.totalPages).isEqualTo(1)
        verify(exactly = 1) { adminSurveyValidator.validateListReadable() }
        verify(exactly = 1) { surveyRepository.countSurveyListPage(false, any(), any(), any(), any(), any(), any(), any()) }
        verify(exactly = 1) {
            surveyRepository.findSurveyListPage(false, any(), any(), any(), any(), any(), any(), any(), 10, 0)
        }
    }

    @Test
    fun `create survey for admin succeeds`() {
        val request = createRequest()
        every { adminSurveyValidator.validateCreateRequest(request) } just Runs
        every { surveyRepository.save(any<Survey>()) } answers { firstArg() }
        every { surveySectionRepository.save(any<SurveySection>()) } answers { firstArg() }
        every { surveyQuestionRepository.save(any<SurveyQuestion>()) } answers { firstArg() }
        every { surveyQuestionOptionRepository.saveAll(any<Iterable<SurveyQuestionOption>>()) } answers {
            firstArg<Iterable<SurveyQuestionOption>>().toMutableList()
        }

        val response = adminSurveyService.createSurvey(request)

        assertThat(response.title).isEqualTo(request.title)
        assertThat(response.status).isEqualTo(SurveyStatus.PUBLISHED)
        verify(exactly = 1) { adminSurveyValidator.validateCreateRequest(request) }
        verify(exactly = 1) {
            surveyRepository.save(
                match {
                    it.surveyVersion == 1 &&
                        it.previousSurveyId == null &&
                        it.latest
                },
            )
        }
        verify(exactly = 1) { surveySectionRepository.save(any<SurveySection>()) }
        verify(exactly = 5) { surveyQuestionRepository.save(any<SurveyQuestion>()) }
        verify(exactly = 5) { surveyQuestionOptionRepository.saveAll(any<Iterable<SurveyQuestionOption>>()) }
    }

    @Test
    fun `update survey creates next version and marks previous survey not latest`() {
        val surveyId = UUID.randomUUID()
        val groupId = UUID.randomUUID()
        val request = createRequest()
        val previousSurvey = survey().apply {
            this.surveyId = surveyId
            this.surveyGroupId = groupId
            this.surveyVersion = 1
        }

        every { adminSurveyValidator.validateCreateRequest(request) } just Runs
        every { surveyRepository.findBySurveyIdAndLatestTrueAndDeletedFalse(surveyId) } returns previousSurvey
        every { surveyRepository.flush() } just Runs
        every { surveyRepository.save(any<Survey>()) } answers { firstArg() }
        every { surveySectionRepository.save(any<SurveySection>()) } answers { firstArg() }
        every { surveyQuestionRepository.save(any<SurveyQuestion>()) } answers { firstArg() }
        every { surveyQuestionOptionRepository.saveAll(any<Iterable<SurveyQuestionOption>>()) } answers {
            firstArg<Iterable<SurveyQuestionOption>>().toMutableList()
        }

        val response = adminSurveyService.updateSurvey(surveyId, request)

        assertThat(previousSurvey.latest).isFalse()
        assertThat(response.surveyVersion).isEqualTo(2)
        verify(exactly = 1) { adminSurveyValidator.validateCreateRequest(request) }
        verify(exactly = 1) { surveyRepository.findBySurveyIdAndLatestTrueAndDeletedFalse(surveyId) }
        verify(exactly = 1) { surveyRepository.flush() }
        verify(exactly = 1) {
            surveyRepository.save(
                match {
                    it.surveyGroupId == groupId &&
                        it.previousSurveyId == surveyId &&
                        it.surveyVersion == 2 &&
                        it.latest
                },
            )
        }
        verify(exactly = 1) { surveySectionRepository.save(any<SurveySection>()) }
        verify(exactly = 5) { surveyQuestionRepository.save(any<SurveyQuestion>()) }
        verify(exactly = 5) { surveyQuestionOptionRepository.saveAll(any<Iterable<SurveyQuestionOption>>()) }
    }

    @Test
    fun `update survey throws when latest survey does not exist`() {
        val surveyId = UUID.randomUUID()
        val request = createRequest()
        every { adminSurveyValidator.validateCreateRequest(request) } just Runs
        every { surveyRepository.findBySurveyIdAndLatestTrueAndDeletedFalse(surveyId) } returns null

        val exception = assertThrows<SurveyException> {
            adminSurveyService.updateSurvey(surveyId, request)
        }

        assertThat(exception.errorCode).isEqualTo(SurveyErrorCode.NOT_FOUND)
        verify(exactly = 1) { adminSurveyValidator.validateCreateRequest(request) }
        verify(exactly = 1) { surveyRepository.findBySurveyIdAndLatestTrueAndDeletedFalse(surveyId) }
        verify(exactly = 0) { surveyRepository.flush() }
        verify(exactly = 0) { surveyRepository.save(any<Survey>()) }
    }

    @Test
    fun `create survey does not save when validation fails`() {
        val request = createRequest(maxScore = 29)
        every { adminSurveyValidator.validateCreateRequest(request) } throws
            SurveyException(SurveyErrorCode.INVALID_MAX_SCORE_TOTAL)

        val exception = assertThrows<SurveyException> {
            adminSurveyService.createSurvey(request)
        }

        assertThat(exception.errorCode).isEqualTo(SurveyErrorCode.INVALID_MAX_SCORE_TOTAL)
        verify(exactly = 1) { adminSurveyValidator.validateCreateRequest(request) }
        verify(exactly = 0) { surveyRepository.save(any<Survey>()) }
        verify(exactly = 0) { surveySectionRepository.save(any<SurveySection>()) }
        verify(exactly = 0) { surveyQuestionRepository.save(any<SurveyQuestion>()) }
        verify(exactly = 0) { surveyQuestionOptionRepository.saveAll(any<Iterable<SurveyQuestionOption>>()) }
    }

    @Test
    fun `update survey status succeeds`() {
        val surveyId = UUID.randomUUID()
        val survey = survey(status = SurveyStatus.DRAFT)
        val request = AdminSurveyStatusUpdateRequest(status = SurveyStatus.LOCKED)
        every { adminSurveyValidator.validateStatus(SurveyStatus.LOCKED) } just Runs
        every { surveyRepository.findBySurveyIdAndDeletedFalse(surveyId) } returns survey

        val response = adminSurveyService.updateSurveyStatus(surveyId, request)

        assertThat(response.status).isEqualTo(SurveyStatus.LOCKED)
        assertThat(survey.status).isEqualTo(SurveyStatus.LOCKED)
        assertThat(survey.lockedAt).isNotNull()
        verify(exactly = 1) { adminSurveyValidator.validateStatus(SurveyStatus.LOCKED) }
        verify(exactly = 1) { surveyRepository.findBySurveyIdAndDeletedFalse(surveyId) }
    }

    @Test
    fun `update survey status throws when survey does not exist`() {
        val surveyId = UUID.randomUUID()
        val request = AdminSurveyStatusUpdateRequest(status = SurveyStatus.CLOSED)
        every { adminSurveyValidator.validateStatus(SurveyStatus.CLOSED) } just Runs
        every { surveyRepository.findBySurveyIdAndDeletedFalse(surveyId) } returns null

        val exception = assertThrows<SurveyException> {
            adminSurveyService.updateSurveyStatus(surveyId, request)
        }

        assertThat(exception.errorCode).isEqualTo(SurveyErrorCode.NOT_FOUND)
        verify(exactly = 1) { adminSurveyValidator.validateStatus(SurveyStatus.CLOSED) }
        verify(exactly = 1) { surveyRepository.findBySurveyIdAndDeletedFalse(surveyId) }
    }

    private fun surveyListQuery(): SurveyListQuery =
        SurveyListQuery(
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

    private fun surveyProjection(status: SurveyStatus = SurveyStatus.PUBLISHED): SurveyListItemProjection =
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

    private fun survey(status: SurveyStatus = SurveyStatus.PUBLISHED): Survey =
        Survey.create(
            title = "Work Stress Survey",
            category = "health/work",
            description = "Survey for checking work stress level.",
            status = status,
            maxScore = 30,
            estimatedTimeSec = 600,
            surveySchema = "{}",
        )

    private fun createRequest(maxScore: Int = 30): AdminSurveyCreateRequest =
        AdminSurveyCreateRequest(
            title = "Work Stress Survey",
            category = "health/work",
            description = "Survey for checking work stress level.",
            status = SurveyStatus.PUBLISHED,
            maxScore = maxScore,
            estimatedTimeSec = 600,
            sections = listOf(
                AdminSurveySectionRequest(
                    title = "Workload",
                    targetAverageScore = 7.0,
                    questions = List(5) {
                        AdminSurveyQuestionRequest(
                            questionType = SurveyQuestionType.LIKERT,
                            title = "I have felt overloaded by work recently.",
                            score = 6,
                            options = listOf(
                                AdminSurveyQuestionOptionRequest("Strongly disagree", 0),
                                AdminSurveyQuestionOptionRequest("Strongly agree", 5),
                            ),
                        )
                    },
                ),
            ),
        )
}
