CREATE TABLE survey_response (
    response_id UUID PRIMARY KEY,
    survey_id UUID NOT NULL REFERENCES survey(survey_id),
    survey_group_id UUID NOT NULL,
    user_id UUID NOT NULL REFERENCES user_account(user_id),
    survey_version INT NOT NULL,
    survey_title VARCHAR(200) NOT NULL,
    started_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    submitted_at TIMESTAMPTZ,
    elapsed_time_sec INT,
    total_score NUMERIC(8,2),
    is_completed BOOLEAN NOT NULL DEFAULT FALSE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMPTZ
);

COMMENT ON TABLE survey_response IS '조사지 응답 헤더 테이블 - 사용자별 조사지 응답 제출 단위';
COMMENT ON COLUMN survey_response.response_id IS 'PK - 응답 UUID, 애플리케이션에서 UUIDv7로 생성';
COMMENT ON COLUMN survey_response.survey_group_id IS 'FK - 조사지 그룹 UUID';
COMMENT ON COLUMN survey_response.survey_id IS 'FK - 조사지 UUID';
COMMENT ON COLUMN survey_response.user_id IS 'FK - 사용자 UUID';
COMMENT ON COLUMN survey_response.survey_version IS '응답 당시 조사지 버전 스냅샷';
COMMENT ON COLUMN survey_response.survey_title IS '응답 당시 조사지명 스냅샷';
COMMENT ON COLUMN survey_response.started_at IS '응답 시작 일시';
COMMENT ON COLUMN survey_response.submitted_at IS '응답 제출 일시';
COMMENT ON COLUMN survey_response.elapsed_time_sec IS '응답 소요 시간, 초 단위';
COMMENT ON COLUMN survey_response.total_score IS '응답 총점';
COMMENT ON COLUMN survey_response.is_completed IS '응답 완료 여부';
COMMENT ON COLUMN survey_response.is_deleted IS '논리 삭제 여부';
COMMENT ON COLUMN survey_response.deleted_at IS '삭제 일시';

CREATE UNIQUE INDEX uq_survey_response_user_group_version
ON survey_response(user_id, survey_group_id, survey_version)
WHERE is_deleted = FALSE;

CREATE INDEX ix_survey_response_user_group_latest
ON survey_response(user_id, survey_group_id, submitted_at DESC)
WHERE is_completed = TRUE AND is_deleted = FALSE;

CREATE INDEX ix_survey_response_survey_id
ON survey_response(survey_id)
WHERE is_deleted = FALSE;

CREATE INDEX ix_survey_response_survey_group_id
ON survey_response(survey_group_id)
WHERE is_deleted = FALSE;

CREATE INDEX ix_survey_response_user_id
ON survey_response(user_id)
WHERE is_deleted = FALSE;

CREATE INDEX ix_survey_response_submitted_at
ON survey_response(submitted_at DESC)
WHERE is_deleted = FALSE;
