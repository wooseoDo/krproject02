package kr.krproject02.domain.survey.service

import kr.krproject02.domain.survey.dto.SurveyListItemResponse
import kr.krproject02.domain.survey.mapper.SurveyMapper
import kr.krproject02.domain.survey.repository.SurveyRepository
import kr.krproject02.domain.survey.validation.AdminSurveyValidator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminSurveyService(
    private val surveyRepository: SurveyRepository,
    private val adminSurveyValidator: AdminSurveyValidator,
) {
    @Transactional(readOnly = true)
    fun getSurveyList(): List<SurveyListItemResponse> {
        adminSurveyValidator.validateListReadable()
        return surveyRepository.findAllByDeletedFalseOrderByCreatedAtDesc()
            .map(SurveyMapper::toListItemResponse)
    }
}
