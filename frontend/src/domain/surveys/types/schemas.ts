import { z } from 'zod';

export const surveyStatusSchema = z.enum(['DRAFT', 'PUBLISHED', 'LOCKED', 'CLOSED']);

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
