import { api } from '../../../../common/api/client/apiClient';
import { surveyListResponseSchema } from '../../types/schemas';
import type { SurveyListItemResponse } from '../../types/types';
import { SURVEY_API_URLS } from '../apiUrls';

export async function fetchNormalSurveys(): Promise<SurveyListItemResponse[]> {
  // Fetch normal-user visible surveys and validate the backend response.
  const response = await api.get<unknown>(SURVEY_API_URLS.NORMAL.BASE);
  return surveyListResponseSchema.parse(response.data);
}
