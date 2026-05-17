export interface ApiErrorBody {
  code?: string;
  message?: string;
}

export class ApiError extends Error {
  status: number;
  body: ApiErrorBody | null;

  // API 실패 상태와 응답 본문을 에러 객체에 담습니다.
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

// 실패 응답의 JSON 본문을 안전하게 파싱합니다.
async function parseErrorBody(response: Response): Promise<ApiErrorBody | null> {
  try {
    return (await response.json()) as ApiErrorBody;
  } catch {
    return null;
  }
}

// JSON payload를 POST로 전송하고 성공 응답을 지정한 타입으로 반환합니다.
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
