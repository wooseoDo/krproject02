INSERT INTO survey_response_answer (
    answer_id,
    response_id,
    survey_id,
    section_id,
    question_id,
    section_sort,
    question_sort,
    option_sort,
    score,
    snapshot_section_title,
    snapshot_question_title,
    snapshot_question_type,
    snapshot_option_label
)
SELECT
    ('019b1000-0000-7000-8000-' || lpad((500000 + row_number() OVER ())::text, 12, '0'))::uuid,
    r.response_id,
    q.survey_id,
    q.section_id,
    q.question_id,
    s.section_sort,
    q.question_sort,
    CASE
        WHEN r.user_id = '019b1000-0000-7000-8000-000000000101'
            THEN (ARRAY[4,4,3,2,5,4])[q.question_sort]
        WHEN r.user_id = '019b1000-0000-7000-8000-000000000102'
            THEN (ARRAY[3,3,3,2,4,3])[q.question_sort]
        ELSE (ARRAY[5,4,4,3,5,4])[q.question_sort]
    END AS option_sort,
    CASE
        WHEN r.user_id = '019b1000-0000-7000-8000-000000000101'
            THEN (ARRAY[4,4,3,2,5,4])[q.question_sort]
        WHEN r.user_id = '019b1000-0000-7000-8000-000000000102'
            THEN (ARRAY[3,3,3,2,4,3])[q.question_sort]
        ELSE (ARRAY[5,4,4,3,5,4])[q.question_sort]
    END AS score,
    s.title,
    q.title,
    q.question_type,
    CASE
        WHEN r.user_id = '019b1000-0000-7000-8000-000000000101'
            THEN (ARRAY['높음','높음','보통','낮음','매우 높음','높음'])[q.question_sort]
        WHEN r.user_id = '019b1000-0000-7000-8000-000000000102'
            THEN (ARRAY['보통','보통','보통','낮음','높음','보통'])[q.question_sort]
        ELSE (ARRAY['매우 높음','높음','높음','보통','매우 높음','높음'])[q.question_sort]
    END AS snapshot_option_label
FROM survey_response r
JOIN survey_question q ON q.survey_id = r.survey_id
JOIN survey_section s ON s.section_id = q.section_id
WHERE r.survey_id = '019b1000-0000-7000-8000-000000001001';
