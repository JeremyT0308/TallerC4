import { Icon, type IconName } from './Icons';

export type ProgressStepId = 'taxpayer' | 'vehicle' | 'ant';
export type ProgressStatus = 'idle' | 'loading' | 'success' | 'error';

type ProgressNavProps = {
  activeStep: ProgressStepId;
  statuses: Record<ProgressStepId, ProgressStatus>;
  onNavigate: (step: ProgressStepId) => void;
};

const steps: Array<{ id: ProgressStepId; label: string; icon: IconName }> = [
  { id: 'taxpayer', label: 'Contribuyente', icon: 'person' },
  { id: 'vehicle', label: 'Vehículo', icon: 'car' },
  { id: 'ant', label: 'Puntos ANT', icon: 'shield' },
];

export function ProgressNav({ activeStep, statuses, onNavigate }: ProgressNavProps) {
  return (
    <nav className="progress-nav" aria-label="Progreso de consultas">
      <div className="progress-line" aria-hidden="true" />
      {steps.map((step, index) => {
        const status = statuses[step.id];
        return (
          <button
            key={step.id}
            className={`progress-step progress-step-${status} ${activeStep === step.id ? 'progress-step-active' : ''}`}
            type="button"
            aria-label={`Ir a ${step.id === 'ant' ? 'ANT' : step.label.toLowerCase()}`}
            aria-current={activeStep === step.id ? 'step' : undefined}
            onClick={() => onNavigate(step.id)}
          >
            <span className="progress-step-index">{status === 'success' ? <Icon name="check" size={15} /> : String(index + 1).padStart(2, '0')}</span>
            <span className="progress-step-copy"><strong>{step.label}</strong><small>{status === 'loading' ? 'Consultando' : status === 'success' ? 'Listo' : status === 'error' ? 'Revisar' : 'Pendiente'}</small></span>
          </button>
        );
      })}
    </nav>
  );
}
