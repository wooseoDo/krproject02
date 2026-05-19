export const SURVEY_MESSAGES = {
  LOAD_ERROR: '조사지 목록을 불러오지 못했습니다.',
  EMPTY: '조회된 조사지가 없습니다.',
  CREATE_ERROR: '조사지 저장에 실패했습니다.',
  createSuccess: (title: string) => `조사지 "${title}"가 저장되었습니다.`,
} as const;
