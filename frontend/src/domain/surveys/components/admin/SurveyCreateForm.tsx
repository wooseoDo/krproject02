import type { Dispatch, SetStateAction } from 'react';
import {
  SURVEY_QUESTION_TYPE_OPTIONS,
  SURVEY_STATUS_OPTIONS,
  createQuestionDraft,
  createSectionDraft,
} from '../../config/surveyCreateConfig';
import { buildLikertOptionLabels, getQuestionScoreTotal } from '../../model/createPayload';
import type {
  AdminSurveyCreateDraft,
  SurveyQuestionDraft,
  SurveyQuestionType,
  SurveySectionDraft,
  SurveyStatus,
} from '../../types/types';

interface SurveyCreateFormProps {
  draft: AdminSurveyCreateDraft;
  errors: string[];
  submitMessage: string;
  setDraft: Dispatch<SetStateAction<AdminSurveyCreateDraft>>;
  onCancel: () => void;
  onOpenReview: () => void;
  onClearSubmitMessage: () => void;
}

type SurveyDraftField = Exclude<keyof AdminSurveyCreateDraft, 'sections'>;
type SurveySectionTextField = 'title' | 'targetAverageScore';
type SurveyQuestionEditableField = 'questionType' | 'title' | 'score' | 'optionCount';

function replaceSection(
  draft: AdminSurveyCreateDraft,
  sectionId: string,
  updater: (section: SurveySectionDraft) => SurveySectionDraft,
) {
  return {
    ...draft,
    sections: draft.sections.map((section) => (section.id === sectionId ? updater(section) : section)),
  };
}

function replaceQuestion(
  section: SurveySectionDraft,
  questionId: string,
  updater: (question: SurveyQuestionDraft) => SurveyQuestionDraft,
) {
  return {
    ...section,
    questions: section.questions.map((question) => (question.id === questionId ? updater(question) : question)),
  };
}

