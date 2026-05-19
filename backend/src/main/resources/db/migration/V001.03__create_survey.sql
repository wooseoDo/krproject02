CREATE TABLE survey (
    survey_id UUID PRIMARY KEY,
    survey_version INT NOT NULL DEFAULT 1,
    title VARCHAR(200) NOT NULL,
    category VARCHAR(100),
    description TEXT,
    status VARCHAR(20) NOT NULL
        CHECK (status IN ('DRAFT', 'PUBLISHED', 'LOCKED', 'CLOSED')),
    max_score INT NOT NULL CHECK (max_score > 0),
    estimated_time_sec INT CHECK (estimated_time_sec IS NULL OR estimated_time_sec > 0),
    survey_schema JSONB NOT NULL,
    created_by UUID REFERENCES user_account(user_id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_by UUID REFERENCES user_account(user_id),
    updated_at TIMESTAMPTZ,
    locked_by UUID REFERENCES user_account(user_id),
    locked_at TIMESTAMPTZ,
    deleted_by UUID REFERENCES user_account(user_id),
    deleted_at TIMESTAMPTZ,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

COMMENT ON TABLE survey IS '조사지 테이블 - 조사지 기본 정보와 JSONB 원본 구조를 관리';
COMMENT ON COLUMN survey.survey_id IS 'PK - 조사지 UUID, 애플리케이션에서 UUIDv7로 생성';
COMMENT ON COLUMN survey.survey_version IS '낙관적 락 및 변경 이력용 버전';
COMMENT ON COLUMN survey.title IS '조사지명 - 목록 조회 및 검색용 직렬화 컬럼';
COMMENT ON COLUMN survey.category IS '조사지 카테고리 - 검색용 직렬화 컬럼';
COMMENT ON COLUMN survey.description IS '조사지 설명';
COMMENT ON COLUMN survey.status IS '조사지 상태 - DRAFT, PUBLISHED, LOCKED, CLOSED';
COMMENT ON COLUMN survey.max_score IS '조사지 최대 배점 - 문항 배점 합계 제한 기준';
COMMENT ON COLUMN survey.estimated_time_sec IS '예상 소요 시간, 초 단위';
COMMENT ON COLUMN survey.survey_schema IS '조사지 전체 구조 JSONB - 중분류, 문항, 선택지, 배점 원본 데이터';
COMMENT ON COLUMN survey.created_by IS '생성자 사용자 UUID';
COMMENT ON COLUMN survey.created_at IS '생성일시';
COMMENT ON COLUMN survey.updated_by IS '최종 수정자 사용자 UUID';
COMMENT ON COLUMN survey.updated_at IS '최종 수정일시';
COMMENT ON COLUMN survey.locked_by IS '잠금 처리자 사용자 UUID';
COMMENT ON COLUMN survey.locked_at IS '잠금 처리일시';
COMMENT ON COLUMN survey.deleted_by IS '삭제 처리자 사용자 UUID';
COMMENT ON COLUMN survey.deleted_at IS '삭제일시';
COMMENT ON COLUMN survey.is_deleted IS '논리 삭제 여부';

CREATE INDEX ix_survey_created_at
ON survey(created_at DESC)
WHERE is_deleted = FALSE;

CREATE INDEX ix_survey_admin_list_cover
ON survey(created_at DESC, survey_id DESC)
INCLUDE (title, survey_version, status, max_score, category, estimated_time_sec)
WHERE is_deleted = FALSE;

CREATE INDEX ix_survey_normal_list_cover
ON survey(created_at DESC, survey_id DESC)
INCLUDE (title, survey_version, status, max_score, category, estimated_time_sec)
WHERE is_deleted = FALSE
  AND status NOT IN ('LOCKED', 'CLOSED');

CREATE INDEX ix_survey_status
ON survey(status)
WHERE is_deleted = FALSE;

CREATE INDEX ix_survey_title
ON survey(title)
WHERE is_deleted = FALSE;

CREATE INDEX ix_survey_category
ON survey(category)
WHERE is_deleted = FALSE;

CREATE INDEX ix_survey_updated_at
ON survey(updated_at DESC)
WHERE is_deleted = FALSE;

CREATE INDEX ix_survey_schema_gin
ON survey USING GIN (survey_schema);
