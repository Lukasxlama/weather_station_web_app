export interface TrendsSeriesModel
{
    temperature_c: { t: string; v: number }[];
    humidity_pct: { t: string; v: number }[];
    pressure_hpa: { t: string; v: number }[];
    gas_kohms: { t: string; v: number }[];
}