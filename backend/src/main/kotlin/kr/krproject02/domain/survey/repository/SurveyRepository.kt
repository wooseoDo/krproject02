package kr.krproject02.domain.survey.repository

import kr.krproject02.domain.survey.entity.Survey
import kr.krproject02.domain.survey.constants.SurveyStatus
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SurveyRepository : JpaRepository<Survey, UUID> {
    fun findAllByDeletedFalseOrderByCreatedAtDesc(): List<Survey>

    fun findAllByStatusNotInAndDeletedFalseOrderByCreatedAtDesc(statuses: Collection<SurveyStatus>): List<Survey>

    fun findBySurveyIdAndDeletedFalse(surveyId: UUID): Survey?
}
