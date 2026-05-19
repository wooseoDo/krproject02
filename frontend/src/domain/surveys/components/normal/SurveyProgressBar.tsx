interface SurveyProgressBarProps {
  answeredCount: number;
  totalQuestionCount: number;
}

export function SurveyProgressBar({ answeredCount, totalQuestionCount }: SurveyProgressBarProps) {
  const progress = totalQuestionCount === 0 ? 0 : Math.round((answeredCount / totalQuestionCount) * 100);

  return (
    <div className="survey-participation-progress" aria-label="답변 진행률">
      <div className="survey-participation-progress__meta">
        <span>
          {answeredCount} / {totalQuestionCount}
        </span>
        <strong>{progress}%</strong>
      </div>
      <div className="survey-participation-progress__track">
        <span style={{ width: `${progress}%` }} />
      </div>
    </div>
  );
}
