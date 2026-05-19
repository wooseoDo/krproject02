package kr.krproject02.domain.survey.dto.normal

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

@Schema(description = "일반 사용자 조사지 최종 제출 응답")
data class NormalSurveySubmitResponse(
    @field:Schema(description = "응답 UUID")
    val responseId: UUID?,

    @field:Schema(description = "조사지 UUID")
    val surveyId: UUID?,

    @field:Schema(description = "조사지 버전 묶음 UUID")
    val surveyGroupId: UUID?,

    @field:Schema(description = "조사지 버전")
    val surveyVersion: Int,

    @field:Schema(description = "응답 완료 여부")
    val completed: Boolean,

    @field:Schema(description = "응답 제출 일시")
    val submittedAt: OffsetDateTime?,

    @field:Schema(description = "응답 소요 시간, 초 단위")
    val elapsedTimeSec: Int?,

    @field:Schema(description = "응답 총점")
    val totalScore: BigDecimal?,
)
