CREATE TABLE admin_account (
    admin_id UUID PRIMARY KEY,
    admin_code_hash VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ
);

COMMENT ON TABLE admin_account IS '관리자 계정 테이블 - 관리자 코드 기반 접근 정보를 관리';
COMMENT ON COLUMN admin_account.admin_id IS 'PK - 관리자 UUID, 애플리케이션에서 UUIDv7로 생성';
COMMENT ON COLUMN admin_account.admin_code_hash IS '관리자 접근 코드 해시값';
COMMENT ON COLUMN admin_account.is_active IS '관리자 계정 활성 여부';
COMMENT ON COLUMN admin_account.created_at IS '생성일시';
COMMENT ON COLUMN admin_account.updated_at IS '수정일시';

CREATE INDEX ix_admin_account_is_active
ON admin_account(is_active);
