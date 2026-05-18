package kr.krproject02.domain.survey.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import kr.krproject02.domain.survey.dto.SurveyListItemResponse
import kr.krproject02.domain.survey.dto.admin.AdminSurveyCreateRequest
import kr.krproject02.domain.survey.dto.admin.AdminSurveyCreateResponse
import kr.krproject02.domain.survey.dto.admin.AdminSurveyStatusUpdateRequest
import kr.krproject02.domain.survey.dto.admin.AdminSurveyStatusUpdateResponse
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import java.util.UUID

@Tag(name = "관리자 조사지", description = "관리자 scope 조사지 API")
interface AdminSurveyApi {
    @Operation(
        summary = "관리자 조사지 리스트 조회",
        description = "관리자 화면에 표시할 조사지 제목, 버전, 상태, 최대점수, 카테고리, 예상 소요 시간, 생성일을 조회합니다.",
    )
    fun getSurveyList(): List<SurveyListItemResponse>

    @Operation(
        summary = "관리자 조사지 생성",
        description = "관리자가 제출한 조사지 제목, 문항, 선택지 전체 구조를 JSONB 스키마와 JPA 테이블 구조로 함께 저장합니다.",
    )
    fun createSurvey(
        @Valid @RequestBody request: AdminSurveyCreateRequest,
    ): AdminSurveyCreateResponse

    @Operation(
        summary = "관리자 조사지 상태 변경",
        description = "조사지 상태를 DRAFT, PUBLISHED, LOCKED, CLOSED 중 하나로 변경합니다.",
    )
    fun updateSurveyStatus(
        @PathVariable surveyId: UUID,
        @Valid @RequestBody request: AdminSurveyStatusUpdateRequest,
    ): AdminSurveyStatusUpdateResponse
}
