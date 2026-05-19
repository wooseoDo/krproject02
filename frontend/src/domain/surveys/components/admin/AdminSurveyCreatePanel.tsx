import { useState } from 'react';
import { createInitialSurveyDraft } from '../../config/surveyCreateConfig';
import { useAdminSurveyCreateMutation } from '../../hooks/admin/useAdminSurveyCreateMutation';
import { validateSurveyCreateDraft } from '../../model/createPayload';
import {
  clearSurveyCreateDraft,
  readSurveyCreateDraft,
  saveSurveyCreateDraft,
} from '../../model/createSession';
import type { AdminSurveyCreateDraft } from '../../types/types';
import { SurveyCreateForm } from './SurveyCreateForm';
import { SurveyCreateReviewModal } from './SurveyCreateReviewModal';

interface AdminSurveyCreatePanelProps {
  onCreated: () => void;
  onCancel: () => void;
}

function createDraftState() {
  return readSurveyCreateDraft() ?? createInitialSurveyDraft();
}

export function AdminSurveyCreatePanel({ onCreated, onCancel }: AdminSurveyCreatePanelProps) {
  const [draft, setDraft] = useState<AdminSurveyCreateDraft>(createDraftState);
  const [errors, setErrors] = useState<string[]>([]);
  const [reviewOpen, setReviewOpen] = useState(false);
  const [reviewSectionIndex, setReviewSectionIndex] = useState(0);
  const { isSubmitting, submitMessage, setSubmitMessage, submitSurveyCreate } = useAdminSurveyCreateMutation({
    onSuccess: onCreated,
  });

  const openReview = () => {
    const nextErrors = validateSurveyCreateDraft(draft);
    setErrors(nextErrors);

    if (nextErrors.length === 0) {
      saveSurveyCreateDraft(draft);
      setReviewSectionIndex(0);
      setReviewOpen(true);
    }
  };

  const moveReviewSection = (nextIndex: number) => {
    saveSurveyCreateDraft(draft);
    setReviewSectionIndex(Math.min(Math.max(nextIndex, 0), draft.sections.length - 1));
  };

  const completeSurveyCreate = async () => {
    const nextErrors = validateSurveyCreateDraft(draft);
    setErrors(nextErrors);

    if (nextErrors.length > 0) {
      setReviewOpen(false);
      return;
    }

    try {
      await submitSurveyCreate(draft);
      clearSurveyCreateDraft();
      setDraft(createInitialSurveyDraft());
      setReviewOpen(false);
    } catch {
      // The mutation hook owns the user-facing submit message.
    }
  };

  return (
    <section className="survey-create-panel" aria-label="관리자 조사지 생성">
      <SurveyCreateForm
        draft={draft}
        errors={errors}
        submitMessage={submitMessage}
        setDraft={setDraft}
        onCancel={onCancel}
        onOpenReview={openReview}
        onClearSubmitMessage={() => setSubmitMessage('')}
      />

      {reviewOpen && (
        <SurveyCreateReviewModal
          draft={draft}
          sectionIndex={reviewSectionIndex}
          isSubmitting={isSubmitting}
          onClose={() => setReviewOpen(false)}
          onMoveSection={moveReviewSection}
          onComplete={completeSurveyCreate}
        />
      )}
    </section>
  );
}
