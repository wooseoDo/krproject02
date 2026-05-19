package kr.krproject02.domain.survey.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kr.krproject02.domain.survey.constants.SurveyStatus
import kr.krproject02.domain.survey.dto.SurveyListPageResponse
import org.springframework.web.bind.annotation.RequestParam
import java.time.LocalDate

@Tag(name = "일반 사용자 조사지", description = "일반 사용자 scope 조사지 API")
interface NormalSurveyApi {
    @Operation(
        summary = "일반 사용자 조사지 목록 조회",
        description = "일반 사용자 화면에 표시할 조사지 목록을 offset 페이징으로 조회합니다. LOCKED, CLOSED 상태는 제외됩니다.",
    )
    fun getSurveyList(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) title: String?,
        @RequestParam(required = false) maxScore: Int?,
        @RequestParam(required = false) estimatedTimeSec: Int?,
        @RequestParam(required = false) surveyVersion: Int?,
        @RequestParam(required = false) status: SurveyStatus?,
        @RequestParam(required = false) releasedAtFrom: LocalDate?,
        @RequestParam(required = false) releasedAtTo: LocalDate?,
    ): SurveyListPageResponse
}
