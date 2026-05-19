import type {
  AdminSurveyCreateDraft,
  AdminSurveyCreateRequest,
  AdminSurveyQuestionOptionRequest,
  SurveyQuestionDraft,
} from '../types/types';

export function buildLikertOptionLabels(optionCount: number) {
  if (optionCount === 2) {
    return ['매우 싫다', '매우 좋다'];
  }

  if (optionCount === 3) {
    return ['매우 싫다', '보통이다', '매우 좋다'];
  }

  if (optionCount === 4) {
    return ['매우 싫다', '싫다', '좋다', '매우 좋다'];
  }

  return ['매우 싫다', '싫다', '보통이다', '좋다', '매우 좋다'];
}

export function getQuestionScoreTotal(draft: AdminSurveyCreateDraft) {
  return draft.sections.reduce(
    (sectionTotal, section) =>
      sectionTotal +
      section.questions.reduce((questionTotal, question) => questionTotal + Number(question.score || 0), 0),
    0,
  );
}

function createOptions(question: SurveyQuestionDraft): AdminSurveyQuestionOptionRequest[] {
  const labels =
    question.questionType === 'LIKERT'
      ? buildLikertOptionLabels(question.optionCount)
      : question.options.slice(0, 5).filter((option) => option.trim() !== '');

  return labels.map((label, index) => ({
    optionLabel: label.trim(),
    optionScore: index + 1,
  }));
}

export function validateSurveyCreateDraft(draft: AdminSurveyCreateDraft): string[] {
  const errors: string[] = [];
  const maxScore = Number(draft.maxScore);
  const estimatedTimeMinutes = Number(draft.estimatedTimeMinutes);
  const questionCount = draft.sections.reduce((total, section) => total + section.questions.length, 0);
  const totalScore = getQuestionScoreTotal(draft);

  if (draft.title.trim() === '') {
    errors.push('조사지 타이틀을 입력해 주세요.');
  }

  if (!Number.isInteger(maxScore) || maxScore < 1) {
    errors.push('최대 점수는 1점 이상 정수로 입력해 주세요.');
  }

  if (!Number.isFinite(estimatedTimeMinutes) || estimatedTimeMinutes <= 0) {
    errors.push('평균 소요시간은 1분 이상으로 입력해 주세요.');
  }

  if (draft.sections.length < 1) {
    errors.push('조사지 항목을 최소 1개 추가해 주세요.');
  }

  if (questionCount < 5) {
    errors.push('백엔드 검증 기준에 맞춰 문항을 최소 5개 추가해 주세요.');
  }

  if (maxScore !== totalScore) {
    errors.push(`문항 배점 합계 ${totalScore}점이 최대 점수 ${maxScore}점과 같아야 합니다.`);
  }

  draft.sections.forEach((section, sectionIndex) => {
    if (section.title.trim() === '') {
      errors.push(`${sectionIndex + 1}번 항목 이름을 입력해 주세요.`);
    }

    section.questions.forEach((question, questionIndex) => {
      const score = Number(question.score);
      const options = createOptions(question);

      if (question.title.trim() === '') {
        errors.push(`${sectionIndex + 1}번 항목 ${questionIndex + 1}번 문제명을 입력해 주세요.`);
      }

      if (!Number.isInteger(score) || score < 1) {
        errors.push(`${sectionIndex + 1}번 항목 ${questionIndex + 1}번 문제 배점은 1점 이상이어야 합니다.`);
      }

      if (options.length < 2 || options.length > 5) {
        errors.push(`${sectionIndex + 1}번 항목 ${questionIndex + 1}번 문제 옵션은 2개부터 5개까지 필요합니다.`);
      }
    });
  });

  return errors;
}

export function toAdminSurveyCreateRequest(draft: AdminSurveyCreateDraft): AdminSurveyCreateRequest {
  return {
    title: draft.title.trim(),
    category: draft.category.trim() || null,
    description: draft.description.trim() || null,
    status: draft.status,
    maxScore: Number(draft.maxScore),
    estimatedTimeSec: Math.round(Number(draft.estimatedTimeMinutes) * 60),
    sections: draft.sections.map((section) => ({
      title: section.title.trim(),
      targetAverageScore: section.targetAverageScore === '' ? null : Number(section.targetAverageScore),
      questions: section.questions.map((question) => ({
        questionType: question.questionType,
        title: question.title.trim(),
        score: Number(question.score),
        options: createOptions(question),
      })),
    })),
  };
}
