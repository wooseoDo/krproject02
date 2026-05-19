package kr.krproject02.domain.survey.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
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
    name = "survey_section",
    indexes = [
        Index(name = "ix_survey_section_survey_id", columnList = "survey_id"),
        Index(name = "ix_survey_section_title", columnList = "title"),
    ],
)
open class SurveySection protected constructor(
    @Id
    @Column(name = "section_id", columnDefinition = "uuid", updatable = false, nullable = false)
    @field:Comment("조사지 항목 UUID")
    open var sectionId: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false)
    @field:Comment("조사지")
    open var survey: Survey,

    @Column(name = "section_sort", nullable = false)
    @field:Comment("조사지 내 항목 정렬 순서")
    open var sectionSort: Int,

    @Column(name = "title", nullable = false, length = SurveyConstants.MAX_SURVEY_SECTION_TITLE_LENGTH)
    @field:Comment("항목 제목")
    open var title: String,

    @Column(name = "target_average_score", precision = 6, scale = 2)
    @field:Comment("항목 목표 평균 점수")
    open var targetAverageScore: BigDecimal? = null,

    @Column(name = "created_at", nullable = false)
    @field:Comment("생성 일시")
    open var createdAt: OffsetDateTime = DateTimeUtils.nowKorea(),

    @Column(name = "updated_at")
    @field:Comment("수정 일시")
    open var updatedAt: OffsetDateTime? = null,
) {
    @PrePersist
    fun onPrePersist() {
        if (sectionId == null) {
            sectionId = UuidV7Utils.generate()
        }
    }

    companion object {
        fun create(
            survey: Survey,
            sectionSort: Int,
            title: String,
            targetAverageScore: BigDecimal?,
        ): SurveySection =
            SurveySection(
                survey = survey,
                sectionSort = sectionSort,
                title = title,
                targetAverageScore = targetAverageScore,
            )
    }
}
