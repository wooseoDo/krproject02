package kr.krproject02.domain.survey.repository

import kr.krproject02.domain.survey.entity.SurveyQuestion
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SurveyQuestionRepository : JpaRepository<SurveyQuestion, UUID>
