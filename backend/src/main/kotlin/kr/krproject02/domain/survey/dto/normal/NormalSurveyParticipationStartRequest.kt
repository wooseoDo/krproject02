package kr.krproject02.domain.survey.dto.normal

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import java.util.UUID

@Schema(description = "일반 사용자 조사지 참여 시작 요청")
data class NormalSurveyParticipationStartRequest(
    @field:Schema(description = "참여 사용자 UUID")
    @field:NotNull
    val userId: UUID?,
)
