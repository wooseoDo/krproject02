package kr.krproject02.domain.survey.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.PrePersist
import jakarta.persistence.Table
import kr.krproject02.common.core.entity.SoftDelete
import kr.krproject02.common.core.utils.UuidV7Utils
import kr.krproject02.domain.survey.constants.SurveyConstants
import kr.krproject02.domain.survey.constants.SurveyStatus
import org.hibernate.annotations.Comment
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(
    name = "survey",
    indexes = [
        Index(name = "ix_survey_created_at", columnList = "created_at"),
        Index(name = "ix_survey_status", columnList = "status"),
        Index(name = "ix_survey_title", columnList = "title"),
        Index(name = "ix_survey_category", columnList = "category"),
        Index(name = "ix_survey_updated_at", columnList = "updated_at"),
    ],
)
open class Survey protected constructor(
    @Id
    @Column(name = "survey_id", columnDefinition = "uuid", updatable = false, nullable = false)
    @field:Comment("조사지 UUID")
    open var surveyId: UUID? = null,

    @Column(name = "survey_version", nullable = false)
    @field:Comment("조사지 버전")
    open var surveyVersion: Int = 1,

    @Column(name = "title", nullable = false, length = SurveyConstants.MAX_SURVEY_TITLE_LENGTH)
    @field:Comment("조사지 제목")
    open var title: String = "",

    @Column(name = "category", length = SurveyConstants.MAX_SURVEY_CATEGORY_LENGTH)
    @field:Comment("조사지 카테고리")
    open var category: String? = null,

    @Column(name = "description", columnDefinition = "text")
    @field:Comment("조사지 설명")
    open var description: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @field:Comment("조사지 상태")
    open var status: SurveyStatus = SurveyStatus.DRAFT,

    @Column(name = "max_score", nullable = false)
    @field:Comment("조사지 최대 점수")
    open var maxScore: Int = 1,

    @Column(name = "estimated_time_sec")
    @field:Comment("예상 소요 시간, 초 단위")
    open var estimatedTimeSec: Int? = null,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "survey_schema", nullable = false, columnDefinition = "jsonb")
    @field:Comment("조사지 전체 구조 JSONB")
    open var surveySchema: String = "{}",

    @Column(name = "locked_by")
    @field:Comment("잠금 처리한 사용자 UUID")
    open var lockedBy: UUID? = null,

    @Column(name = "locked_at")
    @field:Comment("잠금 처리 일시")
    open var lockedAt: OffsetDateTime? = null,
) : SoftDelete() {
    @PrePersist
    fun onPrePersist() {
        if (surveyId == null) {
            surveyId = UuidV7Utils.generate()
        }
    }

    fun changeStatus(status: SurveyStatus) {
        this.status = status
        lockedAt = if (status == SurveyStatus.LOCKED) OffsetDateTime.now() else null
        lockedBy = if (status == SurveyStatus.LOCKED) lockedBy else null
        markUpdated()
    }

    companion object {
        fun create(
            title: String,
            category: String?,
            description: String?,
            status: SurveyStatus,
            maxScore: Int,
            estimatedTimeSec: Int?,
            surveySchema: String,
        ): Survey =
            Survey(
                title = title,
                category = category,
                description = description,
                status = status,
                maxScore = maxScore,
                estimatedTimeSec = estimatedTimeSec,
                surveySchema = surveySchema,
            )
    }
}
