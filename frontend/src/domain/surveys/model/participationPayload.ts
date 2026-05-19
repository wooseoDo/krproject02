import type {
  NormalSurveyParticipationDetailResponse,
  NormalSurveySubmitRequest,
  SurveyParticipationDraft,
} from '../types/types';

export function flattenParticipationQuestionIds(detail: NormalSurveyParticipationDetailResponse) {
  return detail.sections.flatMap((section) =>
    section.questions.map((question) => question.questionId).filter((questionId): questionId is string => Boolean(questionId)),
  );
}

export function buildNormalSurveySubmitPayload(
  detail: NormalSurveyParticipationDetailResponse,
  draft: SurveyParticipationDraft,
  elapsedTimeSec: number,
): NormalSurveySubmitRequest {
  const questionIds = flattenParticipationQuestionIds(detail);

  return {
    elapsedTimeSec,
    answers: questionIds.map((questionId) => ({
      questionId,
      optionId: draft.answers[questionId]?.optionId ?? '',
    })),
  };
}

export function isParticipationComplete(detail: NormalSurveyParticipationDetailResponse, draft: SurveyParticipationDraft) {
  const questionIds = flattenParticipationQuestionIds(detail);
  return questionIds.length > 0 && questionIds.every((questionId) => Boolean(draft.answers[questionId]?.optionId));
}
