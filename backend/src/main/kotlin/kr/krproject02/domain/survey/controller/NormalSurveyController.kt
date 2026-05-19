package kr.krproject02.domain.survey.controller

import kr.krproject02.common.security.permissions.SurveyPermissions.SURVEY_PAGE_READ
import kr.krproject02.domain.survey.api.NormalSurveyApi
import kr.krproject02.domain.survey.constants.SurveyStatus
import kr.krproject02.domain.survey.dto.SurveyListPageResponse
import kr.krproject02.domain.survey.dto.SurveyListQuery
import kr.krproject02.domain.survey.service.NormalSurveyService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping(value = ["/normal/surveys", "/api/normal/surveys"])
class NormalSurveyController(
    private val normalSurveyService: NormalSurveyService,
) : NormalSurveyApi {
    @GetMapping
    @PreAuthorize("hasAuthority('$SURVEY_PAGE_READ')")
    override fun getSurveyList(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "2") size: Int,
        @RequestParam(required = false) title: String?,
        @RequestParam(required = false) maxScore: Int?,
        @RequestParam(required = false) estimatedTimeSec: Int?,
        @RequestParam(required = false) surveyVersion: Int?,
        @RequestParam(required = false) status: SurveyStatus?,
        @RequestParam(required = false) releasedAtFrom: LocalDate?,
        @RequestParam(required = false) releasedAtTo: LocalDate?,
    ): SurveyListPageResponse =
        normalSurveyService.getSurveyList(
            SurveyListQuery(
                page = page,
                size = size,
                title = title,
                maxScore = maxScore,
                estimatedTimeSec = estimatedTimeSec,
                surveyVersion = surveyVersion,
                status = status,
                releasedAtFrom = releasedAtFrom,
                releasedAtTo = releasedAtTo,
            ),
        )
}
