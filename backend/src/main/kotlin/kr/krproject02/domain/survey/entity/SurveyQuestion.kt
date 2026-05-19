package kr.krproject02.domain.survey.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
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
import kr.krproject02.domain.survey.constants.SurveyQuestionType
import org.hibernate.annotations.Comment
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(
    name = "survey_question",
    indexes = [
        Index(name = "ix_survey_question_survey_id", columnList = "survey_id"),
        Index(name = "ix_survey_question_section_id", columnList = "section_id"),
        Index(name = "ix_survey_question_type", columnList = "question_type"),
    ],
)
open class SurveyQuestion protected constructor(
    @Id
    @Column(name = "question_id", columnDefinition = "uuid", updatable = false, nullable = false)
    @field:Comment("문항 UUID")
    open var questionId: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false)
    @field:Comment("조사지")
    open var survey: Survey,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    @field:Comment("조사지 항목")
    open var section: SurveySection,

    @Column(name = "question_sort", nullable = false)
    @field:Comment("조사지 내 문항 정렬 순서")
    open var questionSort: Int,

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false, length = 30)
    @field:Comment("문항 유형")
    open var questionType: SurveyQuestionType,

    @Column(name = "title", nullable = false, length = SurveyConstants.MAX_SURVEY_QUESTION_TITLE_LENGTH)
    @field:Comment("문항 제목")
    open var title: String,

    @Column(name = "score", nullable = false)
    @field:Comment("문항 배점")
    open var score: Int,

    @Column(name = "option_count", nullable = false)
    @field:Comment("선택지 개수")
    open var optionCount: Int,

    @Column(name = "created_at", nullable = false)
    @field:Comment("생성 일시")
    open var createdAt: OffsetDateTime = DateTimeUtils.nowKorea(),

    @Column(name = "updated_at")
    @field:Comment("수정 일시")
    open var updatedAt: OffsetDateTime? = null,
) {
    @PrePersist
    fun onPrePersist() {
        if (questionId == null) {
            questionId = UuidV7Utils.generate()
        }
    }

    companion object {
        fun create(
            survey: Survey,
            section: SurveySection,
            questionSort: Int,
            questionType: SurveyQuestionType,
            title: String,
            score: Int,
            optionCount: Int,
        ): SurveyQuestion =
            SurveyQuestion(
                survey = survey,
                section = section,
                questionSort = questionSort,
                questionType = questionType,
                title = title,
                score = score,
                optionCount = optionCount,
            )
    }
}
