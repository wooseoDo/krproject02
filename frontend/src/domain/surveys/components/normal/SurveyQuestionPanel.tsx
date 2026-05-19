import type { NormalSurveyParticipationDetailResponse, SurveyParticipationDraft } from '../../types/types';
import { SurveyAnswerOptions } from './SurveyAnswerOptions';

interface SurveyQuestionPanelProps {
  detail: NormalSurveyParticipationDetailResponse;
  draft: SurveyParticipationDraft | null;
  onAnswerChange: (questionId: string | null, optionId: string | null) => void;
}

export function SurveyQuestionPanel({ detail, draft, onAnswerChange }: SurveyQuestionPanelProps) {
  return (
    <section className="survey-participation-sections">
      {detail.sections.map((section) => (
        <article className="survey-participation-section" key={section.sectionId ?? section.sectionSort}>
          <div className="survey-participation-section__header">
            <span>{section.sectionSort}</span>
            <h2>{section.title}</h2>
          </div>

          <div className="survey-participation-questions">
            {section.questions.map((question) => (
              <div className="survey-participation-question" key={question.questionId ?? question.questionSort}>
                <div className="survey-participation-question__title">
                  <span>{question.questionSort}</span>
                  <h3>{question.title}</h3>
                </div>
                <SurveyAnswerOptions
                  question={question}
                  selectedOptionId={question.questionId ? draft?.answers[question.questionId]?.optionId : undefined}
                  onChange={onAnswerChange}
                />
              </div>
            ))}
          </div>
        </article>
      ))}
    </section>
  );
}
