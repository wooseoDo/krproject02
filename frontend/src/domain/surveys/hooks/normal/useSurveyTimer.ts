import { useEffect, useState } from 'react';

export function useSurveyTimer(startedAt: string | null) {
  const [elapsedTimeSec, setElapsedTimeSec] = useState(0);

  useEffect(() => {
    if (!startedAt) {
      setElapsedTimeSec(0);
      return undefined;
    }

    const startedAtTime = new Date(startedAt).getTime();
    const updateElapsedTime = () => {
      setElapsedTimeSec(Math.max(0, Math.floor((Date.now() - startedAtTime) / 1000)));
    };

    updateElapsedTime();
    const timerId = window.setInterval(updateElapsedTime, 1000);

    return () => window.clearInterval(timerId);
  }, [startedAt]);

  return elapsedTimeSec;
}
