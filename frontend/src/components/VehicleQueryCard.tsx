import type { FormEvent } from 'react';
import type { VehicleDetails } from '../api/types';
import { Icon } from './Icons';
import { LoadingButton } from './LoadingButton';
import { QuerySectionHeader } from './QuerySectionHeader';
import { InlineMessage, ResultCard, StatusBadge } from './ResultCard';
import type { VehicleQueryState } from './types';

type VehicleQueryCardProps = {
  state: VehicleQueryState;
  plate: string;
  plateError: string | null;
  onPlateChange: (value: string) => void;
  onSubmit: (event: FormEvent<HTMLFormElement>) => void;
};

export function VehicleQueryCard({ state, plate, plateError, onPlateChange, onSubmit }: VehicleQueryCardProps) {
  const data: VehicleDetails | null = state.data;
  return (
    <section className="panel workflow-panel reveal" id="vehiculo">
      <QuerySectionHeader titleId="vehicle-title" step="02" eyebrow="VEHÍCULO" title="Revisa una placa" description="Consulta pagos reportados y datos disponibles." icon="car" />
      <form onSubmit={onSubmit} noValidate>
        <label className="field"><span>Placa <span className="required">*</span></span><div className="input-with-icon"><Icon name="car" size={16} /><input value={plate} onChange={(event) => onPlateChange(event.target.value)} placeholder="ABC1234" maxLength={8} aria-invalid={Boolean(plateError)} /></div>{plateError && <span className="field-error">{plateError}</span>}</label>
        <div className="form-actions"><span className="helper"><Icon name="check" size={14} /> SRI devuelve los pagos reportados disponibles.</span><LoadingButton type="submit" loading={state.loading} loadingLabel="Consultando vehículo…">Consultar vehículo <Icon name="arrow" size={16} /></LoadingButton></div>
      </form>
      <InlineMessage>{state.error}</InlineMessage>
      {data && <ResultCard label="Resultado del vehículo" summary={<><div><span className="eyebrow">RESULTADO SRI</span><h3>{data.plate}</h3></div><StatusBadge tone="success">Encontrado</StatusBadge></>}><dl className="detail-grid"><div><dt>Marca</dt><dd>{data.brand ?? 'No disponible en este reporte'}</dd></div><div><dt>Modelo</dt><dd>{data.model ?? 'No disponible en este reporte'}</dd></div><div><dt>Año</dt><dd>{data.modelYear ?? 'No disponible'}</dd></div><div><dt>Pagos reportados</dt><dd>{data.paymentCount}</dd></div></dl></ResultCard>}
    </section>
  );
}
