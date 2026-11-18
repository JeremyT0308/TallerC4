import type { FormEvent } from 'react';
import type { TaxpayerDetails } from '../api/types';
import { Icon } from './Icons';
import { LoadingButton } from './LoadingButton';
import { QuerySectionHeader } from './QuerySectionHeader';
import { InlineMessage, ResultCard, StatusBadge } from './ResultCard';
import type { TaxpayerQueryState } from './types';

type TaxpayerQueryCardProps = {
  state: TaxpayerQueryState;
  email: string;
  ruc: string;
  onEmailChange: (value: string) => void;
  onRucChange: (value: string) => void;
  identityErrors: { email?: string; ruc?: string };
  onSubmit: (event: FormEvent<HTMLFormElement>) => void;
};

export function TaxpayerQueryCard({ state, email, ruc, onEmailChange, onRucChange, identityErrors, onSubmit }: TaxpayerQueryCardProps) {
  const data: TaxpayerDetails | null = state.data;
  return (
    <section className="panel workflow-panel reveal" id="contribuyente">
      <QuerySectionHeader titleId="taxpayer-title" step="01" eyebrow="IDENTIDAD TRIBUTARIA" title="Comienza con el contribuyente" description="Valida existencia y datos básicos directamente con SRI." icon="person" required />
      <form onSubmit={onSubmit} noValidate>
        <div className="field-grid two-columns">
          <label className="field"><span>Correo electrónico <span className="required">*</span></span><div className="input-with-icon"><Icon name="mail" size={16} /><input type="email" value={email} onChange={(event) => onEmailChange(event.target.value)} placeholder="persona@ejemplo.com" aria-invalid={Boolean(identityErrors.email)} /></div>{identityErrors.email && <span className="field-error">{identityErrors.email}</span>}</label>
          <label className="field"><span>RUC <span className="required">*</span></span><div className="input-with-icon"><Icon name="shield" size={16} /><input inputMode="numeric" value={ruc} onChange={(event) => onRucChange(event.target.value)} placeholder="13 dígitos" maxLength={13} aria-invalid={Boolean(identityErrors.ruc)} /></div>{identityErrors.ruc && <span className="field-error">{identityErrors.ruc}</span>}</label>
        </div>
        <div className="form-actions"><span className="helper"><Icon name="check" size={14} /> La existencia y los datos se consultan directamente en SRI.</span><LoadingButton type="submit" loading={state.loading} loadingLabel="Consultando contribuyente…">Consultar contribuyente <Icon name="arrow" size={16} /></LoadingButton></div>
      </form>
      <InlineMessage>{state.error}</InlineMessage>
      {data && <ResultCard label="Resultado del contribuyente" className="result-taxpayer" summary={<><div><span className="eyebrow">RESULTADO SRI</span><h3>{data.legalName}</h3></div><StatusBadge tone="success">{data.status}</StatusBadge></>}><dl className="detail-grid"><div><dt>RUC</dt><dd>{data.ruc}</dd></div><div><dt>Tipo</dt><dd>{data.taxpayerType}</dd></div><div><dt>Régimen</dt><dd>{data.regime ?? 'No disponible'}</dd></div><div><dt>Actividad principal</dt><dd>{data.mainEconomicActivity ?? 'No disponible'}</dd></div></dl></ResultCard>}
    </section>
  );
}
