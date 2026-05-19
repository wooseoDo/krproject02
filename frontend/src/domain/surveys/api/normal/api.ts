import { api } from '../../../../common/api/client/apiClient';
import {
  normalSurveyParticipationDetailResponseSchema,
  normalSurveyParticipationStartResponseSchema,
  normalSurveySubmitResponseSchema,
  surveyListPageResponseSchema,
} from '../../types/schemas';
import type {
  NormalSurveyParticipationDetailResponse,
  NormalSurveyParticipationStartRequest,
  NormalSurveyParticipationStartResponse,
  NormalSurveySubmitRequest,
  NormalSurveySubmitResponse,
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

export async function fetchNormalSurveys(params: SurveyListQueryParams): Promise<SurveyListPageResponse> {
  // Fetch normal-user visible surveys and validate the backend response.
  const searchParams = createSurveyListSearchParams(params);
  const response = await api.get<unknown>(`${SURVEY_API_URLS.NORMAL.BASE}?${searchParams.toString()}`);
  return surveyListPageResponseSchema.parse(response.data);
}

export async function fetchNormalSurveyParticipationDetail(
  surveyId: string,
): Promise<NormalSurveyParticipationDetailResponse> {
  const response = await api.get<unknown>(SURVEY_API_URLS.NORMAL.PARTICIPATION_DETAIL(surveyId));
  return normalSurveyParticipationDetailResponseSchema.parse(response.data);
}

export async function startNormalSurveyParticipation(
  surveyId: string,
  payload: NormalSurveyParticipationStartRequest,
): Promise<NormalSurveyParticipationStartResponse> {
  const response = await api.post<unknown, NormalSurveyParticipationStartRequest>(
    SURVEY_API_URLS.NORMAL.PARTICIPATION_START(surveyId),
    payload,
  );
  return normalSurveyParticipationStartResponseSchema.parse(response.data);
}

export async function submitNormalSurveyParticipation(
  responseId: string,
  payload: NormalSurveySubmitRequest,
): Promise<NormalSurveySubmitResponse> {
  const response = await api.post<unknown, NormalSurveySubmitRequest>(
    SURVEY_API_URLS.NORMAL.PARTICIPATION_SUBMIT(responseId),
    payload,
  );
  return normalSurveySubmitResponseSchema.parse(response.data);
}
