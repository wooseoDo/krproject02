INSERT INTO survey_version_history (
    history_id,
    survey_id,
    survey_version,
    change_type,
    before_snapshot,
    after_snapshot,
    change_reason,
    changed_by
) VALUES
    (
        '019b1000-0000-7000-8000-000000006001',
        '019b1000-0000-7000-8000-000000001001',
        1,
        'CREATE',
        NULL,
        (SELECT survey_schema FROM survey WHERE survey_id = '019b1000-0000-7000-8000-000000001001'),
        '초기 샘플 조사지 생성',
        '019b1000-0000-7000-8000-000000000001'
    ),
    (
        '019b1000-0000-7000-8000-000000006002',
        '019b1000-0000-7000-8000-000000001002',
        1,
        'CREATE',
        NULL,
        (SELECT survey_schema FROM survey WHERE survey_id = '019b1000-0000-7000-8000-000000001002'),
        '잠금 상태 샘플 조사지 생성',
        '019b1000-0000-7000-8000-000000000001'
    );
