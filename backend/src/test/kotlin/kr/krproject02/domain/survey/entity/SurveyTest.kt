package kr.krproject02.domain.survey.entity

import kr.krproject02.domain.survey.constants.SurveyStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.UUID

class SurveyTest {
    @Test
    fun `new survey uses its own id as survey group id before persist`() {
        val survey = survey()

        survey.onPrePersist()

        assertThat(survey.surveyId).isNotNull()
        assertThat(survey.surveyGroupId).isEqualTo(survey.surveyId)
        assertThat(survey.previousSurveyId).isNull()
        assertThat(survey.surveyVersion).isEqualTo(1)
        assertThat(survey.latest).isTrue()
    }

    @Test
    fun `new survey version keeps original group id and previous survey id`() {
        val groupId = UUID.randomUUID()
        val previousSurveyId = UUID.randomUUID()
        val survey = survey(
            surveyGroupId = groupId,
            previousSurveyId = previousSurveyId,
            surveyVersion = 2,
        )

        survey.onPrePersist()

        assertThat(survey.surveyId).isNotNull()
        assertThat(survey.surveyGroupId).isEqualTo(groupId)
        assertThat(survey.previousSurveyId).isEqualTo(previousSurveyId)
        assertThat(survey.surveyVersion).isEqualTo(2)
        assertThat(survey.latest).isTrue()
    }

    @Test
    fun `mark not latest changes latest flag only`() {
        val survey = survey()

        survey.markNotLatest()

        assertThat(survey.latest).isFalse()
        assertThat(survey.deleted).isFalse()
    }

    private fun survey(
        surveyGroupId: UUID? = null,
        previousSurveyId: UUID? = null,
        surveyVersion: Int = 1,
    ): Survey =
        Survey.create(
            title = "Work Stress Survey",
            category = "health/work",
            description = "Survey for checking work stress level.",
            status = SurveyStatus.PUBLISHED,
            maxScore = 30,
            estimatedTimeSec = 600,
            surveySchema = "{}",
            surveyGroupId = surveyGroupId,
            previousSurveyId = previousSurveyId,
            surveyVersion = surveyVersion,
        )
}
