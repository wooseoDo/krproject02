package kr.krproject02.domain.survey.repository

import kr.krproject02.domain.survey.entity.SurveySection
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SurveySectionRepository : JpaRepository<SurveySection, UUID> {
    fun findBySurveySurveyIdOrderBySectionSortAsc(surveyId: UUID): List<SurveySection>
}
