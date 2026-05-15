INSERT INTO survey_question_option (
    option_id,
    question_id,
    option_sort,
    option_label,
    option_score
)
SELECT
    ('019b1000-0000-7000-8000-' || lpad((300000 + row_number() OVER ())::text, 12, '0'))::uuid,
    q.question_id,
    v.option_sort,
    v.option_label,
    v.option_score
FROM survey_question q
CROSS JOIN (
    VALUES
        (1, '매우 낮음', 1),
        (2, '낮음', 2),
        (3, '보통', 3),
        (4, '높음', 4),
        (5, '매우 높음', 5)
) AS v(option_sort, option_label, option_score);
