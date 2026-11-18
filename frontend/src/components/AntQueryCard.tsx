import type { FormEvent } from 'react';
import type { AntPointsResponse } from '../api/types';
import { Icon } from './Icons';
import { LoadingButton } from './LoadingButton';
import { QuerySectionHeader } from './QuerySectionHeader';
import { InlineMessage, ResultCard, StatusBadge } from './ResultCard';
import type { AntQueryState } from './types';

type AntQueryCardProps = {
  state: AntQueryState;
  identification: string;
  scenario: string;
  onIdentificationChange: (value: string) => void;
  onScenarioChange: (value: string) => void;
  onSubmit: (event: FormEvent<HTMLFormElement>) => void;
};

function formatDate(value?: string | null) {
  if (!value) return 'No disponible';
  return new Intl.DateTimeFormat('es-EC', { dateStyle: 'medium', timeStyle: 'short' }).format(new Date(value));
}

export function AntQueryCard({ state, identification, scenario, onIdentificationChange, onScenarioChange, onSubmit }: AntQueryCardProps) {
  const data: AntPointsResponse | null = state.data;
  return (
    <section className="panel workflow-panel ant-panel reveal" id="ant">
      <QuerySectionHeader titleId="ant-title" step="03" eyebrow="MOVILIDAD" title="Consulta puntos ANT" description="Conoce los puntos y la procedencia del resultado." icon="shield" />
      <form onSubmit={onSubmit} noValidate>
        <label className="field"><span>Identificación para ANT <span className="required">*</span></span><div className="input-with-icon"><Icon name="person" size={16} /><input inputMode="numeric" value={identification} onChange={(event) => onIdentificationChange(event.target.value)} placeholder="10 dígitos" maxLength={10} /></div></label>
        <label className="field"><span>Escenario de demostración</span><select value={scenario} onChange={(event) => onScenarioChange(event.target.value)}><option value="success">Éxito</option><option value="no-results">Sin resultados</option><option value="bad-gateway">HTTP 502</option><option value="timeout">Timeout</option><option value="invalid-response">Respuesta inválida</option></select><span className="select-note">Se usa cuando el backend está en modo simulado.</span></label>
        <div className="form-actions"><span className="helper">{state.loading ? <><Icon name="spinner" size={14} /> Consultando datos…</> : <><Icon name="shield" size={14} /> La fuente y el estado stale aparecerán en el resultado.</>}</span><LoadingButton type="submit" loading={state.loading} loadingLabel="Consultando puntos ANT…">Consultar puntos ANT <Icon name="arrow" size={16} /></LoadingButton></div>
      </form>
      <InlineMessage>{state.error}</InlineMessage>
      {data && <ResultCard label="Resultado ANT" className="result-ant" summary={<><div><span className="eyebrow">RESULTADO ANT</span>{data.points === null ? <h3>Sin puntos disponibles</h3> : <h3>{data.points} puntos</h3>}</div><div className="badge-stack"><StatusBadge tone={data.stale ? 'warning' : data.simulated ? 'neutral' : 'success'}>{data.stale ? 'Datos stale' : data.simulated ? 'Simulado' : 'En vivo'}</StatusBadge>{data.stale && <span className="stale-dot" aria-label="Advertencia de datos stale">●</span>}</div></>}><p className="result-note">{data.note ?? `Fuente: ${data.source}`}</p><dl className="detail-grid"><div><dt>Fuente</dt><dd>{data.source}</dd></div><div><dt>Consultado</dt><dd>{formatDate(data.retrievedAt)}</dd></div>{data.staleSince && <div><dt>Datos stale desde</dt><dd>{formatDate(data.staleSince)}</dd></div>}</dl>{data.stale && <p className="stale-warning" role="status"><strong>Advertencia:</strong> ANT no respondió. Este resultado proviene del último cache elegible y puede estar desactualizado.</p>}</ResultCard>}
    </section>
  );
}
