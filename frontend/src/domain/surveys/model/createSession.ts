import type { AdminSurveyCreateDraft } from '../types/types';

export const SURVEY_CREATE_DRAFT_STORAGE_KEY = 'krproject02.surveys.admin.create-draft';

export function saveSurveyCreateDraft(draft: AdminSurveyCreateDraft) {
  window.sessionStorage.setItem(SURVEY_CREATE_DRAFT_STORAGE_KEY, JSON.stringify(draft));
}

export function readSurveyCreateDraft(): AdminSurveyCreateDraft | null {
  const raw = window.sessionStorage.getItem(SURVEY_CREATE_DRAFT_STORAGE_KEY);

  if (!raw) {
    return null;
  }

  try {
    return JSON.parse(raw) as AdminSurveyCreateDraft;
  } catch {
    window.sessionStorage.removeItem(SURVEY_CREATE_DRAFT_STORAGE_KEY);
    return null;
  }
}

export function clearSurveyCreateDraft() {
  window.sessionStorage.removeItem(SURVEY_CREATE_DRAFT_STORAGE_KEY);
}
