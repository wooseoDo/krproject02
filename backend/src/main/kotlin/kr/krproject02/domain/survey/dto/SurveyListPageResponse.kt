package kr.krproject02.domain.survey.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "조사지 목록 페이징 응답")
data class SurveyListPageResponse(
    @field:Schema(description = "현재 페이지 조사지 목록")
    val items: List<SurveyListItemResponse>,

    @field:Schema(description = "현재 페이지 번호", example = "1")
    val page: Int,

    @field:Schema(description = "페이지 크기", example = "10")
    val size: Int,

    @field:Schema(description = "전체 조사지 수", example = "25")
    val totalItems: Long,

    @field:Schema(description = "전체 페이지 수", example = "3")
    val totalPages: Int,
)
