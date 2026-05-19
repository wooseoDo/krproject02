import { useState } from 'react';
import { updateAdminSurvey } from '../../api/admin/api';
import { SURVEY_MESSAGES } from '../../constants/messages';
import { toAdminSurveyCreateRequest } from '../../model/createPayload';
import type { AdminSurveyCreateDraft } from '../../types/types';

interface UseAdminSurveyUpdateMutationOptions {
  surveyId: string;
  onSuccess?: () => void;
}

export function useAdminSurveyUpdateMutation({ surveyId, onSuccess }: UseAdminSurveyUpdateMutationOptions) {
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [submitMessage, setSubmitMessage] = useState('');

  const submitSurveyUpdate = async (draft: AdminSurveyCreateDraft) => {
    setIsSubmitting(true);
    setSubmitMessage('');

    try {
      const updated = await updateAdminSurvey(surveyId, toAdminSurveyCreateRequest(draft));
      setSubmitMessage(SURVEY_MESSAGES.updateSuccess(updated.title));
      onSuccess?.();
      return updated;
    } catch (error) {
      const message = error instanceof Error ? error.message : SURVEY_MESSAGES.UPDATE_ERROR;
      setSubmitMessage(message);
      throw error;
    } finally {
      setIsSubmitting(false);
    }
  };

  return {
    isSubmitting,
    submitMessage,
    setSubmitMessage,
    submitSurveyUpdate,
  };
}

export type AdminSurveyUpdateMutation = ReturnType<typeof useAdminSurveyUpdateMutation>;
