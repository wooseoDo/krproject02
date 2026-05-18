package kr.krproject02.domain.survey.mapper

import kr.krproject02.domain.survey.dto.SurveyListItemResponse
import kr.krproject02.domain.survey.entity.Survey

object SurveyMapper {
    fun toListItemResponse(survey: Survey): SurveyListItemResponse =
        SurveyListItemResponse(
            surveyId = survey.surveyId,
            title = survey.title,
            surveyVersion = survey.surveyVersion,
            status = survey.status,
            maxScore = survey.maxScore,
            category = survey.category,
            estimatedTimeSec = survey.estimatedTimeSec,
            createdAt = survey.createdAt,
        )
}
