package kr.krproject02.domain.survey.controller

import kr.krproject02.domain.survey.api.AdminSurveyApi
import kr.krproject02.domain.survey.dto.SurveyListItemResponse
import kr.krproject02.domain.survey.service.AdminSurveyService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/surveys", "/api/admin/surveys")
class AdminSurveyController(
    private val adminSurveyService: AdminSurveyService,
) : AdminSurveyApi {
    @GetMapping
    override fun getSurveyList(): List<SurveyListItemResponse> =
        adminSurveyService.getSurveyList()
}
