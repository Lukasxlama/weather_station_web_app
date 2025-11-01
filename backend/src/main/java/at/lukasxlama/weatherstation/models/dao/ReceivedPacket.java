package at.lukasxlama.weatherstation.models.dao;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "received_packet")
public class ReceivedPacket
{
    @Id
    @Column
    private OffsetDateTime timestamp;

    @Column
    private double rssi_dbm;

    @Column
    private double snr_db;

    @Column
    private boolean error;

    @Column
    private String error_type;

    @Column
    private String raw_hex;

    @Embedded
    private SensorData sensor_data;
}