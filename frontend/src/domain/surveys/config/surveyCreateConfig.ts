import type {
  AdminSurveyCreateDraft,
  AdminSurveyDetailResponse,
  SurveyQuestionDraft,
  SurveyQuestionType,
  SurveySectionDraft,
} from '../types/types';

export const SURVEY_STATUS_OPTIONS = [
  { value: 'DRAFT', label: '작성중' },
  { value: 'PUBLISHED', label: '배포' },
  { value: 'LOCKED', label: '중단' },
  { value: 'CLOSED', label: '종료' },
] as const;

export const SURVEY_QUESTION_TYPE_OPTIONS: Array<{ value: SurveyQuestionType; label: string }> = [
  { value: 'SINGLE_CHOICE', label: '객관식' },
  { value: 'LIKERT', label: '리커트' },
];

export function createSurveyDraftId() {
  if (typeof crypto !== 'undefined' && 'randomUUID' in crypto) {
    return crypto.randomUUID();
  }

  return `${Date.now()}-${Math.random().toString(16).slice(2)}`;
}

export function createQuestionDraft(questionType: SurveyQuestionType = 'LIKERT'): SurveyQuestionDraft {
  return {
    id: createSurveyDraftId(),
    questionType,
    title: '',
    score: '1',
    optionCount: 5,
    options: ['', ''],
  };
}

export function createSectionDraft(): SurveySectionDraft {
  return {
    id: createSurveyDraftId(),
    title: '',
    targetAverageScore: '',
    questions: [createQuestionDraft()],
  };
}

export function createInitialSurveyDraft(): AdminSurveyCreateDraft {
  return {
    title: '',
    category: '',
    description: '',
    status: 'DRAFT',
    maxScore: '5',
    estimatedTimeMinutes: '10',
    sections: [createSectionDraft()],
  };
}

export function mapAdminSurveyDetailToDraft(detail: AdminSurveyDetailResponse): AdminSurveyCreateDraft {
  return {
    title: detail.title,
    category: detail.category ?? '',
    description: detail.description ?? '',
    status: detail.status,
    maxScore: String(detail.maxScore),
    estimatedTimeMinutes: detail.estimatedTimeSec == null ? '' : String(Math.round(detail.estimatedTimeSec / 60)),
    sections: detail.sections.map((section) => ({
      id: section.sectionId ?? createSurveyDraftId(),
      title: section.title,
      targetAverageScore: section.targetAverageScore == null ? '' : String(section.targetAverageScore),
      questions: section.questions.map((question) => ({
        id: question.questionId ?? createSurveyDraftId(),
        questionType: question.questionType,
        title: question.title,
        score: String(question.score),
        optionCount: question.options.length,
        options:
          question.questionType === 'LIKERT'
            ? ['', '']
            : question.options
                .slice()
                .sort((left, right) => left.optionSort - right.optionSort)
                .map((option) => option.optionLabel),
      })),
    })),
  };
}
