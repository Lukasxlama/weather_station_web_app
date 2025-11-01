/**
 * Represents raw sensor readings.
 */
export interface SensorDataModel
{
    /** Temperature in °C */
    temperature_c: number;

    /** Humidity in % */
    humidity_pct: number;

    /** Pressure in hPa */
    pressure_hpa: number;

    /** Gas resistance in kΩ */
    gas_kohms: number;
}
