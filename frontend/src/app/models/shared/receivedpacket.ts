import type { SensorDataModel } from "@app/models/shared/sensordata";

/**
 * Represents a packet received from the MQTT broker, optionally containing sensor data.
 */
export interface ReceivedPacketModel
{
    /** ISO-8601 timestamp of the packet */
    timestamp: string;

    /** Indicates if decoding failed */
    error: boolean;

    /** Error type if applicable */
    error_type?: string | null;

    /** Raw hex payload (if error occurred) */
    raw_hex?: string | null;

    /** RSSI in dBm */
    rssi_dbm: number;

    /** SNR in dB */
    snr_db: number;

    /** Nested sensor data */
    sensor_data: SensorDataModel;
}