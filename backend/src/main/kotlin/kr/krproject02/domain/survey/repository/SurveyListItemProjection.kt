package kr.krproject02.domain.survey.repository

import java.time.Instant
import java.util.UUID

interface SurveyListItemProjection {
    val surveyId: UUID?
    val title: String
    val surveyVersion: Int
    val status: String
    val maxScore: Int
    val category: String?
    val estimatedTimeSec: Int?
    val createdAt: Instant
}
