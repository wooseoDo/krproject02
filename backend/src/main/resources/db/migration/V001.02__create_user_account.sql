CREATE TABLE user_account (
    user_id UUID PRIMARY KEY,
    birth_date DATE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    last_login_at TIMESTAMPTZ,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMPTZ
);

COMMENT ON TABLE user_account IS '사용자 계정 테이블 - 생년월일과 비밀번호 기반 간이 계정';
COMMENT ON COLUMN user_account.user_id IS 'PK - 사용자 UUID, 애플리케이션에서 UUIDv7로 생성';
COMMENT ON COLUMN user_account.birth_date IS '사용자 생년월일';
COMMENT ON COLUMN user_account.password_hash IS '사용자 비밀번호 해시값';
COMMENT ON COLUMN user_account.created_at IS '계정 생성일시';
COMMENT ON COLUMN user_account.last_login_at IS '마지막 로그인 일시';
COMMENT ON COLUMN user_account.is_deleted IS '논리 삭제 여부';
COMMENT ON COLUMN user_account.deleted_at IS '삭제일시';

CREATE INDEX ix_user_account_birth_date
ON user_account(birth_date)
WHERE is_deleted = FALSE;

CREATE INDEX ix_user_account_created_at
ON user_account(created_at DESC)
WHERE is_deleted = FALSE;
