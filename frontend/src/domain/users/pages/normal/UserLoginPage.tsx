import { LoginForm } from '../../components/shared/LoginForm';
import { submitNormalUserLogin } from '../../hooks/normal/useNormalUserLogin';

export default function UserLoginPage() {
  return (
    <LoginForm
      scope="normal"
      title="사용자 로그인"
      description="생년월일과 비밀번호로 입장합니다. 기존 계정이 없으면 같은 정보로 신규 계정이 등록됩니다."
      submitLabel="사용자 입장"
      onSubmit={submitNormalUserLogin}
    />
  );
}
