CREATE TABLE survey_section (
    section_id UUID PRIMARY KEY,
    survey_id UUID NOT NULL REFERENCES survey(survey_id),
    section_sort INT NOT NULL,
    title VARCHAR(200) NOT NULL,
    target_average_score NUMERIC(6,2),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ
);

COMMENT ON TABLE survey_section IS '조사지 중분류 테이블 - 통계 집계와 기준 평균 비교를 위한 직렬화 테이블';
COMMENT ON COLUMN survey_section.section_id IS 'PK - 중분류 UUID, 애플리케이션에서 UUIDv7로 생성';
COMMENT ON COLUMN survey_section.survey_id IS 'FK - 조사지 UUID';
COMMENT ON COLUMN survey_section.section_sort IS '조사지 내 중분류 정렬 순서';
COMMENT ON COLUMN survey_section.title IS '중분류명';
COMMENT ON COLUMN survey_section.target_average_score IS '중분류별 적정 평균 점수';
COMMENT ON COLUMN survey_section.created_at IS '생성일시';
COMMENT ON COLUMN survey_section.updated_at IS '수정일시';

CREATE UNIQUE INDEX uq_survey_section_survey_sort
ON survey_section(survey_id, section_sort);

CREATE INDEX ix_survey_section_survey_id
ON survey_section(survey_id);

CREATE INDEX ix_survey_section_title
ON survey_section(title);
