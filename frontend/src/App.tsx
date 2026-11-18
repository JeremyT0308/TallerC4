import { useMemo, useState } from 'react';
import { ApiRequestError, getAntPoints, getTaxpayer, getVehicle } from './api/client';
import type { AntPointsResponse, TaxpayerDetails, VehicleDetails } from './api/types';

type QueryState<T> = { data: T | null; loading: boolean; error: string | null };
const emptyState = <T,>(): QueryState<T> => ({ data: null, loading: false, error: null });

function readableError(error: unknown) {
  if (error instanceof ApiRequestError) {
    if (error.status === 404) return 'No encontramos información para los datos ingresados.';
    if (error.status === 503 || error.status === 0) return `Servicio no disponible: ${error.message}`;
    return error.message;
  }
  return 'Ocurrió un error inesperado. Intenta nuevamente.';
}

function formatDate(value?: string | null) {
  if (!value) return 'No disponible';
  return new Intl.DateTimeFormat('es-EC', { dateStyle: 'medium', timeStyle: 'short' }).format(new Date(value));
}

function StatusBadge({ children, tone = 'neutral' }: { children: React.ReactNode; tone?: 'neutral' | 'success' | 'warning' | 'danger' }) {
  return <span className={`badge badge-${tone}`}>{children}</span>;
}

function ErrorMessage({ children }: { children: string | null }) {
  return children ? <p className="message message-error" role="alert">{children}</p> : null;
}

