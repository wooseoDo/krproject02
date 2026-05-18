export interface ApiErrorBody {
  code?: string;
  message?: string;
}

export interface ApiResponse<TData> {
  data: TData;
}

export class ApiError extends Error {
  status: number;
  body: ApiErrorBody | null;

  constructor(status: number, body: ApiErrorBody | null, fallbackMessage: string) {
    super(body?.message || fallbackMessage);
    this.name = 'ApiError';
    this.status = status;
    this.body = body;
  }
}

const JSON_HEADERS = {
  'Content-Type': 'application/json',
} as const;

async function parseErrorBody(response: Response): Promise<ApiErrorBody | null> {
  // Safely parse optional JSON error bodies from the backend.
  try {
    return (await response.json()) as ApiErrorBody;
  } catch {
    return null;
  }
}

async function requestJson<TResponse, TPayload>(
  method: 'POST',
  url: string,
  payload: TPayload,
): Promise<ApiResponse<TResponse>> {
  // Normalize fetch responses into the same { data } shape used by domain APIs.
  const response = await fetch(url, {
    method,
    headers: JSON_HEADERS,
    body: JSON.stringify(payload),
  });

  if (!response.ok) {
    throw new ApiError(response.status, await parseErrorBody(response), '요청을 처리하지 못했습니다.');
  }

  return {
    data: (await response.json()) as TResponse,
  };
}

export const api = {
  post<TResponse, TPayload>(url: string, payload: TPayload): Promise<ApiResponse<TResponse>> {
    // Send a JSON POST request and return an Axios-like response object.
    return requestJson<TResponse, TPayload>('POST', url, payload);
  },
};
