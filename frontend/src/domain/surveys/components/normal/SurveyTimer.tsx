interface SurveyTimerProps {
  elapsedTimeSec: number;
}

function formatElapsedTime(totalSeconds: number) {
  const minutes = Math.floor(totalSeconds / 60);
  const seconds = totalSeconds % 60;
  return `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;
}

export function SurveyTimer({ elapsedTimeSec }: SurveyTimerProps) {
  return <strong>{formatElapsedTime(elapsedTimeSec)}</strong>;
}
