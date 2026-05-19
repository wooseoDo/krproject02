import { useEffect, useState, type SetStateAction } from 'react';
import { AuthHeader } from '../../../../common/components/layout/AuthHeader';
import { clearAuthenticatedUser, readAuthenticatedUser } from '../../../users/model/authSession';
import { SurveyCreateForm } from '../../components/admin/SurveyCreateForm';
import { SurveyCreateReviewModal } from '../../components/admin/SurveyCreateReviewModal';
import { fetchAdminSurveyDetail } from '../../api/admin/api';
import { mapAdminSurveyDetailToDraft } from '../../config/surveyCreateConfig';
import { SURVEY_MESSAGES } from '../../constants/messages';
import { useAdminSurveyUpdateMutation } from '../../hooks/admin/useAdminSurveyUpdateMutation';
import { validateSurveyCreateDraft } from '../../model/createPayload';
import type { AdminSurveyCreateDraft } from '../../types/types';

function getSurveyIdFromPath() {
  const match = window.location.pathname.match(/^\/admin\/surveys\/([^/]+)\/edit$/);
  return match?.[1] ?? '';
}

function navigateToAdminSurveys() {
  window.history.pushState({}, '', '/admin/surveys');
  window.dispatchEvent(new PopStateEvent('popstate'));
}

export default function AdminSurveyEditPage() {
  const user = readAuthenticatedUser();
  const surveyId = getSurveyIdFromPath();
  const [draft, setDraft] = useState<AdminSurveyCreateDraft | null>(null);
  const [errors, setErrors] = useState<string[]>([]);
  const [loadError, setLoadError] = useState('');
  const [isLoading, setIsLoading] = useState(true);
  const [reviewOpen, setReviewOpen] = useState(false);
  const [reviewSectionIndex, setReviewSectionIndex] = useState(0);
  const { isSubmitting, submitMessage, setSubmitMessage, submitSurveyUpdate } = useAdminSurveyUpdateMutation({
    surveyId,
    onSuccess: navigateToAdminSurveys,
  });

  useEffect(() => {
    let ignore = false;

    const loadSurveyDetail = async () => {
      if (!surveyId) {
        setLoadError(SURVEY_MESSAGES.DETAIL_LOAD_ERROR);
        setIsLoading(false);
        return;
      }

      setIsLoading(true);
      setLoadError('');

      try {
        const detail = await fetchAdminSurveyDetail(surveyId);

        if (!ignore) {
          setDraft(mapAdminSurveyDetailToDraft(detail));
        }
      } catch (error) {
        if (!ignore) {
          setLoadError(error instanceof Error ? error.message : SURVEY_MESSAGES.DETAIL_LOAD_ERROR);
        }
      } finally {
        if (!ignore) {
          setIsLoading(false);
        }
      }
    };

    void loadSurveyDetail();

    return () => {
      ignore = true;
    };
  }, [surveyId]);

  const handleLogout = () => {
    clearAuthenticatedUser();
    window.history.pushState({}, '', '/admin/login');
    window.dispatchEvent(new PopStateEvent('popstate'));
  };

  const openReview = () => {
    if (!draft) {
      return;
    }

    const nextErrors = validateSurveyCreateDraft(draft);
    setErrors(nextErrors);

    if (nextErrors.length === 0) {
      setReviewSectionIndex(0);
      setReviewOpen(true);
    }
  };

  const moveReviewSection = (nextIndex: number) => {
    if (!draft) {
      return;
    }

    setReviewSectionIndex(Math.min(Math.max(nextIndex, 0), draft.sections.length - 1));
  };

  const completeSurveyUpdate = async () => {
    if (!draft) {
      return;
    }

    const nextErrors = validateSurveyCreateDraft(draft);
    setErrors(nextErrors);

    if (nextErrors.length > 0) {
      setReviewOpen(false);
      return;
    }

    try {
      await submitSurveyUpdate(draft);
      setReviewOpen(false);
    } catch {
      // The mutation hook owns the user-facing submit message.
    }
  };

  const setLoadedDraft = (nextDraft: SetStateAction<AdminSurveyCreateDraft>) => {
    setDraft((current) => {
      if (!current) {
        return current;
      }

      return typeof nextDraft === 'function' ? nextDraft(current) : nextDraft;
    });
  };

  return (
    <main className="survey-page-shell">
      <AuthHeader
        accountType="Admin"
        birthDate={user?.birthDate ?? '-'}
        accountInfo={user?.createdAt ? new Date(user.createdAt).toLocaleDateString('ko-KR') : '-'}
        dashboardPath="/admin/dashboard"
        surveyPath="/admin/surveys"
        onLogout={handleLogout}
      />

      <section className="survey-page-header">
        <p className="section-label">SURVEYS</p>
        <h1>Survey Edit</h1>
        <p>Update the survey details, sections, questions, and options.</p>
      </section>

      <section className="survey-create-panel" aria-label="Admin survey edit">
        {isLoading && <p className="form-notice">Loading survey...</p>}
        {loadError && (
          <p className="form-notice" role="alert">
            {loadError}
          </p>
        )}
        {draft && (
          <SurveyCreateForm
            draft={draft}
            errors={errors}
            submitMessage={submitMessage}
            setDraft={setLoadedDraft}
            onCancel={navigateToAdminSurveys}
            onOpenReview={openReview}
            onClearSubmitMessage={() => setSubmitMessage('')}
            sectionLabel="EDIT"
            title="Edit Survey"
            cancelLabel="Back"
            reviewButtonLabel="Save Changes"
          />
        )}
      </section>

      {draft && reviewOpen && (
        <SurveyCreateReviewModal
          draft={draft}
          sectionIndex={reviewSectionIndex}
          isSubmitting={isSubmitting}
          onClose={() => setReviewOpen(false)}
          onMoveSection={moveReviewSection}
          onComplete={completeSurveyUpdate}
        />
      )}
    </main>
  );
}
