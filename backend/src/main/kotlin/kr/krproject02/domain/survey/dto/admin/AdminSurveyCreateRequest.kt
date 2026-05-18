package kr.krproject02.domain.survey.dto.admin

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size
import kr.krproject02.domain.survey.constants.SurveyConstants
import kr.krproject02.domain.survey.constants.SurveyQuestionType
import kr.krproject02.domain.survey.constants.SurveyStatus

@Schema(description = "관리자 조사지 생성 요청")
data class AdminSurveyCreateRequest(
    @field:Schema(description = "조사지 제목", example = "직무 스트레스 자가진단 조사지")
    @field:NotBlank
    @field:Size(max = SurveyConstants.MAX_SURVEY_TITLE_LENGTH)
    val title: String,

    @field:Schema(description = "조사지 카테고리", example = "심리/직무")
    @field:Size(max = SurveyConstants.MAX_SURVEY_CATEGORY_LENGTH)
    val category: String?,

    @field:Schema(description = "조사지 설명", example = "업무 환경에서 느끼는 스트레스 정도를 확인하는 조사지")
    val description: String?,

    @field:Schema(description = "최종 저장할 조사지 상태", example = "DRAFT")
    val status: SurveyStatus = SurveyStatus.DRAFT,

    @field:Schema(description = "조사지 최대 점수. 모든 문항 배점 총합과 같아야 합니다.", example = "30")
    @field:Positive
    val maxScore: Int,

    @field:Schema(description = "예상 소요 시간, 초 단위", example = "600")
    @field:Positive
    val estimatedTimeSec: Int,

    @field:Schema(description = "조사지 항목 목록")
    @field:Valid
    val sections: List<AdminSurveySectionRequest>,
)

@Schema(description = "관리자 조사지 항목 생성 요청")
data class AdminSurveySectionRequest(
    @field:Schema(description = "항목 제목", example = "업무 부담")
    @field:NotBlank
    @field:Size(max = SurveyConstants.MAX_SURVEY_SECTION_TITLE_LENGTH)
    val title: String,

    @field:Schema(description = "항목 목표 평균 점수", example = "7.0")
    val targetAverageScore: Double?,

    @field:Schema(description = "항목에 포함할 문항 목록")
    @field:Valid
    val questions: List<AdminSurveyQuestionRequest>,
)

@Schema(description = "관리자 조사지 문항 생성 요청")
data class AdminSurveyQuestionRequest(
    @field:Schema(description = "문항 유형", example = "LIKERT")
    val questionType: SurveyQuestionType,

    @field:Schema(description = "문항 제목", example = "최근 2주 동안 업무량이 감당하기 어렵다고 느낀 적이 있다.")
    @field:NotBlank
    @field:Size(max = SurveyConstants.MAX_SURVEY_QUESTION_TITLE_LENGTH)
    val title: String,

    @field:Schema(description = "문항 배점. 모든 문항 배점 총합은 조사지 최대 점수와 같아야 합니다.", example = "5")
    @field:Positive
    val score: Int,

    @field:Schema(description = "문항 선택지 목록. 2개 이상 5개 이하")
    @field:Valid
    val options: List<AdminSurveyQuestionOptionRequest>,
)

@Schema(description = "관리자 조사지 문항 선택지 생성 요청")
data class AdminSurveyQuestionOptionRequest(
    @field:Schema(description = "선택지 라벨", example = "매우 그렇다")
    @field:NotBlank
    @field:Size(max = SurveyConstants.MAX_SURVEY_OPTION_LABEL_LENGTH)
    val optionLabel: String,

    @field:Schema(description = "선택지 점수", example = "5")
    @field:PositiveOrZero
    val optionScore: Int,
)
