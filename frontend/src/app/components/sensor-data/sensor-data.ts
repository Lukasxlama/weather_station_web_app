import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import type { SensorDataModel } from '../../models/shared/sensordata';
import { SensorDataItemComponent } from '../sensor-data-item/sensor-data-item';
import { SensorDataIconEnum } from '@app/models/sensor-data-items/sensor-data-icon';
import type { ReceivedPacketModel } from '../../models/shared/receivedpacket';

@Component({
  standalone: true,
  selector: 'app-sensor-data',
  imports: [CommonModule, SensorDataItemComponent],
  templateUrl: './sensor-data.html',
  styleUrl: './sensor-data.css'
})

export class SensorDataComponent
{
  @Input() receivedPacket!: ReceivedPacketModel;
  
  protected get sensorData(): SensorDataModel
  {
    return this.receivedPacket.sensor_data;
  }

  protected getValue(key: string): number | string
  {
    return key == 'timestamp'
      ? this.receivedPacket.timestamp
      : this.receivedPacket.sensor_data[key as keyof typeof this.receivedPacket.sensor_data]
  }

  protected sensorKeys: string[] =
  [
    'temperature_c',
    'humidity_pct',
    'pressure_hpa',
    'gas_kohms',
    'timestamp'
  ] as const;

  protected labels: Record<string, string> =
  {
    temperature_c: 'Temperatur',
    humidity_pct: 'Luftfeuchtigkeit',
    pressure_hpa: 'Luftdruck',
    gas_kohms: 'Gaswiderstand',
    timestamp: 'Zeitpunkt'
  } as const;

  protected units: Record<string, string> =
  {
    temperature_c: '°C',
    humidity_pct: '%',
    pressure_hpa: 'hPa',
    gas_kohms: 'kΩ',
    timestamp: ''
  } as const;

  protected iconMap: Record<string, SensorDataIconEnum> =
  {
    temperature_c: SensorDataIconEnum.temperature,
    humidity_pct: SensorDataIconEnum.humidity,
    pressure_hpa: SensorDataIconEnum.pressure,
    gas_kohms: SensorDataIconEnum.gas_resistance,
    timestamp: SensorDataIconEnum.timestamp
  } as const;
}
