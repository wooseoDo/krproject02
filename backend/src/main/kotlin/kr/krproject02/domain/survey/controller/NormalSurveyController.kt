package kr.krproject02.domain.survey.controller

import kr.krproject02.domain.survey.api.NormalSurveyApi
import kr.krproject02.domain.survey.dto.SurveyListItemResponse
import kr.krproject02.domain.survey.service.NormalSurveyService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kr.krproject02.common.security.permissions.SurveyPermissions.SURVEY_PAGE_READ
@RestController
@RequestMapping("/normal/surveys", "/api/normal/surveys")
class NormalSurveyController(
    private val normalSurveyService: NormalSurveyService,
) : NormalSurveyApi {
    @GetMapping
    @PreAuthorize("hasAuthority('$SURVEY_PAGE_READ')")
    override fun getSurveyList(): List<SurveyListItemResponse> =
        normalSurveyService.getSurveyList()
}
