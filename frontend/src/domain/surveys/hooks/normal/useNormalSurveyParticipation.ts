import { useEffect, useMemo, useState } from 'react';
import {
  fetchNormalSurveyParticipationDetail,
  startNormalSurveyParticipation,
  submitNormalSurveyParticipation,
} from '../../api/normal/api';
import {
  buildNormalSurveySubmitPayload,
  flattenParticipationQuestionIds,
  isParticipationComplete,
} from '../../model/participationPayload';
import { SURVEY_MESSAGES } from '../../constants/messages';
import type {
  NormalSurveyParticipationDetailResponse,
  NormalSurveySubmitResponse,
  SurveyParticipationDraft,
} from '../../types/types';
import { useSurveyParticipationDraft } from './useSurveyParticipationDraft';
import { useSurveyTimer } from './useSurveyTimer';

interface UseNormalSurveyParticipationOptions {
  surveyId: string;
  userId: string;
}

export function useNormalSurveyParticipation({ surveyId, userId }: UseNormalSurveyParticipationOptions) {
  const [detail, setDetail] = useState<NormalSurveyParticipationDetailResponse | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [submitResult, setSubmitResult] = useState<NormalSurveySubmitResponse | null>(null);
  const [alreadySubmittedResult, setAlreadySubmittedResult] = useState<NormalSurveySubmitResponse | null>(null);
  const draftStore = useSurveyParticipationDraft();
  const elapsedTimeSec = useSurveyTimer(draftStore.draft?.startedAt ?? null);

  useEffect(() => {
    let mounted = true;

    async function loadParticipation() {
      setIsLoading(true);
      setError('');
      setSubmitResult(null);
      setAlreadySubmittedResult(null);

      try {
        const nextDetail = await fetchNormalSurveyParticipationDetail(surveyId);
        const startResponse = await startNormalSurveyParticipation(surveyId, { userId });

        if (startResponse.completed) {
          if (mounted) {
            draftStore.clearDraft();
            setDetail(nextDetail);
            setAlreadySubmittedResult({
              responseId: startResponse.responseId,
              surveyId: startResponse.surveyId,
              surveyGroupId: startResponse.surveyGroupId,
              surveyVersion: startResponse.surveyVersion,
              completed: startResponse.completed,
              submittedAt: startResponse.submittedAt,
              elapsedTimeSec: startResponse.elapsedTimeSec,
              totalScore: startResponse.totalScore,
            });
          }

          return;
        }

        if (!startResponse.responseId || !startResponse.surveyId || !startResponse.surveyGroupId) {
          throw new Error('INVALID_START_RESPONSE');
        }

        const nextDraft = draftStore.initializeDraft({
          responseId: startResponse.responseId,
          surveyId: startResponse.surveyId,
          surveyGroupId: startResponse.surveyGroupId,
          surveyVersion: startResponse.surveyVersion,
          userId,
        });

        if (mounted) {
          setDetail(nextDetail);
          if (nextDraft.responseId !== startResponse.responseId) {
            setError('');
          }
        }
      } catch {
        if (mounted) {
          setError(SURVEY_MESSAGES.PARTICIPATION_LOAD_ERROR);
        }
      } finally {
        if (mounted) {
          setIsLoading(false);
        }
      }
    }

    void loadParticipation();

    return () => {
      mounted = false;
    };
  }, [surveyId, userId]);

  const questionIds = useMemo(() => (detail ? flattenParticipationQuestionIds(detail) : []), [detail]);
  const answeredCount = useMemo(
    () => questionIds.filter((questionId) => Boolean(draftStore.draft?.answers[questionId]?.optionId)).length,
    [draftStore.draft?.answers, questionIds],
  );
  const totalQuestionCount = questionIds.length;
  const isComplete = Boolean(detail && draftStore.draft && isParticipationComplete(detail, draftStore.draft));

  const submit = async () => {
    if (!detail || !draftStore.draft || !isComplete) {
      return;
    }

    setIsSubmitting(true);
    setError('');

    try {
      const payload = buildNormalSurveySubmitPayload(detail, draftStore.draft, elapsedTimeSec);
      const response = await submitNormalSurveyParticipation(draftStore.draft.responseId, payload);
      draftStore.clearDraft();
      setSubmitResult(response);
    } catch {
      setError(SURVEY_MESSAGES.PARTICIPATION_SUBMIT_ERROR);
    } finally {
      setIsSubmitting(false);
    }
  };

  const updateAnswer = (questionId: string | null, optionId: string | null) => {
    if (!questionId || !optionId) {
      return;
    }

    draftStore.updateAnswer(questionId, optionId);
  };

  return {
    detail,
    draft: draftStore.draft as SurveyParticipationDraft | null,
    elapsedTimeSec,
    answeredCount,
    totalQuestionCount,
    isComplete,
    isLoading,
    isSubmitting,
    error,
    submitResult,
    alreadySubmittedResult,
    updateAnswer,
    submit,
  };
}