export default function App() {
  const [email, setEmail] = useState('');
  const [ruc, setRuc] = useState('');
  const [plate, setPlate] = useState('');
  const [antIdentification, setAntIdentification] = useState('');
  const [scenario, setScenario] = useState('success');
  const [identityErrors, setIdentityErrors] = useState<{ email?: string; ruc?: string }>({});
  const [plateError, setPlateError] = useState<string | null>(null);
  const [taxpayer, setTaxpayer] = useState<QueryState<TaxpayerDetails>>(emptyState());
  const [vehicle, setVehicle] = useState<QueryState<VehicleDetails>>(emptyState());
  const [ant, setAnt] = useState<QueryState<AntPointsResponse>>(emptyState());

  const normalizedPlate = useMemo(() => plate.trim().replace(/[\s-]/g, '').toUpperCase(), [plate]);

  function validateIdentity() {
    const errors: typeof identityErrors = {};
    if (!email.trim() || !/^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/.test(email.trim())) errors.email = 'El correo electrónico es obligatorio y debe ser válido.';
    if (!/^\d{13}$/.test(ruc.trim())) errors.ruc = 'El RUC debe tener 13 dígitos.';
    setIdentityErrors(errors);
    return Object.keys(errors).length === 0;
  }

  function handleRuc(value: string) {
    const normalized = value.replace(/\D/g, '').slice(0, 13);
    setRuc(normalized);
    if (/^\d{10}/.test(normalized)) setAntIdentification(normalized.slice(0, 10));
  }

  async function submitTaxpayer(event: React.FormEvent) {
    event.preventDefault();
    if (!validateIdentity()) return;
    setTaxpayer({ data: null, loading: true, error: null });
    try {
      setTaxpayer({ data: await getTaxpayer(ruc.trim(), email.trim()), loading: false, error: null });
    } catch (error) {
      setTaxpayer({ data: null, loading: false, error: readableError(error) });
    }
  }

  async function submitVehicle(event: React.FormEvent) {
    event.preventDefault();
    if (!normalizedPlate || !/^[A-Z0-9]{3,8}$/.test(normalizedPlate)) {
      setPlateError('La placa debe tener entre 3 y 8 caracteres alfanuméricos.');
      return;
    }
    setPlateError(null);
    setVehicle({ data: null, loading: true, error: null });
    try {
      setVehicle({ data: await getVehicle(normalizedPlate), loading: false, error: null });
    } catch (error) {
      setVehicle({ data: null, loading: false, error: readableError(error) });
    }
  }

  async function submitAnt(event: React.FormEvent) {
    event.preventDefault();
    if (!/^\d{10}$/.test(antIdentification)) {
      setAnt({ data: null, loading: false, error: 'La identificación ANT debe tener 10 dígitos.' });
      return;
    }
    setAnt({ data: null, loading: true, error: null });
    try {
      setAnt({ data: await getAntPoints(antIdentification, scenario), loading: false, error: null });
    } catch (error) {
      setAnt({ data: null, loading: false, error: readableError(error) });
    }
  }

  return (
    <div className="app-shell">
      <header className="topbar">
        <div className="brand-mark" aria-hidden="true">TC</div>
        <div><span className="eyebrow">CONSULTAS ECUADOR</span><strong>TallerC4</strong></div>
        <span className="topbar-note">SRI · ANT</span>
      </header>

      <main className="page-content">
        <section className="hero">
          <div>
            <span className="eyebrow accent">DATOS EN UN SOLO LUGAR</span>
            <h1>TallerC4: consulta tributaria y vehicular, sin perder el contexto.</h1>
            <p className="hero-copy">Valida un RUC en SRI, revisa los pagos reportados para una placa y conoce el estado de la consulta ANT con su procedencia visible.</p>
          </div>
          <div className="hero-graphic" aria-hidden="true"><span>01</span><span>02</span><span>03</span></div>
        </section>

        <section className="panel workflow-panel">
          <div className="panel-heading"><div><span className="step-number">01</span><div><span className="eyebrow">IDENTIDAD TRIBUTARIA</span><h2>Comienza con el contribuyente</h2></div></div><span className="required-note">* Campos obligatorios</span></div>
          <form onSubmit={submitTaxpayer} noValidate>
            <div className="field-grid two-columns">
              <label className="field">Correo electrónico <span className="required">*</span>
                <input type="email" value={email} onChange={(event) => setEmail(event.target.value)} placeholder="persona@ejemplo.com" aria-invalid={Boolean(identityErrors.email)} />
                {identityErrors.email && <span className="field-error">{identityErrors.email}</span>}
              </label>
              <label className="field">RUC <span className="required">*</span>
                <input inputMode="numeric" value={ruc} onChange={(event) => handleRuc(event.target.value)} placeholder="13 dígitos" maxLength={13} aria-invalid={Boolean(identityErrors.ruc)} />
                {identityErrors.ruc && <span className="field-error">{identityErrors.ruc}</span>}
              </label>
            </div>
            <div className="form-actions"><span className="helper">La existencia y los datos se consultan directamente en SRI.</span><button type="submit" disabled={taxpayer.loading}>{taxpayer.loading ? 'Consultando…' : 'Consultar contribuyente'}</button></div>
          </form>
          <ErrorMessage>{taxpayer.error}</ErrorMessage>
          {taxpayer.data && <article className="result-card result-taxpayer" aria-label="Resultado del contribuyente"><div className="result-card-header"><div><span className="eyebrow">RESULTADO SRI</span><h3>{taxpayer.data.legalName}</h3></div><StatusBadge tone="success">{taxpayer.data.status}</StatusBadge></div><dl className="detail-grid"><div><dt>RUC</dt><dd>{taxpayer.data.ruc}</dd></div><div><dt>Tipo</dt><dd>{taxpayer.data.taxpayerType}</dd></div><div><dt>Régimen</dt><dd>{taxpayer.data.regime ?? 'No disponible'}</dd></div><div><dt>Actividad principal</dt><dd>{taxpayer.data.mainEconomicActivity ?? 'No disponible'}</dd></div></dl></article>}
        </section>

        <div className="content-grid">
          <section className="panel workflow-panel">
            <div className="panel-heading"><div><span className="step-number">02</span><div><span className="eyebrow">VEHÍCULO</span><h2>Revisa una placa</h2></div></div></div>
            <form onSubmit={submitVehicle} noValidate>
              <label className="field">Placa <span className="required">*</span><input value={plate} onChange={(event) => setPlate(event.target.value)} placeholder="ABC1234" maxLength={8} aria-invalid={Boolean(plateError)} />{plateError && <span className="field-error">{plateError}</span>}</label>
              <div className="form-actions"><span className="helper">SRI devuelve los pagos reportados disponibles.</span><button type="submit" disabled={vehicle.loading}>{vehicle.loading ? 'Consultando…' : 'Consultar vehículo'}</button></div>
            </form>
            <ErrorMessage>{vehicle.error}</ErrorMessage>
            {vehicle.data && <article className="result-card" aria-label="Resultado del vehículo"><div className="result-card-header"><div><span className="eyebrow">RESULTADO SRI</span><h3>{vehicle.data.plate}</h3></div><StatusBadge tone="success">Encontrado</StatusBadge></div><dl className="detail-grid"><div><dt>Marca</dt><dd>{vehicle.data.brand ?? 'No disponible en este reporte'}</dd></div><div><dt>Modelo</dt><dd>{vehicle.data.model ?? 'No disponible en este reporte'}</dd></div><div><dt>Año</dt><dd>{vehicle.data.modelYear ?? 'No disponible'}</dd></div><div><dt>Pagos reportados</dt><dd>{vehicle.data.paymentCount}</dd></div></dl></article>}
          </section>

          <section className="panel workflow-panel ant-panel">
            <div className="panel-heading"><div><span className="step-number">03</span><div><span className="eyebrow">MOVILIDAD</span><h2>Consulta puntos ANT</h2></div></div></div>
            <form onSubmit={submitAnt} noValidate>
              <label className="field">Identificación para ANT <span className="required">*</span><input inputMode="numeric" value={antIdentification} onChange={(event) => setAntIdentification(event.target.value.replace(/\D/g, '').slice(0, 10))} placeholder="10 dígitos" maxLength={10} /></label>
              <label className="field">Escenario de demostración <select value={scenario} onChange={(event) => setScenario(event.target.value)}><option value="success">Éxito</option><option value="no-results">Sin resultados</option><option value="bad-gateway">HTTP 502</option><option value="timeout">Timeout</option><option value="invalid-response">Respuesta inválida</option></select><span className="select-note">Se usa cuando el backend está en modo simulado.</span></label>
              <div className="form-actions"><span className="helper">La fuente y el estado stale aparecerán en el resultado.</span><button type="submit" disabled={ant.loading}>{ant.loading ? 'Consultando…' : 'Consultar puntos ANT'}</button></div>
            </form>
            <ErrorMessage>{ant.error}</ErrorMessage>
            {ant.data && <article className="result-card result-ant" aria-label="Resultado ANT"><div className="result-card-header"><div><span className="eyebrow">RESULTADO ANT</span>{ant.data.points === null ? <h3>Sin puntos disponibles</h3> : <h3>{ant.data.points} puntos</h3>}</div><div className="badge-stack"><StatusBadge tone={ant.data.stale ? 'warning' : ant.data.simulated ? 'neutral' : 'success'}>{ant.data.stale ? 'Datos stale' : ant.data.simulated ? 'Simulado' : 'En vivo'}</StatusBadge>{ant.data.stale && <span className="stale-dot" aria-label="Advertencia de datos stale">●</span>}</div></div><p className="result-note">{ant.data.note ?? `Fuente: ${ant.data.source}`}</p><dl className="detail-grid"><div><dt>Fuente</dt><dd>{ant.data.source}</dd></div><div><dt>Consultado</dt><dd>{formatDate(ant.data.retrievedAt)}</dd></div>{ant.data.staleSince && <div><dt>Datos stale desde</dt><dd>{formatDate(ant.data.staleSince)}</dd></div>}</dl>{ant.data.stale && <p className="stale-warning" role="status"><strong>Advertencia:</strong> ANT no respondió. Este resultado proviene del último cache elegible y puede estar desactualizado.</p>}</article>}
          </section>
        </div>

        <footer className="page-footer"><span>TallerC4 · Diseño y Arquitectura de Software</span><span>Los datos de proveedores se muestran con su procedencia.</span></footer>
      </main>
    </div>
  );
}
