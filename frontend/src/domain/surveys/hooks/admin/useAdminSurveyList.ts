import { useCallback } from 'react';
import { fetchAdminSurveys } from '../../api/admin/api';
import { useSurveyListController } from '../useSurveyListController';

export function useAdminSurveyList() {
  const fetchSurveys = useCallback(() => fetchAdminSurveys(), []);
  return useSurveyListController({ scope: 'admin', fetchSurveys });
}
