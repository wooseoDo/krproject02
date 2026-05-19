import { useState } from 'react';
import {
  clearSurveyParticipationDraft,
  createSurveyParticipationDraft,
  readSurveyParticipationDraft,
  saveSurveyParticipationDraft,
} from '../../model/participationSession';
import type { SurveyParticipationDraft } from '../../types/types';

interface InitializeDraftInput {
  responseId: string;
  surveyId: string;
  surveyGroupId: string;
  surveyVersion: number;
  userId: string;
}

export function useSurveyParticipationDraft() {
  const [draft, setDraft] = useState<SurveyParticipationDraft | null>(null);

  const initializeDraft = (input: InitializeDraftInput) => {
    const savedDraft = readSurveyParticipationDraft(input.userId, input.surveyGroupId, input.surveyVersion);
    const nextDraft = savedDraft ?? createSurveyParticipationDraft(input);

    saveSurveyParticipationDraft(nextDraft);
    setDraft(nextDraft);
    return nextDraft;
  };

  const updateAnswer = (questionId: string, optionId: string) => {
    setDraft((current) => {
      if (!current) {
        return current;
      }

      const nextDraft: SurveyParticipationDraft = {
        ...current,
        answers: {
          ...current.answers,
          [questionId]: {
            questionId,
            optionId,
            answeredAt: new Date().toISOString(),
          },
        },
      };

      saveSurveyParticipationDraft(nextDraft);
      return nextDraft;
    });
  };

  const clearDraft = () => {
    if (draft) {
      clearSurveyParticipationDraft(draft);
    }
    setDraft(null);
  };

  return {
    draft,
    initializeDraft,
    updateAnswer,
    clearDraft,
  };
}
