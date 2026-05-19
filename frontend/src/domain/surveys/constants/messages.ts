export const SURVEY_MESSAGES = {
  LOAD_ERROR: '조사지 목록을 불러오지 못했습니다.',
  EMPTY: '조회된 조사지가 없습니다.',
  CREATE_ERROR: '조사지 저장에 실패했습니다.',
  DETAIL_LOAD_ERROR: '조사지 상세 정보를 불러오지 못했습니다.',
  UPDATE_ERROR: '조사지 수정에 실패했습니다.',
  PARTICIPATION_LOAD_ERROR: '참여 정보를 불러오지 못했습니다.',
  PARTICIPATION_SUBMIT_ERROR: '조사지를 제출하지 못했습니다.',
  PARTICIPATION_ALREADY_SUBMITTED: '이미 참여한 조사지입니다.',
  createSuccess: (title: string) => `조사지 "${title}"가 저장되었습니다.`,
  updateSuccess: (title: string) => `조사지 "${title}"가 수정되었습니다.`,
} as const;
