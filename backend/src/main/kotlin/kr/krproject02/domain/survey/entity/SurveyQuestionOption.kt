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
import kr.krproject02.common.core.utils.UuidV7Utils
import kr.krproject02.domain.survey.constants.SurveyConstants
import org.hibernate.annotations.Comment
import java.util.UUID

@Entity
@Table(
    name = "survey_question_option",
    indexes = [
        Index(name = "ix_survey_question_option_question_id", columnList = "question_id"),
    ],
)
open class SurveyQuestionOption protected constructor(
    @Id
    @Column(name = "option_id", columnDefinition = "uuid", updatable = false, nullable = false)
    @field:Comment("선택지 UUID")
    open var optionId: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @field:Comment("문항")
    open var question: SurveyQuestion,

    @Column(name = "option_sort", nullable = false)
    @field:Comment("선택지 정렬 순서")
    open var optionSort: Int,

    @Column(name = "option_label", nullable = false, length = SurveyConstants.MAX_SURVEY_OPTION_LABEL_LENGTH)
    @field:Comment("선택지 라벨")
    open var optionLabel: String,

    @Column(name = "option_score", nullable = false)
    @field:Comment("선택지 점수")
    open var optionScore: Int,
) {
    @PrePersist
    fun onPrePersist() {
        if (optionId == null) {
            optionId = UuidV7Utils.generate()
        }
    }

    companion object {
        fun create(
            question: SurveyQuestion,
            optionSort: Int,
            optionLabel: String,
            optionScore: Int,
        ): SurveyQuestionOption =
            SurveyQuestionOption(
                question = question,
                optionSort = optionSort,
                optionLabel = optionLabel,
                optionScore = optionScore,
            )
    }
}
