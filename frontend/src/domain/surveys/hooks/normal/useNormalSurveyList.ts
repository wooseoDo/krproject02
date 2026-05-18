import { useCallback } from 'react';
import { fetchNormalSurveys } from '../../api/normal/api';
import { useSurveyListController } from '../useSurveyListController';

export function useNormalSurveyList() {
  const fetchSurveys = useCallback(() => fetchNormalSurveys(), []);
  return useSurveyListController({ scope: 'normal', fetchSurveys });
}
