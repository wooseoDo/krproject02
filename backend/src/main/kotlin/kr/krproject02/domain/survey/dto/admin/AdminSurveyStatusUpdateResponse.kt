package kr.krproject02.domain.survey.dto.admin

import io.swagger.v3.oas.annotations.media.Schema
import kr.krproject02.domain.survey.constants.SurveyStatus
import java.util.UUID

@Schema(description = "관리자 조사지 상태 변경 응답")
data class AdminSurveyStatusUpdateResponse(
    @field:Schema(description = "조사지 UUID")
    val surveyId: UUID?,

    @field:Schema(description = "변경된 조사지 상태", example = "PUBLISHED")
    val status: SurveyStatus,
)
