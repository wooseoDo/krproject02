package kr.krproject02.domain.survey.dto.normal

import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

@Schema(description = "일반 사용자 조사지 참여 시작 응답")
data class NormalSurveyParticipationStartResponse(
    @field:Schema(description = "응답 UUID")
    val responseId: UUID?,

    @field:Schema(description = "조사지 UUID")
    val surveyId: UUID?,

    @field:Schema(description = "조사지 버전 묶음 UUID")
    val surveyGroupId: UUID?,

    @field:Schema(description = "조사지 버전")
    val surveyVersion: Int,

    @field:Schema(description = "조사지 제목")
    val surveyTitle: String,

    @field:Schema(description = "기존 진행 응답 재사용 여부")
    val resumed: Boolean,
)
