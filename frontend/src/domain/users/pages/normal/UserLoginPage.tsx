import { LoginForm } from '../../components/shared/LoginForm';
import { USER_MESSAGES } from '../../constants/messages';
import { submitNormalUserLogin } from '../../hooks/normal/useNormalUserLogin';

export default function UserLoginPage() {
  return (
    <LoginForm
      variant="normal"
      titleId="normal-login-title"
      sectionLabel="USER ACCESS"
      title="사용자 로그인"
      description="생년월일과 비밀번호로 입장합니다. 기존 계정이 없으면 같은 정보로 신규 계정이 등록됩니다."
      submitLabel="사용자 입장"
      submittingLabel="확인 중"
      successPath="/dashboard"
      errorMessage={USER_MESSAGES.NORMAL_LOGIN_ERROR}
      onSubmit={submitNormalUserLogin}
    />
  );
}
