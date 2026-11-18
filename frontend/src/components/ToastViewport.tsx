import { Icon } from './Icons';

export type ToastKind = 'success' | 'warning' | 'error';
export type Toast = { id: number; kind: ToastKind; title: string; message: string };

type ToastViewportProps = {
  toasts: Toast[];
  onDismiss: (id: number) => void;
};

export function ToastViewport({ toasts, onDismiss }: ToastViewportProps) {
  return (
    <div className="toast-viewport" role="region" aria-label="Notificaciones" aria-live="polite">
      {toasts.map((toast) => (
        <div key={toast.id} className={`toast toast-${toast.kind}`} role={toast.kind === 'error' ? 'alert' : 'status'}>
          <span className="toast-icon"><Icon name={toast.kind === 'success' ? 'check' : toast.kind === 'warning' ? 'warning' : 'close'} size={17} /></span>
          <div className="toast-copy"><strong>{toast.title}</strong><span>{toast.message}</span></div>
          <button className="toast-close" type="button" aria-label={`Cerrar notificación: ${toast.title}`} onClick={() => onDismiss(toast.id)}><Icon name="close" size={15} /></button>
        </div>
      ))}
    </div>
  );
}
