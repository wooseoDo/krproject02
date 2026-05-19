import { surveyParticipationDraftSchema } from '../types/schemas';
import type { SurveyParticipationDraft } from '../types/types';

const STORAGE_KEY_PREFIX = 'krproject02.surveys.participation';

export function createSurveyParticipationDraftKey(userId: string, surveyGroupId: string, surveyVersion: number) {
  return `${STORAGE_KEY_PREFIX}.${userId}.${surveyGroupId}.${surveyVersion}`;
}

export function createSurveyParticipationDraft({
  responseId,
  surveyId,
  surveyGroupId,
  surveyVersion,
  userId,
}: Pick<SurveyParticipationDraft, 'responseId' | 'surveyId' | 'surveyGroupId' | 'surveyVersion' | 'userId'>) {
  const now = new Date().toISOString();

  return {
    responseId,
    surveyId,
    surveyGroupId,
    surveyVersion,
    userId,
    startedAt: now,
    lastSavedAt: now,
    answers: {},
  } satisfies SurveyParticipationDraft;
}

export function readSurveyParticipationDraft(
  userId: string,
  surveyGroupId: string,
  surveyVersion: number,
): SurveyParticipationDraft | null {
  const key = createSurveyParticipationDraftKey(userId, surveyGroupId, surveyVersion);
  const raw = window.sessionStorage.getItem(key);

  if (!raw) {
    return null;
  }

  try {
    return surveyParticipationDraftSchema.parse(JSON.parse(raw));
  } catch {
    window.sessionStorage.removeItem(key);
    return null;
  }
}

export function saveSurveyParticipationDraft(draft: SurveyParticipationDraft) {
  const key = createSurveyParticipationDraftKey(draft.userId, draft.surveyGroupId, draft.surveyVersion);
  window.sessionStorage.setItem(
    key,
    JSON.stringify({
      ...draft,
      lastSavedAt: new Date().toISOString(),
    }),
  );
}

export function clearSurveyParticipationDraft(draft: SurveyParticipationDraft) {
  const key = createSurveyParticipationDraftKey(draft.userId, draft.surveyGroupId, draft.surveyVersion);
  window.sessionStorage.removeItem(key);
}
