interface DashboardMetric {
  label: string;
  value: string;
}

interface DashboardViewProps {
  variant?: string;
  sectionLabel: string;
  title: string;
  description: string;
  metrics: DashboardMetric[];
  panelTitle: string;
  panelItems: string[];
  onLogout: () => void;
}

export function DashboardView({
  variant,
  sectionLabel,
  title,
  description,
  metrics,
  panelTitle,
  panelItems,
  onLogout,
}: DashboardViewProps) {
  const variantClassName = variant ? ` dashboard-shell--${variant}` : '';

  return (
    <main className={`dashboard-shell${variantClassName}`}>
      <section className="dashboard-hero">
        <p className="section-label">{sectionLabel}</p>
        <h1>{title}</h1>
        <p>{description}</p>
      </section>

      <section className="dashboard-grid">
        {metrics.map((metric) => (
          <article className="metric-card" key={metric.label}>
            <span>{metric.label}</span>
            <strong>{metric.value}</strong>
          </article>
        ))}
      </section>

      <section className="work-panel">
        <h2>{panelTitle}</h2>
        <div className="status-list">
          {panelItems.map((item) => (
            <p key={item}>{item}</p>
          ))}
        </div>
      </section>

      <button className="secondary-action" type="button" onClick={onLogout}>
        로그아웃
      </button>
    </main>
  );
}
