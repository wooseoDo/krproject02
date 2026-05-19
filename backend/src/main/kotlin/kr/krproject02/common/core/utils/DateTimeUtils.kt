package kr.krproject02.common.core.utils

import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

object DateTimeUtils {
    val KOREA_OFFSET: ZoneOffset = ZoneOffset.ofHours(9)

    fun nowKorea(): OffsetDateTime =
        OffsetDateTime.now(KOREA_OFFSET)

    fun toKoreaOffsetDateTime(instant: Instant): OffsetDateTime =
        OffsetDateTime.ofInstant(instant, KOREA_OFFSET)
}
