import type { ButtonHTMLAttributes, ReactNode } from 'react';

type LoadingButtonProps = ButtonHTMLAttributes<HTMLButtonElement> & {
  loading: boolean;
  loadingLabel: string;
  children: ReactNode;
};

export function LoadingButton({ loading, loadingLabel, children, disabled, ...props }: LoadingButtonProps) {
  return (
    <button {...props} disabled={loading || disabled} aria-busy={loading}>
      {loading && <span className="button-spinner" aria-hidden="true" />}
      <span>{loading ? loadingLabel : children}</span>
    </button>
  );
}
