package kr.krproject02.domain.survey.dto.normal

import io.swagger.v3.oas.annotations.media.Schema
import kr.krproject02.domain.survey.constants.SurveyQuestionType
import kr.krproject02.domain.survey.constants.SurveyStatus
import java.math.BigDecimal
import java.util.UUID

@Schema(description = "일반 사용자 조사지 참여 상세 응답")
data class NormalSurveyParticipationDetailResponse(
    @field:Schema(description = "조사지 UUID")
    val surveyId: UUID?,

    @field:Schema(description = "조사지 버전 묶음 UUID")
    val surveyGroupId: UUID?,

    @field:Schema(description = "조사지 버전")
    val surveyVersion: Int,

    @field:Schema(description = "조사지 제목")
    val title: String,

    @field:Schema(description = "조사지 카테고리")
    val category: String?,

    @field:Schema(description = "조사지 설명")
    val description: String?,

    @field:Schema(description = "조사지 상태")
    val status: SurveyStatus,

    @field:Schema(description = "조사지 최고 점수")
    val maxScore: Int,

    @field:Schema(description = "예상 소요 시간, 초 단위")
    val estimatedTimeSec: Int?,

    @field:Schema(description = "조사지 섹션 목록")
    val sections: List<NormalSurveyParticipationSectionResponse>,
)

data class NormalSurveyParticipationSectionResponse(
    val sectionId: UUID?,
    val sectionSort: Int,
    val title: String,
    val targetAverageScore: BigDecimal?,
    val questions: List<NormalSurveyParticipationQuestionResponse>,
)

data class NormalSurveyParticipationQuestionResponse(
    val questionId: UUID?,
    val questionSort: Int,
    val questionType: SurveyQuestionType,
    val title: String,
    val options: List<NormalSurveyParticipationOptionResponse>,
)

data class NormalSurveyParticipationOptionResponse(
    val optionId: UUID?,
    val optionSort: Int,
    val optionLabel: String,
)
