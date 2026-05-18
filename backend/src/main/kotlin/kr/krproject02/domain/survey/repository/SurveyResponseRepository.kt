package kr.krproject02.domain.survey.repository

import kr.krproject02.domain.survey.entity.SurveyResponse
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SurveyResponseRepository : JpaRepository<SurveyResponse, UUID> {
    fun findFirstBySurveyIdAndUserIdAndCompletedTrueAndDeletedFalseOrderBySubmittedAtDesc(
        surveyId: UUID,
        userId: UUID,
    ): SurveyResponse?
}
