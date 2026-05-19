package kr.krproject02.domain.survey.dto.normal

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.util.UUID

@Schema(description = "일반 사용자 조사지 최종 제출 요청")
data class NormalSurveySubmitRequest(
    @field:Schema(description = "응답 소요 시간, 초 단위")
    @field:Min(0)
    val elapsedTimeSec: Int,

    @field:Schema(description = "문항별 선택 답변")
    @field:Valid
    @field:NotEmpty
    val answers: List<NormalSurveySubmitAnswerRequest>,
)

data class NormalSurveySubmitAnswerRequest(
    @field:Schema(description = "문항 UUID")
    @field:NotNull
    val questionId: UUID?,

    @field:Schema(description = "선택지 UUID")
    @field:NotNull
    val optionId: UUID?,
)
