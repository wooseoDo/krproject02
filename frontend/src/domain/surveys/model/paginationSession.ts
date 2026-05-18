import {
  createSurveyListDefaultPageState,
  SURVEY_PAGE_SIZE,
} from '../config/surveyListConfig';
import type { SurveyListPageState, SurveyScope } from '../types/types';

const STORAGE_KEY_PREFIX = 'krproject02.surveys.list';

export function readSurveyListPageState(scope: SurveyScope): SurveyListPageState {
  // Restore per-scope pagination and filter state from session storage.
  const raw = window.sessionStorage.getItem(`${STORAGE_KEY_PREFIX}.${scope}`);

  if (!raw) {
    return createSurveyListDefaultPageState();
  }

  try {
    const parsed = JSON.parse(raw) as SurveyListPageState;
    const defaultState = createSurveyListDefaultPageState();

    return {
      ...defaultState,
      ...parsed,
      filters: {
        ...defaultState.filters,
        ...parsed.filters,
      },
      pageSize: SURVEY_PAGE_SIZE,
    };
  } catch {
    return createSurveyListDefaultPageState();
  }
}

export function saveSurveyListPageState(scope: SurveyScope, state: SurveyListPageState) {
  // Persist per-scope pagination and filter state for the current browser tab.
  window.sessionStorage.setItem(`${STORAGE_KEY_PREFIX}.${scope}`, JSON.stringify(state));
}
