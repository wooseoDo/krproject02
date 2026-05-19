package kr.krproject02.domain.survey.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.PrePersist
import jakarta.persistence.Table
import kr.krproject02.common.core.utils.DateTimeUtils
import kr.krproject02.common.core.utils.UuidV7Utils
import kr.krproject02.domain.survey.constants.SurveyConstants
import org.hibernate.annotations.Comment
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(
    name = "survey_response",
    indexes = [
        Index(name = "ix_survey_response_survey_id", columnList = "survey_id"),
        Index(name = "ix_survey_response_user_id", columnList = "user_id"),
        Index(name = "ix_survey_response_submitted_at", columnList = "submitted_at"),
    ],
)
open class SurveyResponse protected constructor(
    @Id
    @Column(name = "response_id", columnDefinition = "uuid", updatable = false, nullable = false)
    @field:Comment("조사지 응답 UUID")
    open var responseId: UUID? = null,

    @Column(name = "survey_id", columnDefinition = "uuid", nullable = false)
    @field:Comment("응답 대상 조사지 UUID")
    open var surveyId: UUID? = null,

    @Column(name = "survey_group_id", columnDefinition = "uuid", nullable = false)
    @field:Comment("응답 당시 조사지 버전 묶음 UUID")
    open var surveyGroupId: UUID? = null,

    @Column(name = "user_id", columnDefinition = "uuid", nullable = false)
    @field:Comment("응답 사용자 UUID")
    open var userId: UUID? = null,

    @Column(name = "survey_version", nullable = false)
    @field:Comment("응답 당시 조사지 버전")
    open var surveyVersion: Int = 1,

    @Column(name = "survey_title", nullable = false, length = SurveyConstants.MAX_SURVEY_TITLE_LENGTH)
    @field:Comment("응답 당시 조사지 제목")
    open var surveyTitle: String = "",

    @Column(name = "started_at", nullable = false)
    @field:Comment("응답 시작 일시")
    open var startedAt: OffsetDateTime = DateTimeUtils.nowKorea(),

    @Column(name = "submitted_at")
    @field:Comment("응답 제출 일시")
    open var submittedAt: OffsetDateTime? = null,

    @Column(name = "elapsed_time_sec")
    @field:Comment("응답 소요 시간, 초 단위")
    open var elapsedTimeSec: Int? = null,

    @Column(name = "total_score", precision = 8, scale = 2)
    @field:Comment("응답 총점")
    open var totalScore: BigDecimal? = null,

    @Column(name = "is_completed", nullable = false)
    @field:Comment("응답 완료 여부")
    open var completed: Boolean = false,

    @Column(name = "is_deleted", nullable = false)
    @field:Comment("논리 삭제 여부")
    open var deleted: Boolean = false,

    @Column(name = "deleted_at")
    @field:Comment("삭제 일시")
    open var deletedAt: OffsetDateTime? = null,
) {
    @PrePersist
    fun onPrePersist() {
        if (responseId == null) {
            responseId = UuidV7Utils.generate()
        }
    }
}
