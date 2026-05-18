import { LoginForm } from '../../components/shared/LoginForm';
import { USER_MESSAGES } from '../../constants/messages';
import { submitAdminUserLogin } from '../../hooks/admin/useAdminUserLogin';

export default function AdminLoginPage() {
  return (
    <LoginForm
      variant="admin"
      titleId="admin-login-title"
      sectionLabel="ADMIN ACCESS"
      title="관리자 로그인"
      description="등록된 관리자 계정만 접근할 수 있습니다. 없는 계정은 신규 등록되지 않습니다."
      submitLabel="관리자 입장"
      submittingLabel="확인 중"
      successPath="/admin/dashboard"
      errorMessage={USER_MESSAGES.ADMIN_LOGIN_ERROR}
      onSubmit={submitAdminUserLogin}
    />
  );
}
