package kr.krproject02.domain.survey.dto.admin

import io.swagger.v3.oas.annotations.media.Schema
import kr.krproject02.domain.survey.constants.SurveyStatus
import java.util.UUID

@Schema(description = "관리자 조사지 생성 응답")
data class AdminSurveyCreateResponse(
    @field:Schema(description = "조사지 UUID")
    val surveyId: UUID?,

    @field:Schema(description = "조사지 제목", example = "직무 스트레스 자가진단 조사지")
    val title: String,

    @field:Schema(description = "조사지 버전", example = "1")
    val surveyVersion: Int,

    @field:Schema(description = "조사지 상태", example = "DRAFT")
    val status: SurveyStatus,
)
