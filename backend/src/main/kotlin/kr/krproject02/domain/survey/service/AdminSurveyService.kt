package kr.krproject02.domain.survey.service

import kr.krproject02.domain.survey.dto.SurveyListPageResponse
import kr.krproject02.domain.survey.dto.SurveyListQuery
import kr.krproject02.domain.survey.dto.admin.AdminSurveyCreateRequest
import kr.krproject02.domain.survey.dto.admin.AdminSurveyCreateResponse
import kr.krproject02.domain.survey.dto.admin.AdminSurveyStatusUpdateRequest
import kr.krproject02.domain.survey.dto.admin.AdminSurveyStatusUpdateResponse
import kr.krproject02.domain.survey.entity.Survey
import kr.krproject02.domain.survey.entity.SurveyQuestion
import kr.krproject02.domain.survey.entity.SurveyQuestionOption
import kr.krproject02.domain.survey.entity.SurveySection
import kr.krproject02.domain.survey.error.SurveyErrorCode
import kr.krproject02.domain.survey.error.SurveyException
import kr.krproject02.domain.survey.mapper.AdminSurveyMapper
import kr.krproject02.domain.survey.mapper.SurveyMapper
import kr.krproject02.domain.survey.repository.SurveyQuestionOptionRepository
import kr.krproject02.domain.survey.repository.SurveyQuestionRepository
import kr.krproject02.domain.survey.repository.SurveyRepository
import kr.krproject02.domain.survey.repository.SurveySectionRepository
import kr.krproject02.domain.survey.validation.AdminSurveyValidator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.UUID

@Service
class AdminSurveyService(
    private val surveyRepository: SurveyRepository,
    private val surveySectionRepository: SurveySectionRepository,
    private val surveyQuestionRepository: SurveyQuestionRepository,
    private val surveyQuestionOptionRepository: SurveyQuestionOptionRepository,
    private val adminSurveyValidator: AdminSurveyValidator,
) {
    @Transactional(readOnly = true)
    fun getSurveyList(query: SurveyListQuery): SurveyListPageResponse {
        adminSurveyValidator.validateListReadable()
        val totalItems = surveyRepository.countSurveyListPage(
            normalOnly = false,
            title = query.normalizedTitle,
            maxScore = query.maxScore,
            estimatedTimeSec = query.estimatedTimeSec,
            surveyVersion = query.surveyVersion,
            status = query.status?.name,
            releasedAtFrom = query.releasedFromDateTime,
            releasedAtTo = query.releasedToDateTime,
        )
        val items = surveyRepository.findSurveyListPage(
            normalOnly = false,
            title = query.normalizedTitle,
            maxScore = query.maxScore,
            estimatedTimeSec = query.estimatedTimeSec,
            surveyVersion = query.surveyVersion,
            status = query.status?.name,
            releasedAtFrom = query.releasedFromDateTime,
            releasedAtTo = query.releasedToDateTime,
            size = query.safeSize,
            offset = query.offset,
        ).map(SurveyMapper::toListItemResponse)

        return SurveyListPageResponse(
            items = items,
            page = query.safePage,
            size = query.safeSize,
            totalItems = totalItems,
            totalPages = ((totalItems + query.safeSize - 1) / query.safeSize).toInt().coerceAtLeast(1),
        )
    }

    @Transactional
    fun createSurvey(request: AdminSurveyCreateRequest): AdminSurveyCreateResponse {
        adminSurveyValidator.validateCreateRequest(request)

        val survey = surveyRepository.save(
            Survey.create(
                title = request.title,
                category = request.category,
                description = request.description,
                status = request.status,
                maxScore = request.maxScore,
                estimatedTimeSec = request.estimatedTimeSec,
                surveySchema = AdminSurveyMapper.toSurveySchema(request),
            ),
        )

        saveSurveyStructure(survey, request)
        return AdminSurveyMapper.toCreateResponse(survey)
    }

    @Transactional
    fun updateSurveyStatus(
        surveyId: UUID,
        request: AdminSurveyStatusUpdateRequest,
    ): AdminSurveyStatusUpdateResponse {
        adminSurveyValidator.validateStatus(request.status)

        val survey = surveyRepository.findBySurveyIdAndDeletedFalse(surveyId)
            ?: throw SurveyException(SurveyErrorCode.NOT_FOUND)

        survey.changeStatus(request.status)
        return AdminSurveyMapper.toStatusUpdateResponse(survey)
    }

    private fun saveSurveyStructure(
        survey: Survey,
        request: AdminSurveyCreateRequest,
    ) {
        var questionSort = 1

        request.sections.forEachIndexed { sectionIndex, sectionRequest ->
            val section = surveySectionRepository.save(
                SurveySection.create(
                    survey = survey,
                    sectionSort = sectionIndex + 1,
                    title = sectionRequest.title,
                    targetAverageScore = sectionRequest.targetAverageScore?.let(BigDecimal::valueOf),
                ),
            )

            sectionRequest.questions.forEach { questionRequest ->
                val question = surveyQuestionRepository.save(
                    SurveyQuestion.create(
                        survey = survey,
                        section = section,
                        questionSort = questionSort++,
                        questionType = questionRequest.questionType,
                        title = questionRequest.title,
                        score = questionRequest.score,
                        optionCount = questionRequest.options.size,
                    ),
                )

                val options = questionRequest.options.mapIndexed { optionIndex, optionRequest ->
                    SurveyQuestionOption.create(
                        question = question,
                        optionSort = optionIndex + 1,
                        optionLabel = optionRequest.optionLabel,
                        optionScore = optionRequest.optionScore,
                    )
                }
                surveyQuestionOptionRepository.saveAll(options)
            }
        }
    }
}
