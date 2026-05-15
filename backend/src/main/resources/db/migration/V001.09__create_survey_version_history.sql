CREATE TABLE survey_version_history (
    history_id UUID PRIMARY KEY,
    survey_id UUID NOT NULL REFERENCES survey(survey_id),
    survey_version INT NOT NULL,
    change_type VARCHAR(30) NOT NULL
        CHECK (change_type IN ('CREATE', 'UPDATE', 'COPY', 'LOCK', 'UNLOCK', 'DELETE', 'PUBLISH', 'CLOSE')),
    before_snapshot JSONB,
    after_snapshot JSONB,
    change_reason TEXT,
    changed_by UUID REFERENCES user_account(user_id),
    changed_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

COMMENT ON TABLE survey_version_history IS '조사지 변경 이력 테이블 - 생성, 수정, 복사, 잠금, 삭제, 배포 이력을 관리';
COMMENT ON COLUMN survey_version_history.history_id IS 'PK - 이력 UUID, 애플리케이션에서 UUIDv7로 생성';
COMMENT ON COLUMN survey_version_history.survey_id IS 'FK - 조사지 UUID';
COMMENT ON COLUMN survey_version_history.survey_version IS '변경 시점의 조사지 버전';
COMMENT ON COLUMN survey_version_history.change_type IS '변경 유형';
COMMENT ON COLUMN survey_version_history.before_snapshot IS '변경 전 조사지 JSONB 스냅샷';
COMMENT ON COLUMN survey_version_history.after_snapshot IS '변경 후 조사지 JSONB 스냅샷';
COMMENT ON COLUMN survey_version_history.change_reason IS '변경 사유';
COMMENT ON COLUMN survey_version_history.changed_by IS '변경 처리 사용자 UUID';
COMMENT ON COLUMN survey_version_history.changed_at IS '변경 처리일시';

CREATE INDEX ix_survey_version_history_survey_id
ON survey_version_history(survey_id);

CREATE INDEX ix_survey_version_history_changed_at
ON survey_version_history(changed_at DESC);

CREATE INDEX ix_survey_version_history_change_type
ON survey_version_history(change_type);

CREATE INDEX ix_survey_version_history_after_snapshot_gin
ON survey_version_history USING GIN (after_snapshot);
