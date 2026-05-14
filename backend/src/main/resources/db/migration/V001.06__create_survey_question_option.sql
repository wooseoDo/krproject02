CREATE TABLE survey_question_option (
    option_id UUID PRIMARY KEY,
    question_id UUID NOT NULL REFERENCES survey_question(question_id),
    option_sort INT NOT NULL CHECK (option_sort BETWEEN 1 AND 5),
    option_label VARCHAR(500) NOT NULL,
    option_score INT NOT NULL CHECK (option_score >= 0)
);

COMMENT ON TABLE survey_question_option IS '조사지 문항 선택지 테이블 - 문항별 선택지와 점수를 관리';
COMMENT ON COLUMN survey_question_option.option_id IS 'PK - 선택지 UUID, 애플리케이션에서 UUIDv7로 생성';
COMMENT ON COLUMN survey_question_option.question_id IS 'FK - 문항 UUID';
COMMENT ON COLUMN survey_question_option.option_sort IS '선택지 정렬 순서, 1~5';
COMMENT ON COLUMN survey_question_option.option_label IS '선택지 텍스트';
COMMENT ON COLUMN survey_question_option.option_score IS '선택지 선택 시 부여 점수';

CREATE UNIQUE INDEX uq_survey_question_option_question_sort
ON survey_question_option(question_id, option_sort);

CREATE INDEX ix_survey_question_option_question_id
ON survey_question_option(question_id);
