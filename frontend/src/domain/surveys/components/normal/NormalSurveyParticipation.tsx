import { useState } from 'react';
import { AuthHeader } from '../../../../common/components/layout/AuthHeader';
import { clearAuthenticatedUser, readAuthenticatedUser } from '../../../users/model/authSession';
import { useNormalSurveyParticipation } from '../../hooks/normal/useNormalSurveyParticipation';
import { SurveyProgressBar } from './SurveyProgressBar';
import { SurveyQuestionPanel } from './SurveyQuestionPanel';
import { SurveySubmitConfirmModal } from './SurveySubmitConfirmModal';
import { SurveyTimer } from './SurveyTimer';

interface NormalSurveyParticipationProps {
  surveyId: string;
}

function navigateTo(path: string) {
  window.history.pushState({}, '', path);
  window.dispatchEvent(new PopStateEvent('popstate'));
}

export function NormalSurveyParticipation({ surveyId }: NormalSurveyParticipationProps) {
  const user = readAuthenticatedUser();

  if (!user?.userId) {
    navigateTo('/login');
    return null;
  }

  return <NormalSurveyParticipationContent surveyId={surveyId} userId={user.userId} />;
}

interface NormalSurveyParticipationContentProps {
  surveyId: string;
  userId: string;
}

function NormalSurveyParticipationContent({ surveyId, userId }: NormalSurveyParticipationContentProps) {
  const user = readAuthenticatedUser();
  const [confirmOpen, setConfirmOpen] = useState(false);
  const participation = useNormalSurveyParticipation({
    surveyId,
    userId,
  });

  const handleLogout = () => {
    clearAuthenticatedUser();
    navigateTo('/login');
  };

  if (!user) {
    navigateTo('/login');
    return null;
  }

  if (participation.submitResult) {
    return (
      <main className="survey-page-shell">
        <AuthHeader
          accountType="일반 사용자"
          birthDate={user.birthDate}
          accountInfo={user.createdAt ? new Date(user.createdAt).toLocaleDateString('ko-KR') : '-'}
          dashboardPath="/dashboard"
          surveyPath="/surveys"
          onLogout={handleLogout}
        />
        <section className="survey-complete-panel">
          <p className="section-label">SUBMITTED</p>
          <h1>제출 완료</h1>
          <dl>
            <div>
              <dt>총점</dt>
              <dd>{participation.submitResult.totalScore ?? 0}</dd>
            </div>
            <div>
              <dt>소요 시간</dt>
              <dd>{participation.submitResult.elapsedTimeSec ?? 0}초</dd>
            </div>
          </dl>
          <button type="button" className="primary-action" onClick={() => navigateTo('/surveys')}>
            목록으로
          </button>
        </section>
      </main>
    );
  }

  return (
    <main className="survey-page-shell">
      <AuthHeader
        accountType="일반 사용자"
        birthDate={user.birthDate}
        accountInfo={user.createdAt ? new Date(user.createdAt).toLocaleDateString('ko-KR') : '-'}
        dashboardPath="/dashboard"
        surveyPath="/surveys"
        onLogout={handleLogout}
      />

      {participation.isLoading ? (
        <section className="table-state-message">조사지를 불러오는 중입니다.</section>
      ) : participation.error && !participation.detail ? (
        <section className="table-state-message is-error">{participation.error}</section>
      ) : participation.detail ? (
        <>
          <section className="survey-participation-header">
            <div>
              <p className="section-label">PARTICIPATION</p>
              <h1>{participation.detail.title}</h1>
              {participation.detail.description ? <p>{participation.detail.description}</p> : null}
            </div>
            <div className="survey-participation-stats" aria-label="참여 상태">
              <span>버전 {participation.detail.surveyVersion}</span>
              <span>
                <SurveyTimer elapsedTimeSec={participation.elapsedTimeSec} />
              </span>
            </div>
          </section>

          <SurveyProgressBar
            answeredCount={participation.answeredCount}
            totalQuestionCount={participation.totalQuestionCount}
          />

          <SurveyQuestionPanel
            detail={participation.detail}
            draft={participation.draft}
            onAnswerChange={participation.updateAnswer}
          />

          {participation.error ? <p className="form-notice">{participation.error}</p> : null}

          <div className="survey-participation-actions">
            <button type="button" className="secondary-action" onClick={() => navigateTo('/surveys')}>
              목록
            </button>
            <button
              type="button"
              className="primary-action"
              disabled={!participation.isComplete || participation.isSubmitting}
              onClick={() => setConfirmOpen(true)}
            >
              최종 제출
            </button>
          </div>

          <SurveySubmitConfirmModal
            open={confirmOpen}
            isSubmitting={participation.isSubmitting}
            answeredCount={participation.answeredCount}
            totalQuestionCount={participation.totalQuestionCount}
            onCancel={() => setConfirmOpen(false)}
            onConfirm={() => {
              setConfirmOpen(false);
              void participation.submit();
            }}
          />
        </>
      ) : null}
    </main>
  );
}
