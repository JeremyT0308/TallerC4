import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import App from './App';

describe('TallerC4 app shell', () => {
  beforeEach(() => {
    vi.restoreAllMocks();
  });

  it('shows the Spanish query heading and required fields', () => {
    render(<App />);
    expect(screen.getByRole('heading', { name: /tallerc4/i })).toBeInTheDocument();
    expect(screen.getByLabelText(/correo/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/ruc/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/placa/i)).toBeInTheDocument();
  });

  it('shows validation feedback without calling the API', () => {
    const fetchSpy = vi.spyOn(globalThis, 'fetch');
    render(<App />);
    fireEvent.click(screen.getByRole('button', { name: /consultar contribuyente/i }));
    expect(screen.getByText(/correo electrónico es obligatorio/i)).toBeInTheDocument();
    expect(screen.getByText(/ruc debe tener 13 dígitos/i)).toBeInTheDocument();
    expect(fetchSpy).not.toHaveBeenCalled();
  });

  it('runs the complete taxpayer, vehicle and ANT workflow', async () => {
    const responses = [
      { ruc: '1716537341001', legalName: 'PERSONA DE PRUEBA', status: 'ACTIVO', taxpayerType: 'PERSONA NATURAL', regime: 'GENERAL', mainEconomicActivity: 'SERVICIOS', category: null },
      { plate: 'PEI7954', brand: null, model: null, modelYear: null, paymentCount: 2 },
      { identification: '1716537341', points: 30, source: 'ANT_SIMULATED', simulated: true, stale: false, retrievedAt: '2026-11-17T20:00:00Z', staleSince: null, note: 'Resultado de demostración' },
    ];
    vi.spyOn(globalThis, 'fetch').mockImplementation(async () => new Response(JSON.stringify(responses.shift()), { status: 200, headers: { 'Content-Type': 'application/json' } }));
    render(<App />);
    fireEvent.change(screen.getByLabelText(/correo/i), { target: { value: 'persona@example.com' } });
    fireEvent.change(screen.getByLabelText(/ruc/i), { target: { value: '1716537341001' } });
    fireEvent.click(screen.getByRole('button', { name: /consultar contribuyente/i }));
    expect(await screen.findByText('PERSONA DE PRUEBA')).toBeInTheDocument();
    fireEvent.change(screen.getByLabelText(/placa/i), { target: { value: 'PEI7954' } });
    fireEvent.click(screen.getByRole('button', { name: /consultar vehículo/i }));
    expect(await screen.findByText('PEI7954')).toBeInTheDocument();
    fireEvent.click(screen.getByRole('button', { name: /consultar puntos ant/i }));
    expect(await screen.findByText('30 puntos')).toBeInTheDocument();
    expect(screen.getByText('Simulado', { exact: true })).toBeInTheDocument();
  });

  it('announces stale ANT data and unavailable errors', async () => {
    vi.spyOn(globalThis, 'fetch').mockResolvedValue(new Response(JSON.stringify({
      identification: '1716537341', points: 21.5, source: 'ANT', simulated: false, stale: true,
      retrievedAt: '2026-11-10T20:00:00Z', staleSince: '2026-11-10T20:15:00Z', note: 'Cache ANT',
    }), { status: 200, headers: { 'Content-Type': 'application/json' } }));
    render(<App />);
    fireEvent.change(screen.getByLabelText(/ruc/i), { target: { value: '1716537341001' } });
    fireEvent.click(screen.getByRole('button', { name: /consultar puntos ant/i }));
    expect(await screen.findByText('Datos stale', { exact: true })).toBeInTheDocument();
    expect(screen.getByText('21.5 puntos')).toBeInTheDocument();

    vi.restoreAllMocks();
    vi.spyOn(globalThis, 'fetch').mockResolvedValue(new Response(JSON.stringify({ code: 'SERVICE_UNAVAILABLE', message: 'ANT no está disponible y no existe un resultado ANT utilizable.', status: 503, timestamp: '2026-11-17T20:00:00Z', path: '/api/v1/ant/points' }), { status: 503, headers: { 'Content-Type': 'application/json' } }));
    fireEvent.click(screen.getByRole('button', { name: /consultar puntos ant/i }));
    await waitFor(() => expect(screen.getByText(/servicio no disponible/i)).toBeInTheDocument());
  });
});
