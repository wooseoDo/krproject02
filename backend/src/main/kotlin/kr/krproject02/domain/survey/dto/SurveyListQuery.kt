package kr.krproject02.domain.survey.dto

import kr.krproject02.common.core.utils.DateTimeUtils
import kr.krproject02.domain.survey.constants.SurveyStatus
import java.time.LocalDate
import java.time.OffsetDateTime

data class SurveyListQuery(
    val page: Int,
    val size: Int,
    val title: String?,
    val maxScore: Int?,
    val estimatedTimeSec: Int?,
    val surveyVersion: Int?,
    val status: SurveyStatus?,
    val releasedAtFrom: LocalDate?,
    val releasedAtTo: LocalDate?,
) {
    val safePage: Int = page.coerceAtLeast(MIN_PAGE)
    val safeSize: Int = size.coerceIn(MIN_SIZE, MAX_SIZE)
    val offset: Int = (safePage - 1) * safeSize
    val normalizedTitle: String? = title?.trim()?.takeIf(String::isNotBlank)
    val releasedFromDateTime: OffsetDateTime? = releasedAtFrom?.atStartOfDay()?.atOffset(DateTimeUtils.KOREA_OFFSET)
    val releasedToDateTime: OffsetDateTime? = releasedAtTo?.plusDays(1)?.atStartOfDay()?.atOffset(DateTimeUtils.KOREA_OFFSET)

    companion object {
        private const val MIN_PAGE = 1
        private const val MIN_SIZE = 1
        private const val MAX_SIZE = 50
    }
}
