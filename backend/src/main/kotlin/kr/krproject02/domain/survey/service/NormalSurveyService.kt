package kr.krproject02.domain.survey.service

import kr.krproject02.domain.survey.dto.SurveyListPageResponse
import kr.krproject02.domain.survey.dto.SurveyListQuery
import kr.krproject02.domain.survey.dto.normal.NormalSurveyParticipationDetailResponse
import kr.krproject02.domain.survey.dto.normal.NormalSurveyParticipationOptionResponse
import kr.krproject02.domain.survey.dto.normal.NormalSurveyParticipationQuestionResponse
import kr.krproject02.domain.survey.dto.normal.NormalSurveyParticipationSectionResponse
import kr.krproject02.domain.survey.dto.normal.NormalSurveyParticipationStartResponse
import kr.krproject02.domain.survey.dto.normal.NormalSurveySubmitRequest
import kr.krproject02.domain.survey.dto.normal.NormalSurveySubmitResponse
import kr.krproject02.domain.survey.entity.SurveyResponse
import kr.krproject02.domain.survey.entity.SurveyResponseAnswer
import kr.krproject02.domain.survey.error.SurveyErrorCode
import kr.krproject02.domain.survey.error.SurveyException
import kr.krproject02.domain.survey.mapper.SurveyMapper
import kr.krproject02.domain.survey.repository.SurveyQuestionOptionRepository
import kr.krproject02.domain.survey.repository.SurveyQuestionRepository
import kr.krproject02.domain.survey.repository.SurveyRepository
import kr.krproject02.domain.survey.repository.SurveyResponseAnswerRepository
import kr.krproject02.domain.survey.repository.SurveyResponseRepository
import kr.krproject02.domain.survey.repository.SurveySectionRepository
import kr.krproject02.domain.survey.validation.NormalSurveyValidator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.UUID

