export type TaxpayerDetails = {
  ruc: string;
  legalName: string;
  status: string;
  taxpayerType: string;
  regime?: string | null;
  mainEconomicActivity?: string | null;
  category?: string | null;
};

export type VehicleDetails = {
  plate: string;
  brand?: string | null;
  model?: string | null;
  modelYear?: number | null;
  paymentCount: number;
};

export type AntPointsResponse = {
  identification: string;
  points: number | null;
  source: 'ANT' | 'ANT_SIMULATED' | 'ANT_CACHE' | string;
  simulated: boolean;
  stale: boolean;
  retrievedAt: string;
  staleSince?: string | null;
  note?: string | null;
};

export type ApiError = {
  code?: string;
  message?: string;
  status?: number;
  timestamp?: string;
  path?: string;
};
