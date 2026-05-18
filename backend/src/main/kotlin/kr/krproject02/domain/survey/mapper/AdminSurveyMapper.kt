package kr.krproject02.domain.survey.mapper

import kr.krproject02.domain.survey.dto.admin.AdminSurveyCreateRequest
import kr.krproject02.domain.survey.dto.admin.AdminSurveyCreateResponse
import kr.krproject02.domain.survey.dto.admin.AdminSurveyStatusUpdateResponse
import kr.krproject02.domain.survey.entity.Survey

object AdminSurveyMapper {
    fun toCreateResponse(survey: Survey): AdminSurveyCreateResponse =
        AdminSurveyCreateResponse(
            surveyId = survey.surveyId,
            title = survey.title,
            surveyVersion = survey.surveyVersion,
            status = survey.status,
        )

    fun toStatusUpdateResponse(survey: Survey): AdminSurveyStatusUpdateResponse =
        AdminSurveyStatusUpdateResponse(
            surveyId = survey.surveyId,
            status = survey.status,
        )

    // 조사지 작성 화면의 중첩 구조를 PostgreSQL jsonb 컬럼에 그대로 저장할 수 있는 스키마 문자열로 변환한다.
    fun toSurveySchema(request: AdminSurveyCreateRequest): String {
        val sectionsJson = request.sections.mapIndexed { sectionIndex, section ->
            val questionsJson = section.questions.mapIndexed { questionIndex, question ->
                val optionsJson = question.options.mapIndexed { optionIndex, option ->
                    """
                    {
                      "sort": ${optionIndex + 1},
                      "label": "${escapeJson(option.optionLabel)}",
                      "score": ${option.optionScore}
                    }
                    """.trimIndent()
                }.joinToString(prefix = "[", postfix = "]", separator = ",")

                """
                {
                  "sort": ${questionIndex + 1},
                  "type": "${question.questionType}",
                  "title": "${escapeJson(question.title)}",
                  "score": ${question.score},
                  "options": $optionsJson
                }
                """.trimIndent()
            }.joinToString(prefix = "[", postfix = "]", separator = ",")

            """
            {
              "sort": ${sectionIndex + 1},
              "title": "${escapeJson(section.title)}",
              "targetAverageScore": ${section.targetAverageScore ?: "null"},
              "questions": $questionsJson
            }
            """.trimIndent()
        }.joinToString(prefix = "[", postfix = "]", separator = ",")

        return """
        {
          "title": "${escapeJson(request.title)}",
          "category": ${request.category?.let { "\"${escapeJson(it)}\"" } ?: "null"},
          "description": ${request.description?.let { "\"${escapeJson(it)}\"" } ?: "null"},
          "status": "${request.status}",
          "maxScore": ${request.maxScore},
          "estimatedTimeSec": ${request.estimatedTimeSec},
          "sections": $sectionsJson
        }
        """.trimIndent()
    }

    private fun escapeJson(value: String): String =
        value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
}
