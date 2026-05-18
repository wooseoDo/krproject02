package kr.krproject02.domain.survey.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kr.krproject02.domain.survey.dto.SurveyListItemResponse

@Tag(name = "관리자 조사지", description = "관리자 scope 조사지 API")
interface AdminSurveyApi {
    @Operation(
        summary = "관리자 조사지 리스트 조회",
        description = "관리자 화면에 표시할 조사지 제목, 버전, 상태, 최대점수, 카테고리, 예상 소요 시간, 생성일을 조회합니다.",
    )
    fun getSurveyList(): List<SurveyListItemResponse>
}
