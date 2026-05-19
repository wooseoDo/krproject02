package kr.krproject02.domain.survey.controller

import jakarta.validation.Valid
import kr.krproject02.common.security.permissions.SurveyPermissions.SURVEY_DETAIL_READ
import kr.krproject02.common.security.permissions.SurveyPermissions.SURVEY_RESPONSE_CREATE
import kr.krproject02.common.security.permissions.SurveyPermissions.SURVEY_RESPONSE_UPDATE
import kr.krproject02.domain.survey.api.NormalSurveyParticipationApi
import kr.krproject02.domain.survey.dto.normal.NormalSurveyParticipationDetailResponse
import kr.krproject02.domain.survey.dto.normal.NormalSurveyParticipationStartRequest
import kr.krproject02.domain.survey.dto.normal.NormalSurveyParticipationStartResponse
import kr.krproject02.domain.survey.dto.normal.NormalSurveySubmitRequest
import kr.krproject02.domain.survey.dto.normal.NormalSurveySubmitResponse
import kr.krproject02.domain.survey.service.NormalSurveyService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping(value = ["/normal/surveys", "/api/normal/surveys"])
class NormalSurveyParticipationController(
    private val normalSurveyService: NormalSurveyService,
) : NormalSurveyParticipationApi {
    @GetMapping("/{surveyId}/participation")
    @PreAuthorize("hasAuthority('$SURVEY_DETAIL_READ')")
    override fun getParticipationDetail(
        @PathVariable surveyId: UUID,
    ): NormalSurveyParticipationDetailResponse =
        normalSurveyService.getParticipationDetail(surveyId)

    @PostMapping("/{surveyId}/responses/start")
    @PreAuthorize("hasAuthority('$SURVEY_RESPONSE_CREATE')")
    override fun startParticipation(
        @PathVariable surveyId: UUID,
        @Valid @RequestBody request: NormalSurveyParticipationStartRequest,
    ): NormalSurveyParticipationStartResponse =
        normalSurveyService.startParticipation(
            surveyId = surveyId,
            userId = request.userId,
        )

    @PostMapping("/responses/{responseId}/submit")
    @PreAuthorize("hasAuthority('$SURVEY_RESPONSE_UPDATE')")
    override fun submitParticipation(
        @PathVariable responseId: UUID,
        @Valid @RequestBody request: NormalSurveySubmitRequest,
    ): NormalSurveySubmitResponse =
        normalSurveyService.submitParticipation(
            responseId = responseId,
            request = request,
        )
}
