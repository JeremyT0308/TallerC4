import { useEffect, useMemo, useRef, useState, type FormEvent } from 'react';
import { ApiRequestError, getAntPoints, getTaxpayer, getVehicle } from './api/client';
import type { AntPointsResponse, TaxpayerDetails, VehicleDetails } from './api/types';
import { AntQueryCard } from './components/AntQueryCard';
import { Icon } from './components/Icons';
import { ProgressNav, type ProgressStepId, type ProgressStatus } from './components/ProgressNav';
import { TaxpayerQueryCard } from './components/TaxpayerQueryCard';
import { ToastViewport, type Toast, type ToastKind } from './components/ToastViewport';
import type { QueryState } from './components/types';
import { VehicleQueryCard } from './components/VehicleQueryCard';

const emptyState = <T,>(): QueryState<T> => ({ data: null, loading: false, error: null });

function readableError(error: unknown) {
  if (error instanceof ApiRequestError) {
    if (error.status === 404) return 'No encontramos información para los datos ingresados.';
    if (error.status === 503 || error.status === 0) return `Servicio no disponible: ${error.message}`;
    return error.message;
  }
  return 'Ocurrió un error inesperado. Intenta nuevamente.';
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
  const [toasts, setToasts] = useState<Toast[]>([]);
  const toastSequence = useRef(0);

  const normalizedPlate = useMemo(() => plate.trim().replace(/[\s-]/g, '').toUpperCase(), [plate]);

  function pushToast(kind: ToastKind, title: string, message: string) {
    toastSequence.current += 1;
    const toast = { id: toastSequence.current, kind, title, message } satisfies Toast;
    setToasts((current) => [...current.slice(-2), toast]);
  }

  function dismissToast(id: number) {
    setToasts((current) => current.filter((toast) => toast.id !== id));
  }

  useEffect(() => {
    const currentToast = toasts[0];
    if (!currentToast) return undefined;
    const timeout = window.setTimeout(() => dismissToast(currentToast.id), 5200);
    return () => window.clearTimeout(timeout);
  }, [toasts]);

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

  async function submitTaxpayer(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!validateIdentity()) {
      pushToast('error', 'Revisa los datos', 'Completa un correo válido y un RUC de 13 dígitos.');
      return;
    }
    setTaxpayer({ data: null, loading: true, error: null });
    try {
      const data = await getTaxpayer(ruc.trim(), email.trim());
      setTaxpayer({ data, loading: false, error: null });
      pushToast('success', 'Contribuyente consultado', 'Datos recibidos desde SRI.');
    } catch (error) {
      const message = readableError(error);
      setTaxpayer({ data: null, loading: false, error: message });
      pushToast('error', 'Consulta de contribuyente no disponible', message);
    }
  }

  async function submitVehicle(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!normalizedPlate || !/^[A-Z0-9]{3,8}$/.test(normalizedPlate)) {
      const message = 'La placa debe tener entre 3 y 8 caracteres alfanuméricos.';
      setPlateError(message);
      pushToast('error', 'Revisa la placa', message);
      return;
    }
    setPlateError(null);
    setVehicle({ data: null, loading: true, error: null });
    try {
      const data = await getVehicle(normalizedPlate);
      setVehicle({ data, loading: false, error: null });
      pushToast('success', 'Vehículo consultado', 'Pagos reportados recibidos desde SRI.');
    } catch (error) {
      const message = readableError(error);
      setVehicle({ data: null, loading: false, error: message });
      pushToast('error', 'Consulta de vehículo no disponible', message);
    }
  }

  async function submitAnt(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!/^\d{10}$/.test(antIdentification)) {
      const message = 'La identificación ANT debe tener 10 dígitos.';
      setAnt({ data: null, loading: false, error: message });
      pushToast('error', 'Revisa la identificación', message);
      return;
    }
    setAnt({ data: null, loading: true, error: null });
    try {
      const data = await getAntPoints(antIdentification, scenario);
      setAnt({ data, loading: false, error: null });
      if (data.stale) {
        pushToast('warning', 'Resultado ANT en contingencia', 'Se muestra el último resultado válido con su fecha original.');
      } else if (data.points === null) {
        pushToast('warning', 'ANT sin resultados', 'No se reportaron puntos para esta identificación.');
      } else {
        pushToast('success', 'Consulta ANT lista', `Fuente ${data.source}.`);
      }
    } catch (error) {
      const message = readableError(error);
      setAnt({ data: null, loading: false, error: message });
      pushToast('error', 'Consulta ANT no disponible', 'La consulta falló; revisa el detalle mostrado en la tarjeta.');
    }
  }

  const activeStep: ProgressStepId = ant.loading || ant.data || ant.error ? 'ant' : vehicle.loading || vehicle.data || vehicle.error ? 'vehicle' : 'taxpayer';
  const statuses: Record<ProgressStepId, ProgressStatus> = {
    taxpayer: taxpayer.loading ? 'loading' : taxpayer.error ? 'error' : taxpayer.data ? 'success' : 'idle',
    vehicle: vehicle.loading ? 'loading' : vehicle.error ? 'error' : vehicle.data ? 'success' : 'idle',
    ant: ant.loading ? 'loading' : ant.error ? 'error' : ant.data ? 'success' : 'idle',
  };

  function handleNavigate(step: ProgressStepId) {
    const sectionId = step === 'taxpayer' ? 'contribuyente' : step === 'vehicle' ? 'vehiculo' : 'ant';
    const section = document.getElementById(sectionId);
    const reduceMotion = typeof window.matchMedia === 'function' && window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    section?.scrollIntoView({ behavior: reduceMotion ? 'auto' : 'smooth', block: 'start' });
  }

  return (
    <div className="app-shell">
      <header className="topbar">
        <div className="brand-mark" aria-hidden="true"><Icon name="spark" size={19} /></div>
        <div><span className="eyebrow">CONSULTAS ECUADOR</span><strong>TallerC4</strong></div>
        <span className="topbar-note"><span className="status-pulse" aria-hidden="true" /> 3 consultas conectadas</span>
      </header>

      <main className="page-content">
        <section className="hero reveal">
          <div>
            <span className="eyebrow accent">DATOS EN UN SOLO LUGAR</span>
            <h1>TallerC4: consulta Ecuador con contexto.</h1>
            <p className="hero-copy">Valida SRI, revisa una placa y consulta ANT con su fuente y estado visibles en todo momento.</p>
            <div className="hero-meta"><span><Icon name="check" size={14} /> SRI integrado</span><span><Icon name="shield" size={14} /> ANT trazable</span></div>
          </div>
          <div className="hero-graphic" aria-hidden="true"><span>01</span><span>02</span><span>03</span></div>
        </section>

        <ProgressNav activeStep={activeStep} statuses={statuses} onNavigate={handleNavigate} />

        <div className="query-stack">
          <TaxpayerQueryCard state={taxpayer} email={email} ruc={ruc} onEmailChange={setEmail} onRucChange={handleRuc} identityErrors={identityErrors} onSubmit={submitTaxpayer} />
          <div className="content-grid">
            <VehicleQueryCard state={vehicle} plate={plate} plateError={plateError} onPlateChange={setPlate} onSubmit={submitVehicle} />
            <AntQueryCard state={ant} identification={antIdentification} scenario={scenario} onIdentificationChange={(value) => setAntIdentification(value.replace(/\D/g, '').slice(0, 10))} onScenarioChange={setScenario} onSubmit={submitAnt} />
          </div>
        </div>

        <footer className="page-footer"><span>TallerC4 · Diseño y Arquitectura de Software</span><span>Los datos de proveedores se muestran con su procedencia.</span></footer>
      </main>
      <ToastViewport toasts={toasts} onDismiss={dismissToast} />
    </div>
  );
}
