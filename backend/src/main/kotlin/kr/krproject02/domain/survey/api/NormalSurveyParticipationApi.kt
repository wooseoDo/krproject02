package kr.krproject02.domain.survey.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import kr.krproject02.domain.survey.dto.normal.NormalSurveyParticipationDetailResponse
import kr.krproject02.domain.survey.dto.normal.NormalSurveyParticipationStartRequest
import kr.krproject02.domain.survey.dto.normal.NormalSurveyParticipationStartResponse
import kr.krproject02.domain.survey.dto.normal.NormalSurveySubmitRequest
import kr.krproject02.domain.survey.dto.normal.NormalSurveySubmitResponse
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import java.util.UUID

@Tag(name = "일반 사용자 조사지 참여", description = "일반 사용자 scope 조사지 참여 API")
interface NormalSurveyParticipationApi {
    @Operation(
        summary = "조사지 참여 상세 조회",
        description = "일반 사용자가 참여 가능한 최신 조사지의 섹션, 문항, 선택지 구조를 조회합니다.",
    )
    fun getParticipationDetail(
        @PathVariable surveyId: UUID,
    ): NormalSurveyParticipationDetailResponse

    @Operation(
        summary = "조사지 참여 시작",
        description = "사용자가 조사지 참여를 시작합니다. 같은 사용자는 같은 조사지 버전에 중복 참여할 수 없고, 새 버전이 생성되면 다시 참여할 수 있습니다.",
    )
    fun startParticipation(
        @PathVariable surveyId: UUID,
        @Valid @RequestBody request: NormalSurveyParticipationStartRequest,
    ): NormalSurveyParticipationStartResponse

    @Operation(
        summary = "조사지 최종 제출",
        description = "사용자가 작성한 문항별 선택 답변을 제출하고 응답 스냅샷과 총점을 저장합니다.",
    )
    fun submitParticipation(
        @PathVariable responseId: UUID,
        @Valid @RequestBody request: NormalSurveySubmitRequest,
    ): NormalSurveySubmitResponse
}
