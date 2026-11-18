import { useState, type ReactNode } from 'react';
import { Icon } from './Icons';

export type BadgeTone = 'neutral' | 'success' | 'warning' | 'danger';

export function StatusBadge({ children, tone = 'neutral' }: { children: ReactNode; tone?: BadgeTone }) {
  return <span className={`badge badge-${tone}`}>{children}</span>;
}

export function InlineMessage({ children }: { children: string | null }) {
  return children ? <p className="message message-error" role="alert"><Icon name="warning" size={16} /><span>{children}</span></p> : null;
}

type ResultCardProps = {
  label: string;
  className?: string;
  summary: ReactNode;
  children: ReactNode;
};

export function ResultCard({ label, className = '', summary, children }: ResultCardProps) {
  const [open, setOpen] = useState(false);
  return (
    <article className={`result-card interactive-result ${className}`} aria-label={label}>
      <details open={open}>
        <summary className="result-summary" onClick={(event) => { event.preventDefault(); setOpen((current) => !current); }}>
          <span className="result-summary-main">{summary}</span>
          <span className="result-toggle">{open ? 'Ocultar detalles' : 'Ver detalles'} <Icon name={open ? 'chevron-up' : 'chevron-down'} size={15} /></span>
        </summary>
        <div className="result-details">{children}</div>
      </details>
    </article>
  );
}
