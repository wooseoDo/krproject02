package kr.krproject02.domain.survey.controller

import jakarta.validation.Valid
import kr.krproject02.domain.survey.api.AdminSurveyApi
import kr.krproject02.domain.survey.dto.SurveyListItemResponse
import kr.krproject02.domain.survey.dto.admin.AdminSurveyCreateRequest
import kr.krproject02.domain.survey.dto.admin.AdminSurveyCreateResponse
import kr.krproject02.domain.survey.dto.admin.AdminSurveyStatusUpdateRequest
import kr.krproject02.domain.survey.dto.admin.AdminSurveyStatusUpdateResponse
import kr.krproject02.domain.survey.service.AdminSurveyService
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import kr.krproject02.common.security.permissions.SurveyPermissions.ADMIN_SURVEY_PAGE_READ
import kr.krproject02.common.security.permissions.SurveyPermissions.ADMIN_SURVEY_CREATE
import kr.krproject02.common.security.permissions.SurveyPermissions.ADMIN_SURVEY_UPDATE
import java.util.UUID

@RestController
@RequestMapping("/admin/surveys", "/api/admin/surveys")
class AdminSurveyController(
    private val adminSurveyService: AdminSurveyService,
) : AdminSurveyApi {
    @GetMapping
    @PreAuthorize("hasAuthority('$ADMIN_SURVEY_PAGE_READ')")
    override fun getSurveyList(): List<SurveyListItemResponse> =
        adminSurveyService.getSurveyList()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('$ADMIN_SURVEY_CREATE')")
    override fun createSurvey(
        @Valid @RequestBody request: AdminSurveyCreateRequest,
    ): AdminSurveyCreateResponse =
        adminSurveyService.createSurvey(request)

    @PatchMapping("/{surveyId}/status")
    @PreAuthorize("hasAuthority('$ADMIN_SURVEY_UPDATE')")
    override fun updateSurveyStatus(
        @PathVariable surveyId: UUID,
        @Valid @RequestBody request: AdminSurveyStatusUpdateRequest,
    ): AdminSurveyStatusUpdateResponse =
        adminSurveyService.updateSurveyStatus(surveyId, request)
}
