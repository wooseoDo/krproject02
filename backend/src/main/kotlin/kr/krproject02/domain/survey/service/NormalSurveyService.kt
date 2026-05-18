package kr.krproject02.domain.survey.service

import kr.krproject02.domain.survey.dto.SurveyListItemResponse
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
    fun getSurveyList(): List<SurveyListItemResponse> {
        normalSurveyValidator.validateListReadable()
        return surveyRepository.findAllByDeletedFalseOrderByCreatedAtDesc()
            .map(SurveyMapper::toListItemResponse)
    }
}
