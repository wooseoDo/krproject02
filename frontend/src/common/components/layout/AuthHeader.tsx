interface AuthHeaderProps {
  accountType: string;
  birthDate: string;
  accountInfo: string;
  dashboardPath: string;
  surveyPath: string;
  onLogout: () => void;
}

export function AuthHeader({
  accountType,
  birthDate,
  accountInfo,
  dashboardPath,
  surveyPath,
  onLogout,
}: AuthHeaderProps) {
  return (
    <header className="auth-header">
      <div className="auth-header__summary" aria-label="계정 정보">
        <span>
          계정구분 <strong>{accountType}</strong>
        </span>
        <span>
          생년월일 <strong>{birthDate}</strong>
        </span>
        <span>
          계정정보 <strong>{accountInfo}</strong>
        </span>
      </div>
      <nav className="auth-header__nav" aria-label="주요 메뉴">
        <a href={dashboardPath}>대시보드</a>
        <a href={surveyPath}>조사지</a>
        <button type="button" onClick={onLogout}>
          로그아웃
        </button>
      </nav>
    </header>
  );
}
