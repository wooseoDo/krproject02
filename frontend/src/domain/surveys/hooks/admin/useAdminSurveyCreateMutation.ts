import { useState } from 'react';
import { createAdminSurvey } from '../../api/admin/api';
import { SURVEY_MESSAGES } from '../../constants/messages';
import { toAdminSurveyCreateRequest } from '../../model/createPayload';
import type { AdminSurveyCreateDraft } from '../../types/types';

interface UseAdminSurveyCreateMutationOptions {
  onSuccess?: () => void;
}

export function useAdminSurveyCreateMutation({ onSuccess }: UseAdminSurveyCreateMutationOptions = {}) {
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [submitMessage, setSubmitMessage] = useState('');

  const submitSurveyCreate = async (draft: AdminSurveyCreateDraft) => {
    setIsSubmitting(true);
    setSubmitMessage('');

    try {
      const created = await createAdminSurvey(toAdminSurveyCreateRequest(draft));
      setSubmitMessage(SURVEY_MESSAGES.createSuccess(created.title));
      onSuccess?.();
      return created;
    } catch (error) {
      const message = error instanceof Error ? error.message : SURVEY_MESSAGES.CREATE_ERROR;
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
    submitSurveyCreate,
  };
}

export type AdminSurveyCreateMutation = ReturnType<typeof useAdminSurveyCreateMutation>;
