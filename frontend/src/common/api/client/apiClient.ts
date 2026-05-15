export interface ApiErrorBody {
  code?: string;
  message?: string;
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
  try {
    return (await response.json()) as ApiErrorBody;
  } catch {
    return null;
  }
}

export async function postJson<TResponse, TPayload>(
  url: string,
  payload: TPayload,
): Promise<TResponse> {
  const response = await fetch(url, {
    method: 'POST',
    headers: JSON_HEADERS,
    body: JSON.stringify(payload),
  });

  if (!response.ok) {
    throw new ApiError(response.status, await parseErrorBody(response), '요청을 처리하지 못했습니다.');
  }

  return (await response.json()) as TResponse;
}
