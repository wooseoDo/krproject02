package kr.krproject02.domain.survey.repository

import kr.krproject02.domain.survey.entity.SurveyResponseAnswer
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SurveyResponseAnswerRepository : JpaRepository<SurveyResponseAnswer, UUID> {
    fun existsByResponseResponseId(responseId: UUID): Boolean
}
