import { z } from 'zod';
import type { SurveyParticipationDraft } from './types';

export const surveyStatusSchema = z.enum(['DRAFT', 'PUBLISHED', 'LOCKED', 'CLOSED']);
export const surveyQuestionTypeSchema = z.enum(['SINGLE_CHOICE', 'LIKERT']);

export const surveyListItemSchema = z.object({
  surveyId: z.string().uuid().nullable(),
  title: z.string(),
  surveyVersion: z.number().int().nonnegative(),
  status: surveyStatusSchema,
  maxScore: z.number().int().nonnegative(),
  category: z.string().nullable(),
  estimatedTimeSec: z.number().int().nonnegative().nullable(),
  createdAt: z.string(),
});

export const surveyListPageResponseSchema = z.object({
  items: z.array(surveyListItemSchema),
  page: z.number().int().positive(),
  size: z.number().int().positive(),
  totalItems: z.number().int().nonnegative(),
  totalPages: z.number().int().positive(),
});

export const adminSurveyCreateResponseSchema = z.object({
  surveyId: z.string().uuid().nullable(),
  title: z.string(),
  surveyVersion: z.number().int().nonnegative(),
  status: surveyStatusSchema,
});

const adminSurveyDetailOptionSchema = z.object({
  optionId: z.string().uuid().nullable(),
  optionSort: z.number().int().positive(),
  optionLabel: z.string(),
  optionScore: z.number().int().nonnegative(),
});

const adminSurveyDetailQuestionSchema = z.object({
  questionId: z.string().uuid().nullable(),
  questionSort: z.number().int().positive(),
  questionType: surveyQuestionTypeSchema,
  title: z.string(),
  score: z.number().int().positive(),
  options: z.array(adminSurveyDetailOptionSchema),
});

const adminSurveyDetailSectionSchema = z.object({
  sectionId: z.string().uuid().nullable(),
  sectionSort: z.number().int().positive(),
  title: z.string(),
  targetAverageScore: z.number().nullable(),
  questions: z.array(adminSurveyDetailQuestionSchema),
});

export const adminSurveyDetailResponseSchema = z.object({
  surveyId: z.string().uuid().nullable(),
  surveyGroupId: z.string().uuid().nullable(),
  previousSurveyId: z.string().uuid().nullable(),
  surveyVersion: z.number().int().nonnegative(),
  latest: z.boolean(),
  title: z.string(),
  category: z.string().nullable(),
  description: z.string().nullable(),
  status: surveyStatusSchema,
  maxScore: z.number().int().nonnegative(),
  estimatedTimeSec: z.number().int().nonnegative().nullable(),
  sections: z.array(adminSurveyDetailSectionSchema),
});

const normalSurveyParticipationOptionSchema = z.object({
  optionId: z.string().uuid().nullable(),
  optionSort: z.number().int().positive(),
  optionLabel: z.string(),
});

const normalSurveyParticipationQuestionSchema = z.object({
  questionId: z.string().uuid().nullable(),
  questionSort: z.number().int().positive(),
  questionType: surveyQuestionTypeSchema,
  title: z.string(),
  options: z.array(normalSurveyParticipationOptionSchema),
});

const normalSurveyParticipationSectionSchema = z.object({
  sectionId: z.string().uuid().nullable(),
  sectionSort: z.number().int().positive(),
  title: z.string(),
  targetAverageScore: z.number().nullable(),
  questions: z.array(normalSurveyParticipationQuestionSchema),
});

export const normalSurveyParticipationDetailResponseSchema = z.object({
  surveyId: z.string().uuid().nullable(),
  surveyGroupId: z.string().uuid().nullable(),
  surveyVersion: z.number().int().nonnegative(),
  title: z.string(),
  category: z.string().nullable(),
  description: z.string().nullable(),
  status: surveyStatusSchema,
  maxScore: z.number().int().nonnegative(),
  estimatedTimeSec: z.number().int().nonnegative().nullable(),
  sections: z.array(normalSurveyParticipationSectionSchema),
});

export const normalSurveyParticipationStartResponseSchema = z.object({
  responseId: z.string().uuid().nullable(),
  surveyId: z.string().uuid().nullable(),
  surveyGroupId: z.string().uuid().nullable(),
  surveyVersion: z.number().int().nonnegative(),
  surveyTitle: z.string(),
  resumed: z.boolean(),
  completed: z.boolean(),
  submittedAt: z.string().nullable(),
  elapsedTimeSec: z.number().int().nonnegative().nullable(),
  totalScore: z.number().nullable(),
});

export const normalSurveySubmitResponseSchema = z.object({
  responseId: z.string().uuid().nullable(),
  surveyId: z.string().uuid().nullable(),
  surveyGroupId: z.string().uuid().nullable(),
  surveyVersion: z.number().int().nonnegative(),
  completed: z.boolean(),
  submittedAt: z.string().nullable(),
  elapsedTimeSec: z.number().int().nonnegative().nullable(),
  totalScore: z.number().nullable(),
});

export const surveyParticipationDraftSchema: z.ZodType<SurveyParticipationDraft> = z.object({
  responseId: z.string().uuid(),
  surveyId: z.string().uuid(),
  surveyGroupId: z.string().uuid(),
  surveyVersion: z.number().int().nonnegative(),
  userId: z.string().uuid(),
  startedAt: z.string(),
  lastSavedAt: z.string(),
  answers: z.record(
    z.string(),
    z.object({
      questionId: z.string().uuid(),
      optionId: z.string().uuid(),
      answeredAt: z.string(),
    }),
  ),
});
