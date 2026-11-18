import type { AntPointsResponse, TaxpayerDetails, VehicleDetails } from '../api/types';

export type QueryState<T> = { data: T | null; loading: boolean; error: string | null };

export type TaxpayerQueryState = QueryState<TaxpayerDetails>;
export type VehicleQueryState = QueryState<VehicleDetails>;
export type AntQueryState = QueryState<AntPointsResponse>;
