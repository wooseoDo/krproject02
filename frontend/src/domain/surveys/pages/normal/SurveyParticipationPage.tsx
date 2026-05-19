import { NormalSurveyParticipation } from '../../components/normal/NormalSurveyParticipation';

function getSurveyIdFromPath(pathname: string) {
  const match = pathname.match(/^\/surveys\/([^/]+)\/participate$/);
  return match?.[1] ?? null;
}

export default function SurveyParticipationPage() {
  const surveyId = getSurveyIdFromPath(window.location.pathname);

  if (!surveyId) {
    return (
      <main className="route-notice-shell">
        <section className="route-notice-panel">
          <p className="section-label">NOT FOUND</p>
          <h1>조사지를 찾을 수 없습니다</h1>
          <p>
            <a href="/surveys">조사지 목록</a>
          </p>
        </section>
      </main>
    );
  }

  return <NormalSurveyParticipation surveyId={surveyId} />;
}
