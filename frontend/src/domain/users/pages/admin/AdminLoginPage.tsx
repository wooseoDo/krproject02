import { LoginForm } from '../../components/shared/LoginForm';
import { submitAdminUserLogin } from '../../hooks/admin/useAdminUserLogin';

export default function AdminLoginPage() {
  return (
    <LoginForm
      scope="admin"
      title="관리자 로그인"
      description="등록된 관리자 계정만 접근할 수 있습니다. 없는 계정은 신규 등록되지 않습니다."
      submitLabel="관리자 입장"
      onSubmit={submitAdminUserLogin}
    />
  );
}
