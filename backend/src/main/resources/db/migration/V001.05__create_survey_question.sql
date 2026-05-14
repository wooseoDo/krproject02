CREATE TABLE survey_question (
    question_id UUID PRIMARY KEY,
    survey_id UUID NOT NULL REFERENCES survey(survey_id),
    section_id UUID NOT NULL REFERENCES survey_section(section_id),
    question_sort INT NOT NULL,
    question_type VARCHAR(30) NOT NULL
        CHECK (question_type IN ('MULTIPLE_CHOICE', 'LIKERT', 'RADIO')),
    title VARCHAR(500) NOT NULL,
    score INT NOT NULL CHECK (score > 0),
    option_count INT NOT NULL CHECK (option_count BETWEEN 2 AND 5),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ
);

COMMENT ON TABLE survey_question IS '조사지 문항 테이블 - 응답 검증과 통계 계산을 위한 직렬화 문항 정보';
COMMENT ON COLUMN survey_question.question_id IS 'PK - 문항 UUID, 애플리케이션에서 UUIDv7로 생성';
COMMENT ON COLUMN survey_question.survey_id IS 'FK - 조사지 UUID';
COMMENT ON COLUMN survey_question.section_id IS 'FK - 중분류 UUID';
COMMENT ON COLUMN survey_question.question_sort IS '조사지 내 문항 정렬 순서';
COMMENT ON COLUMN survey_question.question_type IS '문항 유형 - MULTIPLE_CHOICE, LIKERT, RADIO';
COMMENT ON COLUMN survey_question.title IS '문항 제목';
COMMENT ON COLUMN survey_question.score IS '문항 배점';
COMMENT ON COLUMN survey_question.option_count IS '선택지 개수, 2개 이상 5개 이하';
COMMENT ON COLUMN survey_question.created_at IS '생성일시';
COMMENT ON COLUMN survey_question.updated_at IS '수정일시';

CREATE UNIQUE INDEX uq_survey_question_survey_sort
ON survey_question(survey_id, question_sort);

CREATE INDEX ix_survey_question_survey_id
ON survey_question(survey_id);

CREATE INDEX ix_survey_question_section_id
ON survey_question(section_id);

CREATE INDEX ix_survey_question_type
ON survey_question(question_type);