@Service
class NormalSurveyService(
    private val surveyRepository: SurveyRepository,
    private val surveyResponseRepository: SurveyResponseRepository,
    private val surveyResponseAnswerRepository: SurveyResponseAnswerRepository,
    private val surveySectionRepository: SurveySectionRepository,
    private val surveyQuestionRepository: SurveyQuestionRepository,
    private val surveyQuestionOptionRepository: SurveyQuestionOptionRepository,
    private val normalSurveyValidator: NormalSurveyValidator,
) {
    @Transactional(readOnly = true)
    fun getSurveyList(query: SurveyListQuery): SurveyListPageResponse {
        normalSurveyValidator.validateListReadable()
        val totalItems = surveyRepository.countSurveyListPage(
            normalOnly = true,
            title = query.normalizedTitle,
            maxScore = query.maxScore,
            estimatedTimeSec = query.estimatedTimeSec,
            surveyVersion = query.surveyVersion,
            status = query.status?.name,
            releasedAtFrom = query.releasedFromDateTime,
            releasedAtTo = query.releasedToDateTime,
        )
        val items = surveyRepository.findSurveyListPage(
            normalOnly = true,
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

    @Transactional(readOnly = true)
    fun getParticipationDetail(surveyId: UUID): NormalSurveyParticipationDetailResponse {
        val survey = surveyRepository.findBySurveyIdAndLatestTrueAndDeletedFalse(surveyId)
            ?: throw SurveyException(SurveyErrorCode.NOT_FOUND)

        if (!normalSurveyValidator.canParticipate(survey.status)) {
            throw SurveyException(SurveyErrorCode.NOT_PARTICIPABLE)
        }

        val savedSurveyId = survey.surveyId ?: throw SurveyException(SurveyErrorCode.NOT_FOUND)
        val sections = surveySectionRepository.findBySurveySurveyIdOrderBySectionSortAsc(savedSurveyId)
        val questions = surveyQuestionRepository.findBySurveySurveyIdOrderByQuestionSortAsc(savedSurveyId)
        val questionIds = questions.mapNotNull { question -> question.questionId }
        val options = if (questionIds.isEmpty()) {
            emptyList()
        } else {
            surveyQuestionOptionRepository.findByQuestionQuestionIdInOrderByOptionSortAsc(questionIds)
        }
        val optionsByQuestionId = options.groupBy { option -> option.question.questionId }
        val questionsBySectionId = questions.groupBy { question -> question.section.sectionId }

        return NormalSurveyParticipationDetailResponse(
            surveyId = survey.surveyId,
            surveyGroupId = survey.surveyGroupId,
            surveyVersion = survey.surveyVersion,
            title = survey.title,
            category = survey.category,
            description = survey.description,
            status = survey.status,
            maxScore = survey.maxScore,
            estimatedTimeSec = survey.estimatedTimeSec,
            sections = sections.map { section ->
                NormalSurveyParticipationSectionResponse(
                    sectionId = section.sectionId,
                    sectionSort = section.sectionSort,
                    title = section.title,
                    targetAverageScore = section.targetAverageScore,
                    questions = questionsBySectionId[section.sectionId].orEmpty().map { question ->
                        NormalSurveyParticipationQuestionResponse(
                            questionId = question.questionId,
                            questionSort = question.questionSort,
                            questionType = question.questionType,
                            title = question.title,
                            options = optionsByQuestionId[question.questionId].orEmpty().map { option ->
                                NormalSurveyParticipationOptionResponse(
                                    optionId = option.optionId,
                                    optionSort = option.optionSort,
                                    optionLabel = option.optionLabel,
                                )
                            },
                        )
                    },
                )
            },
        )
    }

    @Transactional
    fun startParticipation(
        surveyId: UUID,
        userId: UUID?,
    ): NormalSurveyParticipationStartResponse {
        val participantId = userId ?: throw SurveyException(SurveyErrorCode.USER_REQUIRED)
        val survey = surveyRepository.findBySurveyIdAndLatestTrueAndDeletedFalse(surveyId)
            ?: throw SurveyException(SurveyErrorCode.NOT_FOUND)

        if (!normalSurveyValidator.canParticipate(survey.status)) {
            throw SurveyException(SurveyErrorCode.NOT_PARTICIPABLE)
        }

        val surveyGroupId = survey.surveyGroupId ?: survey.surveyId ?: throw SurveyException(SurveyErrorCode.NOT_FOUND)
        val existingResponse = surveyResponseRepository.findByUserIdAndSurveyGroupIdAndSurveyVersionAndDeletedFalse(
            userId = participantId,
            surveyGroupId = surveyGroupId,
            surveyVersion = survey.surveyVersion,
        )

        if (existingResponse != null) {
            return NormalSurveyParticipationStartResponse(
                responseId = existingResponse.responseId,
                surveyId = existingResponse.surveyId,
                surveyGroupId = existingResponse.surveyGroupId,
                surveyVersion = existingResponse.surveyVersion,
                surveyTitle = existingResponse.surveyTitle,
                resumed = true,
                completed = existingResponse.completed,
                submittedAt = existingResponse.submittedAt,
                elapsedTimeSec = existingResponse.elapsedTimeSec,
                totalScore = existingResponse.totalScore,
            )
        }

        val response = surveyResponseRepository.save(
            SurveyResponse.start(
                surveyId = survey.surveyId,
                surveyGroupId = surveyGroupId,
                userId = participantId,
                surveyVersion = survey.surveyVersion,
                surveyTitle = survey.title,
            ),
        )

        return NormalSurveyParticipationStartResponse(
            responseId = response.responseId,
            surveyId = response.surveyId,
            surveyGroupId = response.surveyGroupId,
            surveyVersion = response.surveyVersion,
            surveyTitle = response.surveyTitle,
            resumed = false,
            completed = response.completed,
            submittedAt = response.submittedAt,
            elapsedTimeSec = response.elapsedTimeSec,
            totalScore = response.totalScore,
        )
    }

    @Transactional
    fun submitParticipation(
        responseId: UUID,
        request: NormalSurveySubmitRequest,
    ): NormalSurveySubmitResponse {
        val response = surveyResponseRepository.findByResponseIdAndDeletedFalse(responseId)
            ?: throw SurveyException(SurveyErrorCode.RESPONSE_NOT_FOUND)

        if (response.completed) {
            throw SurveyException(SurveyErrorCode.RESPONSE_ALREADY_SUBMITTED)
        }
        if (surveyResponseAnswerRepository.existsByResponseResponseId(responseId)) {
            throw SurveyException(SurveyErrorCode.RESPONSE_ALREADY_SUBMITTED)
        }

        val savedSurveyId = response.surveyId ?: throw SurveyException(SurveyErrorCode.NOT_FOUND)
        val survey = surveyRepository.findBySurveyIdAndDeletedFalse(savedSurveyId)
            ?: throw SurveyException(SurveyErrorCode.NOT_FOUND)
        val questions = surveyQuestionRepository.findBySurveySurveyIdOrderByQuestionSortAsc(savedSurveyId)
        val questionIds = questions.map { question ->
            question.questionId ?: throw SurveyException(SurveyErrorCode.INVALID_ANSWER)
        }
        val answerByQuestionId = request.answers.associateBy { answer ->
            answer.questionId ?: throw SurveyException(SurveyErrorCode.INVALID_ANSWER)
        }

        if (questionIds.isEmpty() || answerByQuestionId.size != request.answers.size || answerByQuestionId.keys != questionIds.toSet()) {
            throw SurveyException(SurveyErrorCode.INVALID_ANSWER)
        }

        val options = surveyQuestionOptionRepository.findByQuestionQuestionIdInOrderByOptionSortAsc(questionIds)
        val optionById = options.associateBy { option ->
            option.optionId ?: throw SurveyException(SurveyErrorCode.INVALID_ANSWER)
        }
        val answers = questions.map { question ->
            val questionId = question.questionId ?: throw SurveyException(SurveyErrorCode.INVALID_ANSWER)
            val optionId = answerByQuestionId[questionId]?.optionId
                ?: throw SurveyException(SurveyErrorCode.INVALID_ANSWER)
            val option = optionById[optionId] ?: throw SurveyException(SurveyErrorCode.INVALID_ANSWER)

            if (option.question.questionId != questionId) {
                throw SurveyException(SurveyErrorCode.INVALID_ANSWER)
            }

            SurveyResponseAnswer.create(
                response = response,
                survey = survey,
                question = question,
                option = option,
            )
        }
        val totalScore = answers.fold(BigDecimal.ZERO) { sum, answer -> sum + answer.score }

        surveyResponseAnswerRepository.saveAll(answers)
        response.complete(
            elapsedTimeSec = request.elapsedTimeSec,
            totalScore = totalScore,
        )

        return NormalSurveySubmitResponse(
            responseId = response.responseId,
            surveyId = response.surveyId,
            surveyGroupId = response.surveyGroupId,
            surveyVersion = response.surveyVersion,
            completed = response.completed,
            submittedAt = response.submittedAt,
            elapsedTimeSec = response.elapsedTimeSec,
            totalScore = response.totalScore,
        )
    }
}
