package kr.krproject02.domain.survey.validation

import kr.krproject02.domain.survey.constants.SurveyStatus
import org.springframework.stereotype.Component

@Component
class NormalSurveyValidator {
    fun validateListReadable() {
        // 현재 일반 사용자 리스트 조회는 추가 입력값이 없어 검증할 조건이 없다.
    }

    fun canParticipate(status: SurveyStatus): Boolean =
        status != SurveyStatus.LOCKED && status != SurveyStatus.CLOSED
}
