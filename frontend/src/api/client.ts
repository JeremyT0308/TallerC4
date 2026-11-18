import type { ApiError, AntPointsResponse, TaxpayerDetails, VehicleDetails } from './types';

export class ApiRequestError extends Error {
  readonly status: number;
  readonly code?: string;

  constructor(payload: ApiError, status: number) {
    super(payload.message ?? 'No fue posible completar la consulta.');
    this.name = 'ApiRequestError';
    this.status = status;
    this.code = payload.code;
  }
}

async function getJson<T>(path: string): Promise<T> {
  let response: Response;
  try {
    response = await fetch(path, { headers: { Accept: 'application/json' } });
  } catch {
    throw new ApiRequestError({ code: 'NETWORK_ERROR', message: 'No se pudo conectar con el backend.' }, 0);
  }
  const payload = (await response.json().catch(() => ({}))) as ApiError;
  if (!response.ok) throw new ApiRequestError(payload, response.status);
  return payload as T;
}

export function getTaxpayer(ruc: string, email: string) {
  return getJson<TaxpayerDetails>(`/api/v1/taxpayers/${encodeURIComponent(ruc)}?email=${encodeURIComponent(email)}`);
}

export function getVehicle(plate: string) {
  return getJson<VehicleDetails>(`/api/v1/vehicles/${encodeURIComponent(plate)}`);
}

export function getAntPoints(identification: string, scenario: string) {
  const params = new URLSearchParams({ identification });
  if (scenario) params.set('scenario', scenario);
  return getJson<AntPointsResponse>(`/api/v1/ant/points?${params.toString()}`);
}
