import { useEffect, useMemo, useState } from 'react';
import {
  createSurveyListDefaultFilters,
  SURVEY_PAGE_SIZE,
} from '../config/surveyListConfig';
import { buildSurveyListItems } from '../model/list';
import {
  readSurveyListPageState,
  saveSurveyListPageState,
} from '../model/paginationSession';
import type {
  SurveyListFilters,
  SurveyListPageResponse,
  SurveyListQueryParams,
  SurveyScope,
} from '../types/types';

interface UseSurveyListControllerOptions {
  scope: SurveyScope;
  fetchSurveys: (params: SurveyListQueryParams) => Promise<SurveyListPageResponse>;
}

export function useSurveyListController({ scope, fetchSurveys }: UseSurveyListControllerOptions) {
  const initialState = useMemo(() => readSurveyListPageState(scope), [scope]);
  const [response, setResponse] = useState<SurveyListPageResponse>({
    items: [],
    page: initialState.page,
    size: SURVEY_PAGE_SIZE,
    totalItems: 0,
    totalPages: 1,
  });
  const [filters, setFilters] = useState<SurveyListFilters>(initialState.filters);
  const [page, setPage] = useState(initialState.page);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');
  const [reloadKey, setReloadKey] = useState(0);

  useEffect(() => {
    let mounted = true;

    async function loadSurveys() {
      setIsLoading(true);
      setError('');

      try {
        const data = await fetchSurveys({
          page,
          pageSize: SURVEY_PAGE_SIZE,
          filters,
        });

        if (mounted) {
          setResponse(data);
        }
      } catch {
        if (mounted) {
          setError('LOAD_ERROR');
        }
      } finally {
        if (mounted) {
          setIsLoading(false);
        }
      }
    }

    void loadSurveys();

    return () => {
      mounted = false;
    };
  }, [fetchSurveys, filters, page, reloadKey]);

  useEffect(() => {
    saveSurveyListPageState(scope, {
      page,
      pageSize: SURVEY_PAGE_SIZE,
      filters,
    });
  }, [filters, page, scope]);

  const totalPages = response.totalPages;
  const safePage = Math.min(page, totalPages);
  const pageItems = useMemo(
    () => buildSurveyListItems(response.items, response.page, response.size),
    [response.items, response.page, response.size],
  );

  const updateFilter = (key: keyof SurveyListFilters, value: string) => {
    setFilters((current) => ({ ...current, [key]: value }));
    setPage(1);
  };

  const resetFilters = () => {
    setFilters(createSurveyListDefaultFilters());
    setPage(1);
  };

  return {
    filters,
    page: safePage,
    pageSize: SURVEY_PAGE_SIZE,
    totalItems: response.totalItems,
    totalPages,
    pageItems,
    isLoading,
    error,
    updateFilter,
    resetFilters,
    refresh: () => setReloadKey((current) => current + 1),
    setPage,
  };
}
