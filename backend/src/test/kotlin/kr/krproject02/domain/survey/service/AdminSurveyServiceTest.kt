package kr.krproject02.domain.survey.service

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kr.krproject02.domain.survey.constants.SurveyQuestionType
import kr.krproject02.domain.survey.constants.SurveyStatus
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
import kr.krproject02.domain.survey.repository.SurveyQuestionOptionRepository
import kr.krproject02.domain.survey.repository.SurveyQuestionRepository
import kr.krproject02.domain.survey.repository.SurveyRepository
import kr.krproject02.domain.survey.repository.SurveySectionRepository
import kr.krproject02.domain.survey.validation.AdminSurveyValidator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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
        val survey = survey()
        every { adminSurveyValidator.validateListReadable() } just Runs
        every { surveyRepository.findAllByDeletedFalseOrderByCreatedAtDesc() } returns listOf(survey)

        val responses = adminSurveyService.getSurveyList()

        assertThat(responses).hasSize(1)
        assertThat(responses.first().title).isEqualTo("직무 스트레스 자가진단 조사지")
        verify(exactly = 1) { adminSurveyValidator.validateListReadable() }
        verify(exactly = 1) { surveyRepository.findAllByDeletedFalseOrderByCreatedAtDesc() }
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
        verify(exactly = 1) { surveyRepository.save(any<Survey>()) }
        verify(exactly = 1) { surveySectionRepository.save(any<SurveySection>()) }
        verify(exactly = 5) { surveyQuestionRepository.save(any<SurveyQuestion>()) }
        verify(exactly = 5) { surveyQuestionOptionRepository.saveAll(any<Iterable<SurveyQuestionOption>>()) }
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

    private fun survey(status: SurveyStatus = SurveyStatus.PUBLISHED): Survey =
        Survey.create(
            title = "직무 스트레스 자가진단 조사지",
            category = "심리/직무",
            description = "업무 환경에서 느끼는 스트레스 정도를 확인하는 조사지",
            status = status,
            maxScore = 30,
            estimatedTimeSec = 600,
            surveySchema = "{}",
        )

    private fun createRequest(
        maxScore: Int = 30,
    ): AdminSurveyCreateRequest =
        AdminSurveyCreateRequest(
            title = "직무 스트레스 자가진단 조사지",
            category = "심리/직무",
            description = "업무 환경에서 느끼는 스트레스 정도를 확인하는 조사지",
            status = SurveyStatus.PUBLISHED,
            maxScore = maxScore,
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
