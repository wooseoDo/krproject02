package kr.krproject02.common.core.utils

import java.security.SecureRandom
import java.time.Instant
import java.util.UUID

object UuidV7Utils {
    private val random = SecureRandom()

    fun generate(): UUID {
        val timestamp = Instant.now().toEpochMilli()
        // UUIDv7은 상위 48비트에 Unix epoch milliseconds를 담아 시간순 정렬성을 확보한다.
        val mostSignificantBits = (timestamp shl 16) or 0x7000L or random.nextInt(0x1000).toLong()
        // RFC 4122 variant 비트를 고정하고 나머지 하위 비트는 난수로 채운다.
        val leastSignificantBits = Long.MIN_VALUE or (random.nextLong() and 0x3fffffffffffffffL)
        return UUID(mostSignificantBits, leastSignificantBits)
    }
}
