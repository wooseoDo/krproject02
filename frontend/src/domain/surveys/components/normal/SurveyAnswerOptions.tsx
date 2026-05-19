import type { NormalSurveyParticipationQuestionResponse } from '../../types/types';

interface SurveyAnswerOptionsProps {
  question: NormalSurveyParticipationQuestionResponse;
  selectedOptionId?: string;
  onChange: (questionId: string | null, optionId: string | null) => void;
}

export function SurveyAnswerOptions({ question, selectedOptionId, onChange }: SurveyAnswerOptionsProps) {
  return (
    <ul className="survey-answer-options">
      {question.options.map((option) => {
        const optionKey = option.optionId ?? `${question.questionId}-${option.optionSort}`;

        return (
          <li key={optionKey}>
            <label className="survey-answer-option">
              <input
                type="radio"
                name={`question-${question.questionId}`}
                checked={selectedOptionId === option.optionId}
                onChange={() => onChange(question.questionId, option.optionId)}
              />
              <span>{question.questionType === 'SINGLE_CHOICE' ? `${option.optionSort}번` : option.optionSort}</span>
              <strong>{option.optionLabel}</strong>
            </label>
          </li>
        );
      })}
    </ul>
  );
}
