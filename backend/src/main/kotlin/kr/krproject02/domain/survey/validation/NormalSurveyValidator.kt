package kr.krproject02.domain.survey.validation

import org.springframework.stereotype.Component

@Component
class NormalSurveyValidator {
    fun validateListReadable() {
        // 현재 일반 사용자 리스트 조회는 추가 입력값이 없어 검증할 조건이 없다.
    }
}
