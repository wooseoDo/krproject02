import { api } from '../../../../common/api/client/apiClient';
import { surveyListResponseSchema } from '../../types/schemas';
import type { SurveyListItemResponse } from '../../types/types';
import { SURVEY_API_URLS } from '../apiUrls';

export async function fetchAdminSurveys(): Promise<SurveyListItemResponse[]> {
  // Fetch admin-visible surveys and validate the backend response.
  const response = await api.get<unknown>(SURVEY_API_URLS.ADMIN.BASE);
  return surveyListResponseSchema.parse(response.data);
}
