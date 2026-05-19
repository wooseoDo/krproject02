package kr.krproject02.domain.survey.repository

import kr.krproject02.domain.survey.entity.Survey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.OffsetDateTime
import java.util.UUID

interface SurveyRepository : JpaRepository<Survey, UUID> {
    fun findBySurveyIdAndDeletedFalse(surveyId: UUID): Survey?

    fun findBySurveyIdAndLatestTrueAndDeletedFalse(surveyId: UUID): Survey?

    @Query(
        value = """
            SELECT
                survey_id AS "surveyId",
                title AS title,
                survey_version AS "surveyVersion",
                status AS status,
                max_score AS "maxScore",
                category AS category,
                estimated_time_sec AS "estimatedTimeSec",
                created_at AS "createdAt"
            FROM survey
            WHERE is_deleted = FALSE
              AND is_latest = TRUE
              AND (:normalOnly = FALSE OR status NOT IN ('LOCKED', 'CLOSED', 'DRAFT'))
              AND (CAST(:title AS TEXT) IS NULL OR LOWER(title) LIKE LOWER(CONCAT('%', CAST(:title AS TEXT), '%')))
              AND (CAST(:maxScore AS INTEGER) IS NULL OR max_score = CAST(:maxScore AS INTEGER))
              AND (CAST(:estimatedTimeSec AS INTEGER) IS NULL OR estimated_time_sec = CAST(:estimatedTimeSec AS INTEGER))
              AND (CAST(:surveyVersion AS INTEGER) IS NULL OR survey_version = CAST(:surveyVersion AS INTEGER))
              AND (CAST(:status AS TEXT) IS NULL OR status = CAST(:status AS TEXT))
              AND (CAST(:releasedAtFrom AS TIMESTAMPTZ) IS NULL OR created_at >= CAST(:releasedAtFrom AS TIMESTAMPTZ))
              AND (CAST(:releasedAtTo AS TIMESTAMPTZ) IS NULL OR created_at < CAST(:releasedAtTo AS TIMESTAMPTZ))
            ORDER BY created_at DESC, survey_id DESC
            LIMIT :size OFFSET :offset
        """,
        nativeQuery = true,
    )
    fun findSurveyListPage(
        @Param("normalOnly") normalOnly: Boolean,
        @Param("title") title: String?,
        @Param("maxScore") maxScore: Int?,
        @Param("estimatedTimeSec") estimatedTimeSec: Int?,
        @Param("surveyVersion") surveyVersion: Int?,
        @Param("status") status: String?,
        @Param("releasedAtFrom") releasedAtFrom: OffsetDateTime?,
        @Param("releasedAtTo") releasedAtTo: OffsetDateTime?,
        @Param("size") size: Int,
        @Param("offset") offset: Int,
    ): List<SurveyListItemProjection>

    @Query(
        value = """
            SELECT COUNT(*)
            FROM survey
            WHERE is_deleted = FALSE
              AND is_latest = TRUE
              AND (:normalOnly = FALSE OR status NOT IN ('LOCKED', 'CLOSED', 'DRAFT'))
              AND (CAST(:title AS TEXT) IS NULL OR LOWER(title) LIKE LOWER(CONCAT('%', CAST(:title AS TEXT), '%')))
              AND (CAST(:maxScore AS INTEGER) IS NULL OR max_score = CAST(:maxScore AS INTEGER))
              AND (CAST(:estimatedTimeSec AS INTEGER) IS NULL OR estimated_time_sec = CAST(:estimatedTimeSec AS INTEGER))
              AND (CAST(:surveyVersion AS INTEGER) IS NULL OR survey_version = CAST(:surveyVersion AS INTEGER))
              AND (CAST(:status AS TEXT) IS NULL OR status = CAST(:status AS TEXT))
              AND (CAST(:releasedAtFrom AS TIMESTAMPTZ) IS NULL OR created_at >= CAST(:releasedAtFrom AS TIMESTAMPTZ))
              AND (CAST(:releasedAtTo AS TIMESTAMPTZ) IS NULL OR created_at < CAST(:releasedAtTo AS TIMESTAMPTZ))
        """,
        nativeQuery = true,
    )
    fun countSurveyListPage(
        @Param("normalOnly") normalOnly: Boolean,
        @Param("title") title: String?,
        @Param("maxScore") maxScore: Int?,
        @Param("estimatedTimeSec") estimatedTimeSec: Int?,
        @Param("surveyVersion") surveyVersion: Int?,
        @Param("status") status: String?,
        @Param("releasedAtFrom") releasedAtFrom: OffsetDateTime?,
        @Param("releasedAtTo") releasedAtTo: OffsetDateTime?,
    ): Long
}
