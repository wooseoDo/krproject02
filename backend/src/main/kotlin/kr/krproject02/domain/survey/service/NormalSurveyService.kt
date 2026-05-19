package kr.krproject02.domain.survey.service

import kr.krproject02.domain.survey.dto.SurveyListPageResponse
import kr.krproject02.domain.survey.dto.SurveyListQuery
import kr.krproject02.domain.survey.mapper.SurveyMapper
import kr.krproject02.domain.survey.repository.SurveyRepository
import kr.krproject02.domain.survey.validation.NormalSurveyValidator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NormalSurveyService(
    private val surveyRepository: SurveyRepository,
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
}
