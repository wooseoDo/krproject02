package kr.krproject02.domain.survey.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kr.krproject02.domain.survey.dto.SurveyListItemResponse

@Tag(name = "일반 사용자 조사지", description = "일반 사용자 scope 조사지 API")
interface NormalSurveyApi {
    @Operation(
        summary = "일반 사용자 조사지 리스트 조회",
        description = "일반 사용자 화면에 표시할 조사지 목록을 조회합니다. LOCKED, CLOSED 상태의 조사는 노출하지 않습니다.",
    )
    fun getSurveyList(): List<SurveyListItemResponse>
}
