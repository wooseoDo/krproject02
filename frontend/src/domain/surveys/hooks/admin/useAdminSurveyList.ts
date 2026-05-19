import { useCallback } from 'react';
import { fetchAdminSurveys } from '../../api/admin/api';
import { useSurveyListController } from '../useSurveyListController';

export function useAdminSurveyList() {
  const fetchSurveys = useCallback((params: Parameters<typeof fetchAdminSurveys>[0]) => fetchAdminSurveys(params), []);
  return useSurveyListController({ scope: 'admin', fetchSurveys });
}
