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
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(
    name = "survey_response_answer",
    indexes = [
        Index(name = "ix_survey_response_answer_response_id", columnList = "response_id"),
        Index(name = "ix_survey_response_answer_survey_id", columnList = "survey_id"),
        Index(name = "ix_survey_response_answer_section_id", columnList = "section_id"),
        Index(name = "ix_survey_response_answer_question_sort", columnList = "response_id, question_sort"),
        Index(name = "ix_survey_response_answer_stats", columnList = "survey_id, section_id, score"),
    ],
)
open class SurveyResponseAnswer protected constructor(
    @Id
    @Column(name = "answer_id", columnDefinition = "uuid", updatable = false, nullable = false)
    open var answerId: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "response_id", nullable = false)
    open var response: SurveyResponse,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false)
    open var survey: Survey,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    open var section: SurveySection,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    open var question: SurveyQuestion,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id")
    open var option: SurveyQuestionOption?,

    @Column(name = "section_sort", nullable = false)
    open var sectionSort: Int,

    @Column(name = "question_sort", nullable = false)
    open var questionSort: Int,

    @Column(name = "option_sort", nullable = false)
    open var optionSort: Int,

    @Column(name = "score", nullable = false, precision = 6, scale = 2)
    open var score: BigDecimal,

    @Column(name = "snapshot_section_title", length = SurveyConstants.MAX_SURVEY_SECTION_TITLE_LENGTH)
    open var snapshotSectionTitle: String?,

    @Column(name = "snapshot_question_title", length = SurveyConstants.MAX_SURVEY_QUESTION_TITLE_LENGTH)
    open var snapshotQuestionTitle: String?,

    @Column(name = "snapshot_question_type", length = 30)
    open var snapshotQuestionType: String?,

    @Column(name = "snapshot_option_label", length = SurveyConstants.MAX_SURVEY_OPTION_LABEL_LENGTH)
    open var snapshotOptionLabel: String?,

    @Column(name = "created_at", nullable = false)
    open var createdAt: OffsetDateTime = DateTimeUtils.nowKorea(),
) {
    @PrePersist
    fun onPrePersist() {
        if (answerId == null) {
            answerId = UuidV7Utils.generate()
        }
    }

    companion object {
        fun create(
            response: SurveyResponse,
            survey: Survey,
            question: SurveyQuestion,
            option: SurveyQuestionOption,
        ): SurveyResponseAnswer =
            SurveyResponseAnswer(
                response = response,
                survey = survey,
                section = question.section,
                question = question,
                option = option,
                sectionSort = question.section.sectionSort,
                questionSort = question.questionSort,
                optionSort = option.optionSort,
                score = BigDecimal.valueOf(option.optionScore.toLong()),
                snapshotSectionTitle = question.section.title,
                snapshotQuestionTitle = question.title,
                snapshotQuestionType = question.questionType.name,
                snapshotOptionLabel = option.optionLabel,
                createdAt = DateTimeUtils.nowKorea(),
            )
    }
}
