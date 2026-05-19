interface SurveySubmitConfirmModalProps {
  open: boolean;
  isSubmitting: boolean;
  answeredCount: number;
  totalQuestionCount: number;
  onCancel: () => void;
  onConfirm: () => void;
}

export function SurveySubmitConfirmModal({
  open,
  isSubmitting,
  answeredCount,
  totalQuestionCount,
  onCancel,
  onConfirm,
}: SurveySubmitConfirmModalProps) {
  if (!open) {
    return null;
  }

  return (
    <div className="survey-review-backdrop" role="presentation">
      <section className="survey-submit-modal" role="dialog" aria-modal="true" aria-labelledby="survey-submit-title">
        <div className="survey-review-modal__header">
          <h2 id="survey-submit-title">최종 제출</h2>
          <button type="button" onClick={onCancel} disabled={isSubmitting}>
            닫기
          </button>
        </div>
        <p>
          답변 {answeredCount} / {totalQuestionCount}
        </p>
        <div className="survey-review-actions">
          <button type="button" onClick={onCancel} disabled={isSubmitting}>
            취소
          </button>
          <button type="button" className="primary-action" onClick={onConfirm} disabled={isSubmitting}>
            {isSubmitting ? '제출 중' : '제출'}
          </button>
        </div>
      </section>
    </div>
  );
}
