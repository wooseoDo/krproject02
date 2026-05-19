import { describe, expect, it } from 'vitest';
import {
  buildLikertOptionLabels,
  getQuestionScoreTotal,
  toAdminSurveyCreateRequest,
  validateSurveyCreateDraft,
} from './createPayload';
import { createInitialSurveyDraft } from '../config/surveyCreateConfig';

function validDraft() {
  const draft = createInitialSurveyDraft();
  draft.title = '직무 만족도 조사';
  draft.maxScore = '5';
  draft.estimatedTimeMinutes = '10';
  draft.sections = [
    {
      id: 'section-1',
      title: '업무 환경',
      targetAverageScore: '',
      questions: Array.from({ length: 5 }, (_, index) => ({
        id: `question-${index + 1}`,
        questionType: 'LIKERT',
        title: `문항 ${index + 1}`,
        score: '1',
        optionCount: 5,
        options: ['옵션 1', '옵션 2'],
      })),
    },
  ];
  return draft;
}

describe('survey create model', () => {
  it('builds likert labels by option count', () => {
    expect(buildLikertOptionLabels(5)).toEqual(['매우 싫다', '싫다', '보통이다', '좋다', '매우 좋다']);
    expect(buildLikertOptionLabels(4)).toEqual(['매우 싫다', '싫다', '좋다', '매우 좋다']);
    expect(buildLikertOptionLabels(3)).toEqual(['매우 싫다', '보통이다', '매우 좋다']);
    expect(buildLikertOptionLabels(2)).toEqual(['매우 싫다', '매우 좋다']);
  });

  it('validates that question scores match the max score', () => {
    const draft = validDraft();

    expect(getQuestionScoreTotal(draft)).toBe(5);
    expect(validateSurveyCreateDraft(draft)).toEqual([]);

    draft.maxScore = '6';
    expect(validateSurveyCreateDraft(draft)).toContain('문항 배점 합계 5점이 최대 점수 6점과 같아야 합니다.');
  });

  it('maps draft values to the backend create request', () => {
    const request = toAdminSurveyCreateRequest(validDraft());

    expect(request).toMatchObject({
      title: '직무 만족도 조사',
      status: 'DRAFT',
      maxScore: 5,
      estimatedTimeSec: 600,
    });
    expect(request.sections[0].questions[0].options).toEqual([
      { optionLabel: '매우 싫다', optionScore: 1 },
      { optionLabel: '싫다', optionScore: 2 },
      { optionLabel: '보통이다', optionScore: 3 },
      { optionLabel: '좋다', optionScore: 4 },
      { optionLabel: '매우 좋다', optionScore: 5 },
    ]);
  });
});
