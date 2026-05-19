import { api } from '../../../../common/api/client/apiClient';
import { surveyListPageResponseSchema } from '../../types/schemas';
import type { SurveyListPageResponse, SurveyListQueryParams } from '../../types/types';
import { SURVEY_API_URLS } from '../apiUrls';

function createSurveyListSearchParams({ page, pageSize, filters }: SurveyListQueryParams) {
  // Send only active filters so the backend can apply indexed paging cleanly.
  const params = new URLSearchParams({
    page: String(page),
    size: String(pageSize),
  });

  Object.entries(filters).forEach(([key, value]) => {
    if (value !== '') {
      params.set(key, value);
    }
  });

  return params;
}

export async function fetchNormalSurveys(params: SurveyListQueryParams): Promise<SurveyListPageResponse> {
  // Fetch normal-user visible surveys and validate the backend response.
  const searchParams = createSurveyListSearchParams(params);
  const response = await api.get<unknown>(`${SURVEY_API_URLS.NORMAL.BASE}?${searchParams.toString()}`);
  return surveyListPageResponseSchema.parse(response.data);
}