export function SurveyCreateForm({
  draft,
  errors,
  submitMessage,
  setDraft,
  onCancel,
  onOpenReview,
  onClearSubmitMessage,
}: SurveyCreateFormProps) {
  const totalScore = getQuestionScoreTotal(draft);
  const maxScore = Number(draft.maxScore || 0);

  const updateDraftField = <TKey extends SurveyDraftField>(key: TKey, value: AdminSurveyCreateDraft[TKey]) => {
    setDraft((current) => ({ ...current, [key]: value }));
    onClearSubmitMessage();
  };

  const updateSection = (sectionId: string, key: SurveySectionTextField, value: string) => {
    setDraft((current) => replaceSection(current, sectionId, (section) => ({ ...section, [key]: value })));
  };

  const updateQuestion = <TKey extends SurveyQuestionEditableField>(
    sectionId: string,
    questionId: string,
    key: TKey,
    value: SurveyQuestionDraft[TKey],
  ) => {
    setDraft((current) =>
      replaceSection(current, sectionId, (section) =>
        replaceQuestion(section, questionId, (question) => ({ ...question, [key]: value })),
      ),
    );
  };

  const addSection = () => {
    setDraft((current) => ({ ...current, sections: [...current.sections, createSectionDraft()] }));
  };

  const removeSection = (sectionId: string) => {
    setDraft((current) => ({
      ...current,
      sections: current.sections.length <= 1 ? current.sections : current.sections.filter((section) => section.id !== sectionId),
    }));
  };

  const addQuestion = (sectionId: string) => {
    setDraft((current) =>
      replaceSection(current, sectionId, (section) => ({
        ...section,
        questions: [...section.questions, createQuestionDraft()],
      })),
    );
  };

  const removeQuestion = (sectionId: string, questionId: string) => {
    setDraft((current) =>
      replaceSection(current, sectionId, (section) => ({
        ...section,
        questions:
          section.questions.length <= 1
            ? section.questions
            : section.questions.filter((question) => question.id !== questionId),
      })),
    );
  };

  const addMultipleChoiceOption = (sectionId: string, questionId: string) => {
    setDraft((current) =>
      replaceSection(current, sectionId, (section) =>
        replaceQuestion(section, questionId, (question) => ({
          ...question,
          options: question.options.length >= 5 ? question.options : [...question.options, ''],
        })),
      ),
    );
  };

  const updateMultipleChoiceOption = (sectionId: string, questionId: string, optionIndex: number, value: string) => {
    setDraft((current) =>
      replaceSection(current, sectionId, (section) =>
        replaceQuestion(section, questionId, (question) => ({
          ...question,
          options: question.options.map((option, index) => (index === optionIndex ? value : option)),
        })),
      ),
    );
  };

  const removeMultipleChoiceOption = (sectionId: string, questionId: string, optionIndex: number) => {
    setDraft((current) =>
      replaceSection(current, sectionId, (section) =>
        replaceQuestion(section, questionId, (question) => ({
          ...question,
          options: question.options.length <= 2 ? question.options : question.options.filter((_, index) => index !== optionIndex),
        })),
      ),
    );
  };

  return (
    <>
      <div className="survey-create-panel__header">
        <div>
          <p className="section-label">CREATE</p>
          <h2>신규 조사지 생성</h2>
        </div>
        <button type="button" className="secondary-action" onClick={onCancel}>
          닫기
        </button>
      </div>

      <div className="survey-create-grid">
        <label className="field-group">
          조사지 제목
          <input
            value={draft.title}
            placeholder="예: 직무 스트레스 자가진단 조사지"
            onChange={(event) => updateDraftField('title', event.target.value)}
          />
        </label>
        <label className="field-group">
          최고 점수
          <input
            type="number"
            min="1"
            value={draft.maxScore}
            placeholder="예: 30"
            onChange={(event) => updateDraftField('maxScore', event.target.value)}
          />
        </label>
        <label className="field-group">
          평균 소요시간(분)
          <input
            type="number"
            min="1"
            value={draft.estimatedTimeMinutes}
            placeholder="예: 10"
            onChange={(event) => updateDraftField('estimatedTimeMinutes', event.target.value)}
          />
        </label>
        <label className="field-group">
          상태
          <select value={draft.status} onChange={(event) => updateDraftField('status', event.target.value as SurveyStatus)}>
            {SURVEY_STATUS_OPTIONS.map((status) => (
              <option key={status.value} value={status.value}>
                {status.label}
              </option>
            ))}
          </select>
        </label>
        <label className="field-group">
          카테고리
          <input
            value={draft.category}
            placeholder="예: 심리/직무"
            onChange={(event) => updateDraftField('category', event.target.value)}
          />
        </label>
        <label className="field-group survey-create-grid__wide">
          설명
          <textarea
            value={draft.description}
            placeholder="조사지 설명을 입력해 주세요."
            onChange={(event) => updateDraftField('description', event.target.value)}
          />
        </label>
      </div>

      <div className={`survey-score-meter ${totalScore === maxScore ? 'is-valid' : 'is-invalid'}`}>
        <span>문항 배점 합계 {totalScore}점</span>
        <strong>최고 점수 {Number.isFinite(maxScore) ? maxScore : 0}점</strong>
      </div>

      <div className="survey-section-builder">
        <div className="survey-section-builder__toolbar">
          <h3>조사지 항목</h3>
          <button type="button" className="secondary-action" onClick={addSection}>
            + 항목 추가
          </button>
        </div>

        {draft.sections.map((section, sectionIndex) => (
          <section className="survey-section-editor" key={section.id}>
            <div className="survey-section-editor__header">
              <label className="field-group">
                {sectionIndex + 1}번 항목 이름
                <input
                  value={section.title}
                  placeholder="예: 업무 부담"
                  onChange={(event) => updateSection(section.id, 'title', event.target.value)}
                />
              </label>
              <label className="field-group">
                목표 평균 점수
                <input
                  type="number"
                  step="0.1"
                  value={section.targetAverageScore}
                  placeholder="예: 7.0"
                  onChange={(event) => updateSection(section.id, 'targetAverageScore', event.target.value)}
                />
              </label>
              <button type="button" className="secondary-action" onClick={() => removeSection(section.id)}>
                항목 삭제
              </button>
            </div>

            <div className="survey-question-list">
              {section.questions.map((question, questionIndex) => (
                <div className="survey-question-editor" key={question.id}>
                  <div className="survey-question-editor__top">
                    <strong>문제 {questionIndex + 1}</strong>
                    <button type="button" onClick={() => removeQuestion(section.id, question.id)}>
                      삭제
                    </button>
                  </div>

                  <div className="survey-question-editor__fields">
                    <label className="field-group">
                      문제 유형
                      <select
                        value={question.questionType}
                        onChange={(event) =>
                          updateQuestion(section.id, question.id, 'questionType', event.target.value as SurveyQuestionType)
                        }
                      >
                        {SURVEY_QUESTION_TYPE_OPTIONS.map((type) => (
                          <option key={type.value} value={type.value}>
                            {type.label}
                          </option>
                        ))}
                      </select>
                    </label>
                    <label className="field-group">
                      배점
                      <input
                        type="number"
                        min="1"
                        value={question.score}
                        placeholder="예: 5"
                        onChange={(event) => updateQuestion(section.id, question.id, 'score', event.target.value)}
                      />
                    </label>
                    <label className="field-group survey-question-editor__title">
                      문제명
                      <input
                        value={question.title}
                        placeholder="예: 최근 2주 동안 업무량이 부담스럽다고 느꼈다."
                        onChange={(event) => updateQuestion(section.id, question.id, 'title', event.target.value)}
                      />
                    </label>
                  </div>

                  {question.questionType === 'LIKERT' ? (
                    <div className="survey-options-editor">
                      <label className="field-group">
                        리커트 옵션 개수
                        <select
                          value={question.optionCount}
                          onChange={(event) => updateQuestion(section.id, question.id, 'optionCount', Number(event.target.value))}
                        >
                          {[2, 3, 4, 5].map((count) => (
                            <option key={count} value={count}>
                              {count}개
                            </option>
                          ))}
                        </select>
                      </label>
                      <div className="survey-option-preview">
                        {buildLikertOptionLabels(question.optionCount).map((label, optionIndex) => (
                          <label className="survey-option-preview__radio" key={label}>
                            <input type="radio" name={`${question.id}-likert-preview`} disabled />
                            <span>
                              {optionIndex + 1}. {label}
                            </span>
                          </label>
                        ))}
                      </div>
                    </div>
                  ) : (
                    <div className="survey-options-editor">
                      <div className="survey-options-editor__toolbar">
                        <strong>객관식 옵션</strong>
                        <button type="button" onClick={() => addMultipleChoiceOption(section.id, question.id)}>
                          + 옵션
                        </button>
                      </div>
                      {question.options.map((option, optionIndex) => (
                        <label className="field-group survey-option-row" key={`${question.id}-${optionIndex}`}>
                          <span className="survey-option-row__number">{optionIndex + 1}번</span>
                          <input
                            value={option}
                            placeholder={`옵션 ${optionIndex + 1}`}
                            onChange={(event) =>
                              updateMultipleChoiceOption(section.id, question.id, optionIndex, event.target.value)
                            }
                          />
                          <button
                            type="button"
                            onClick={() => removeMultipleChoiceOption(section.id, question.id, optionIndex)}
                          >
                            삭제
                          </button>
                        </label>
                      ))}
                    </div>
                  )}
                </div>
              ))}
            </div>

            <button type="button" className="secondary-action" onClick={() => addQuestion(section.id)}>
              + 문제 추가
            </button>
          </section>
        ))}
      </div>

      {errors.length > 0 && (
        <div className="survey-create-errors" role="alert">
          {errors.map((error) => (
            <p key={error}>{error}</p>
          ))}
        </div>
      )}

      {submitMessage && <p className="form-notice">{submitMessage}</p>}

      <div className="survey-create-actions">
        <button type="button" className="primary-action" onClick={onOpenReview}>
          최종 확인
        </button>
      </div>
    </>
  );
}
