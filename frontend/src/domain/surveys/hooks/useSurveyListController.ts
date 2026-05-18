import { useEffect, useMemo, useState } from 'react';
import {
  createSurveyListDefaultFilters,
  SURVEY_PAGE_SIZE,
} from '../config/surveyListConfig';
import {
  buildSurveyListItems,
  filterSurveyListItems,
  paginateSurveyListItems,
} from '../model/list';
import {
  readSurveyListPageState,
  saveSurveyListPageState,
} from '../model/paginationSession';
import type {
  SurveyListFilters,
  SurveyListItemResponse,
  SurveyScope,
} from '../types/types';

interface UseSurveyListControllerOptions {
  scope: SurveyScope;
  fetchSurveys: () => Promise<SurveyListItemResponse[]>;
}

export function useSurveyListController({ scope, fetchSurveys }: UseSurveyListControllerOptions) {
  const initialState = useMemo(() => readSurveyListPageState(scope), [scope]);
  const [surveys, setSurveys] = useState<SurveyListItemResponse[]>([]);
  const [filters, setFilters] = useState<SurveyListFilters>(initialState.filters);
  const [page, setPage] = useState(initialState.page);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let mounted = true;

    async function loadSurveys() {
      setIsLoading(true);
      setError('');

      try {
        const data = await fetchSurveys();

        if (mounted) {
          setSurveys(data);
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
  }, [fetchSurveys]);

  useEffect(() => {
    saveSurveyListPageState(scope, {
      page,
      pageSize: SURVEY_PAGE_SIZE,
      filters,
    });
  }, [filters, page, scope]);

  const sortedItems = useMemo(() => buildSurveyListItems(surveys), [surveys]);
  const filteredItems = useMemo(() => filterSurveyListItems(sortedItems, filters), [filters, sortedItems]);
  const totalPages = Math.max(1, Math.ceil(filteredItems.length / SURVEY_PAGE_SIZE));
  const safePage = Math.min(page, totalPages);
  const pageItems = useMemo(
    () => paginateSurveyListItems(filteredItems, safePage, SURVEY_PAGE_SIZE),
    [filteredItems, safePage],
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
    totalItems: filteredItems.length,
    totalPages,
    pageItems,
    isLoading,
    error,
    updateFilter,
    resetFilters,
    setPage,
  };
}
