package kr.krproject02.domain.survey.dto.admin

import io.swagger.v3.oas.annotations.media.Schema
import kr.krproject02.domain.survey.constants.SurveyStatus

@Schema(description = "관리자 조사지 상태 변경 요청")
data class AdminSurveyStatusUpdateRequest(
    @field:Schema(description = "변경할 조사지 상태", example = "PUBLISHED")
    val status: SurveyStatus,
)
