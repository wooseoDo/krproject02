import type { ReactNode } from 'react';
import { AuthHeader } from '../../../../common/components/layout/AuthHeader';

interface DashboardMetric {
  label: string;
  value: string;
}

interface DashboardViewProps {
  variant?: string;
  sectionLabel: string;
  title: string;
  description: string;
  accountType: string;
  birthDate: string;
  accountInfo: string;
  dashboardPath: string;
  surveyPath: string;
  metrics: DashboardMetric[];
  panelTitle: string;
  panelItems: string[];
  children?: ReactNode;
  onLogout: () => void;
}

export function DashboardView({
  variant,
  sectionLabel,
  title,
  description,
  accountType,
  birthDate,
  accountInfo,
  dashboardPath,
  surveyPath,
  metrics,
  panelTitle,
  panelItems,
  children,
  onLogout,
}: DashboardViewProps) {
  const variantClassName = variant ? ` dashboard-shell--${variant}` : '';

  return (
    <main className={`dashboard-shell${variantClassName}`}>
      <AuthHeader
        accountType={accountType}
        birthDate={birthDate}
        accountInfo={accountInfo}
        dashboardPath={dashboardPath}
        surveyPath={surveyPath}
        onLogout={onLogout}
      />

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

      {children}

      <section className="work-panel">
        <h2>{panelTitle}</h2>
        <div className="status-list">
          {panelItems.map((item) => (
            <p key={item}>{item}</p>
          ))}
        </div>
      </section>
    </main>
  );
}
