CREATE TABLE user_account (
    user_id UUID PRIMARY KEY,
    birth_date CHAR(6) NOT NULL
        CHECK (char_length(birth_date) = 6 AND birth_date ~ '^[0-9]{6}$'),
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL
        CHECK (role IN ('NORMAL', 'ADMIN')),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by UUID,
    updated_at TIMESTAMPTZ,
    updated_by UUID,
    last_login_at TIMESTAMPTZ,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMPTZ,
    deleted_by UUID,
    CONSTRAINT fk_user_account_created_by FOREIGN KEY (created_by)
        REFERENCES user_account(user_id) ON DELETE SET NULL,
    CONSTRAINT fk_user_account_updated_by FOREIGN KEY (updated_by)
        REFERENCES user_account(user_id) ON DELETE SET NULL,
    CONSTRAINT fk_user_account_deleted_by FOREIGN KEY (deleted_by)
        REFERENCES user_account(user_id) ON DELETE SET NULL
);

COMMENT ON TABLE user_account IS '통합 사용자 계정 테이블 - 일반 사용자와 관리자를 role로 구분';
COMMENT ON COLUMN user_account.user_id IS 'PK - 사용자 UUID, 애플리케이션에서 UUIDv7로 생성';
COMMENT ON COLUMN user_account.birth_date IS '사용자 생년월일 6자리, YYMMDD 형식';
COMMENT ON COLUMN user_account.password_hash IS 'BCrypt 비밀번호 해시값';
COMMENT ON COLUMN user_account.role IS '사용자 역할 - NORMAL, ADMIN';
COMMENT ON COLUMN user_account.is_active IS '계정 활성 여부';
COMMENT ON COLUMN user_account.created_at IS '계정 생성일시';
COMMENT ON COLUMN user_account.created_by IS '계정 생성자 사용자 UUID';
COMMENT ON COLUMN user_account.updated_at IS '계정 수정일시';
COMMENT ON COLUMN user_account.updated_by IS '계정 수정자 사용자 UUID';
COMMENT ON COLUMN user_account.last_login_at IS '마지막 로그인 일시';
COMMENT ON COLUMN user_account.is_deleted IS '논리 삭제 여부';
COMMENT ON COLUMN user_account.deleted_at IS '삭제일시';
COMMENT ON COLUMN user_account.deleted_by IS '삭제 처리자 사용자 UUID';

CREATE INDEX ix_user_account_birth_date
ON user_account(birth_date)
WHERE is_deleted = FALSE;

CREATE INDEX ix_user_account_created_at
ON user_account(created_at DESC)
WHERE is_deleted = FALSE;

CREATE INDEX ix_user_account_role
ON user_account(role)
WHERE is_deleted = FALSE;

CREATE INDEX ix_user_account_is_active
ON user_account(is_active)
WHERE is_deleted = FALSE;
