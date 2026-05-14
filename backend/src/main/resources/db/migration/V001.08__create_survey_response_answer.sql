CREATE TABLE survey_response_answer (
    answer_id UUID PRIMARY KEY,
    response_id UUID NOT NULL REFERENCES survey_response(response_id),
    survey_id UUID NOT NULL REFERENCES survey(survey_id),
    section_id UUID NOT NULL REFERENCES survey_section(section_id),
    question_id UUID NOT NULL REFERENCES survey_question(question_id),
    option_id UUID REFERENCES survey_question_option(option_id),
    section_sort INT NOT NULL,
    question_sort INT NOT NULL,
    option_sort INT NOT NULL CHECK (option_sort BETWEEN 1 AND 5),
    score NUMERIC(6,2) NOT NULL,
    snapshot_section_title VARCHAR(200),
    snapshot_question_title VARCHAR(500),
    snapshot_question_type VARCHAR(30),
    snapshot_option_label VARCHAR(500),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

COMMENT ON TABLE survey_response_answer IS '조사지 응답 상세 테이블 - 문항별 제출 답변과 응답 당시 스냅샷을 관리';
COMMENT ON COLUMN survey_response_answer.answer_id IS 'PK - 답변 UUID, 애플리케이션에서 UUIDv7로 생성';
COMMENT ON COLUMN survey_response_answer.response_id IS 'FK - 응답 헤더 UUID';
COMMENT ON COLUMN survey_response_answer.survey_id IS 'FK - 조사지 UUID';
COMMENT ON COLUMN survey_response_answer.section_id IS 'FK - 중분류 UUID';
COMMENT ON COLUMN survey_response_answer.question_id IS 'FK - 문항 UUID';
COMMENT ON COLUMN survey_response_answer.option_id IS 'FK - 선택지 UUID';
COMMENT ON COLUMN survey_response_answer.section_sort IS '응답 당시 중분류 정렬 순서';
COMMENT ON COLUMN survey_response_answer.question_sort IS '응답 당시 문항 정렬 순서';
COMMENT ON COLUMN survey_response_answer.option_sort IS '응답자가 선택한 선택지 정렬 순서';
COMMENT ON COLUMN survey_response_answer.score IS '문항별 획득 점수';
COMMENT ON COLUMN survey_response_answer.snapshot_section_title IS '응답 당시 중분류명 스냅샷';
COMMENT ON COLUMN survey_response_answer.snapshot_question_title IS '응답 당시 문항명 스냅샷';
COMMENT ON COLUMN survey_response_answer.snapshot_question_type IS '응답 당시 문항 유형 스냅샷';
COMMENT ON COLUMN survey_response_answer.snapshot_option_label IS '응답 당시 선택지명 스냅샷';
COMMENT ON COLUMN survey_response_answer.created_at IS '생성일시';

CREATE UNIQUE INDEX uq_survey_response_answer_question
ON survey_response_answer(response_id, question_id);

CREATE INDEX ix_survey_response_answer_response_id
ON survey_response_answer(response_id);

CREATE INDEX ix_survey_response_answer_survey_id
ON survey_response_answer(survey_id);

CREATE INDEX ix_survey_response_answer_section_id
ON survey_response_answer(section_id);

CREATE INDEX ix_survey_response_answer_question_sort
ON survey_response_answer(response_id, question_sort);

CREATE INDEX ix_survey_response_answer_stats
ON survey_response_answer(survey_id, section_id, score);
