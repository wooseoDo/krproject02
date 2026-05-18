package kr.krproject02.domain.survey.validation

import org.springframework.stereotype.Component

@Component
class AdminSurveyValidator {
    fun validateListReadable() {
        // 현재 관리자 리스트 조회는 추가 입력값이 없어 검증할 조건이 없다.
    }
}
