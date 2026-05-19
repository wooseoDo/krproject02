import { api } from '../../../../common/api/client/apiClient';
import { adminSurveyCreateResponseSchema, surveyListPageResponseSchema } from '../../types/schemas';
import type {
  AdminSurveyCreateRequest,
  AdminSurveyCreateResponse,
  AdminSurveyUpdateRequest,
  AdminSurveyUpdateResponse,
  SurveyListPageResponse,
  SurveyListQueryParams,
} from '../../types/types';
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

export async function fetchAdminSurveys(params: SurveyListQueryParams): Promise<SurveyListPageResponse> {
  // Fetch admin-visible surveys and validate the backend response.
  const searchParams = createSurveyListSearchParams(params);
  const response = await api.get<unknown>(`${SURVEY_API_URLS.ADMIN.BASE}?${searchParams.toString()}`);
  return surveyListPageResponseSchema.parse(response.data);
}

export async function createAdminSurvey(payload: AdminSurveyCreateRequest): Promise<AdminSurveyCreateResponse> {
  const response = await api.post<unknown, AdminSurveyCreateRequest>(SURVEY_API_URLS.ADMIN.BASE, payload);
  return adminSurveyCreateResponseSchema.parse(response.data);
}

export async function updateAdminSurvey(
  surveyId: string,
  payload: AdminSurveyUpdateRequest,
): Promise<AdminSurveyUpdateResponse> {
  const response = await api.put<unknown, AdminSurveyUpdateRequest>(SURVEY_API_URLS.ADMIN.DETAIL(surveyId), payload);
  return adminSurveyCreateResponseSchema.parse(response.data);
}
