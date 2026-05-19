package kr.krproject02.domain.survey.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import kr.krproject02.domain.survey.constants.SurveyStatus
import kr.krproject02.domain.survey.dto.SurveyListPageResponse
import kr.krproject02.domain.survey.dto.admin.AdminSurveyCreateRequest
import kr.krproject02.domain.survey.dto.admin.AdminSurveyCreateResponse
import kr.krproject02.domain.survey.dto.admin.AdminSurveyStatusUpdateRequest
import kr.krproject02.domain.survey.dto.admin.AdminSurveyStatusUpdateResponse
import kr.krproject02.domain.survey.dto.admin.AdminSurveyUpdateRequest
import kr.krproject02.domain.survey.dto.admin.AdminSurveyUpdateResponse
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import java.time.LocalDate
import java.util.UUID

@Tag(name = "관리자 조사지", description = "관리자 scope 조사지 API")
interface AdminSurveyApi {
    @Operation(
        summary = "관리자 조사지 목록 조회",
        description = "관리자 화면에 표시할 조사지 목록을 offset 페이징으로 조회합니다.",
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

    @Operation(
        summary = "관리자 조사지 생성",
        description = "관리자가 제출한 조사지 제목, 문항, 선택지 구조를 JSONB 원본과 JPA 테이블 구조로 함께 저장합니다.",
    )
    fun createSurvey(
        @Valid @RequestBody request: AdminSurveyCreateRequest,
    ): AdminSurveyCreateResponse

    @Operation(
        summary = "관리자 조사지 수정",
        description = "기존 조사지 row를 덮어쓰지 않고 새 버전을 생성합니다. 기존 버전은 최신 버전에서 제외됩니다.",
    )
    fun updateSurvey(
        @PathVariable surveyId: UUID,
        @Valid @RequestBody request: AdminSurveyUpdateRequest,
    ): AdminSurveyUpdateResponse

    @Operation(
        summary = "관리자 조사지 상태 변경",
        description = "조사지 상태를 DRAFT, PUBLISHED, LOCKED, CLOSED 중 하나로 변경합니다.",
    )
    fun updateSurveyStatus(
        @PathVariable surveyId: UUID,
        @Valid @RequestBody request: AdminSurveyStatusUpdateRequest,
    ): AdminSurveyStatusUpdateResponse
}
