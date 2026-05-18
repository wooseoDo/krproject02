package kr.krproject02.domain.survey.dto

import io.swagger.v3.oas.annotations.media.Schema
import kr.krproject02.domain.survey.constants.SurveyStatus
import java.time.OffsetDateTime
import java.util.UUID

@Schema(description = "조사지 리스트 항목 응답")
data class SurveyListItemResponse(
    @field:Schema(description = "조사지 UUID")
    val surveyId: UUID?,

    @field:Schema(description = "조사지 제목", example = "직무 스트레스 자가진단 조사지")
    val title: String,

    @field:Schema(description = "조사지 버전", example = "1")
    val surveyVersion: Int,

    @field:Schema(description = "조사지 상태", example = "PUBLISHED")
    val status: SurveyStatus,

    @field:Schema(description = "최대 점수", example = "30")
    val maxScore: Int,

    @field:Schema(description = "카테고리", example = "심리/직무")
    val category: String?,

    @field:Schema(description = "예상 소요 시간, 초 단위", example = "600")
    val estimatedTimeSec: Int?,

    @field:Schema(description = "생성일시")
    val createdAt: OffsetDateTime,
)
