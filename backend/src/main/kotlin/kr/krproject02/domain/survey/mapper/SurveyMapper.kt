package kr.krproject02.domain.survey.mapper

import kr.krproject02.common.core.utils.DateTimeUtils
import kr.krproject02.domain.survey.dto.SurveyListItemResponse
import kr.krproject02.domain.survey.entity.Survey
import kr.krproject02.domain.survey.constants.SurveyStatus
import kr.krproject02.domain.survey.repository.SurveyListItemProjection

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
            createdAt = survey.createdAt.withOffsetSameInstant(DateTimeUtils.KOREA_OFFSET),
        )

    fun toListItemResponse(survey: SurveyListItemProjection): SurveyListItemResponse =
        SurveyListItemResponse(
            surveyId = survey.surveyId,
            title = survey.title,
            surveyVersion = survey.surveyVersion,
            status = SurveyStatus.valueOf(survey.status),
            maxScore = survey.maxScore,
            category = survey.category,
            estimatedTimeSec = survey.estimatedTimeSec,
            createdAt = DateTimeUtils.toKoreaOffsetDateTime(survey.createdAt),
        )
}
