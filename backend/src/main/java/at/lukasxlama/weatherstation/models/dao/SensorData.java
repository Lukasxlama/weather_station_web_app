package at.lukasxlama.weatherstation.models.dao;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class SensorData
{
    @Column
    private double temperature_c;

    @Column
    private double humidity_pct;

    @Column
    private double pressure_hpa;

    @Column
    private double gas_kohms;
}