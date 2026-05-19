import { SURVEY_STATUS_OPTIONS } from '../../config/surveyCreateConfig';
import { buildLikertOptionLabels } from '../../model/createPayload';
import type { AdminSurveyCreateDraft, SurveyQuestionDraft } from '../../types/types';

interface SurveyCreateReviewModalProps {
  draft: AdminSurveyCreateDraft;
  sectionIndex: number;
  isSubmitting: boolean;
  onClose: () => void;
  onMoveSection: (nextIndex: number) => void;
  onComplete: () => void;
}

function getReviewOptions(question: SurveyQuestionDraft) {
  return question.questionType === 'LIKERT'
    ? buildLikertOptionLabels(question.optionCount)
    : question.options.slice(0, 5).filter((option) => option.trim() !== '');
}

export function SurveyCreateReviewModal({
  draft,
  sectionIndex,
  isSubmitting,
  onClose,
  onMoveSection,
  onComplete,
}: SurveyCreateReviewModalProps) {
  const currentSection = draft.sections[sectionIndex];

  if (!currentSection) {
    return null;
  }

  return (
    <div className="survey-review-backdrop" role="dialog" aria-modal="true" aria-label="조사지 최종 확인">
      <section className="survey-review-modal">
        <div className="survey-review-modal__header">
          <div>
            <p className="section-label">REVIEW</p>
            <h2>{draft.title}</h2>
          </div>
          <button type="button" onClick={onClose}>
            닫기
          </button>
        </div>

        <div className="survey-review-summary">
          <span>상태 {SURVEY_STATUS_OPTIONS.find((status) => status.value === draft.status)?.label}</span>
          <span>최고 {draft.maxScore}점</span>
          <span>예상 {draft.estimatedTimeMinutes}분</span>
        </div>

        <section className="survey-review-section">
          <h3>
            {sectionIndex + 1}. {currentSection.title}
          </h3>
          {currentSection.questions.map((question, questionIndex) => (
            <div className="survey-review-question" key={question.id}>
              <strong>
                {questionIndex + 1}. {question.title} ({question.score}점)
              </strong>
              <ul className={question.questionType === 'LIKERT' ? 'survey-review-options is-likert' : 'survey-review-options'}>
                {getReviewOptions(question).map((option, optionIndex) => (
                  <li key={`${question.id}-${optionIndex}`}>
                    {question.questionType === 'LIKERT' ? (
                      <label className="survey-review-option-radio">
                        <input type="radio" name={`${question.id}-review`} disabled />
                        <span>
                          {optionIndex + 1}. {option}
                        </span>
                      </label>
                    ) : (
                      <span className="survey-review-option-choice">
                        <strong>{optionIndex + 1}번</strong>
                        <span>{option}</span>
                      </span>
                    )}
                  </li>
                ))}
              </ul>
            </div>
          ))}
        </section>

        <div className="survey-review-actions">
          <button type="button" disabled={sectionIndex === 0} onClick={() => onMoveSection(sectionIndex - 1)}>
            이전
          </button>
          {sectionIndex < draft.sections.length - 1 ? (
            <button type="button" onClick={() => onMoveSection(sectionIndex + 1)}>
              다음
            </button>
          ) : (
            <button type="button" className="primary-action" disabled={isSubmitting} onClick={onComplete}>
              최종 완료
            </button>
          )}
        </div>
      </section>
    </div>
  );
}
