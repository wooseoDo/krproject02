import { useCallback } from 'react';
import { fetchNormalSurveys } from '../../api/normal/api';
import { useSurveyListController } from '../useSurveyListController';

export function useNormalSurveyList() {
  const fetchSurveys = useCallback((params: Parameters<typeof fetchNormalSurveys>[0]) => fetchNormalSurveys(params), []);
  return useSurveyListController({ scope: 'normal', fetchSurveys });
}
