package kr.krproject02.domain.survey.repository

import kr.krproject02.domain.survey.entity.SurveyQuestionOption
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SurveyQuestionOptionRepository : JpaRepository<SurveyQuestionOption, UUID> {
    fun findByQuestionQuestionIdInOrderByOptionSortAsc(questionIds: Collection<UUID>): List<SurveyQuestionOption>
}
