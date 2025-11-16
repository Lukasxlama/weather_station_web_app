package at.lukasxlama.weatherstation.controllers;

import at.lukasxlama.weatherstation.models.dao.ReceivedPacket;
import at.lukasxlama.weatherstation.models.dto.TrendsResponse;
import at.lukasxlama.weatherstation.repository.ReceivedPacketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class PacketController
{
    private final ReceivedPacketRepository repository;

    // --------------------------------------------------------------------
    // GET /health
    // --------------------------------------------------------------------
    @GetMapping("/health")
    public ResponseEntity<String> health()
    {
        return ResponseEntity.ok("OK");
    }

    // --------------------------------------------------------------------
    // GET /latest
    // --------------------------------------------------------------------
    @GetMapping("/latest")
    public ResponseEntity<ReceivedPacket> getLatestPacket()
    {
        log.debug("📡 Fetching latest packet");

        return repository.findTopByOrderByTimestampDesc()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    // --------------------------------------------------------------------
    // GET /trends?from=...&to=...
    // --------------------------------------------------------------------
    @GetMapping("/trends")
    public ResponseEntity<TrendsResponse> getRange(
            @RequestParam("from") OffsetDateTime from,
            @RequestParam("to") OffsetDateTime to
    )
    {
        log.debug("📈 Fetching packets from {} to {}", from, to);

        var packets = repository.findByTimestampBetweenOrderByTimestampAsc(from, to);
        if (packets.isEmpty()) return ResponseEntity.noContent().build();

        var series = new HashMap<String, List<Map<String, Object>>>();
        series.put("temperature_c", new ArrayList<>());
        series.put("humidity_pct", new ArrayList<>());
        series.put("pressure_hpa", new ArrayList<>());
        series.put("gas_kohms", new ArrayList<>());

        for (var p : packets)
        {
            var t = p.getTimestamp().toString();
            var s = p.getSensor_data();

            if (s == null)
            {
                log.warn("Skipping packet at {} (sensor_data == null)", t);
                continue;
            }

            series.get("temperature_c").add(Map.of("t", t, "v", s.getTemperature_c()));
            series.get("humidity_pct").add(Map.of("t", t, "v", s.getHumidity_pct()));
            series.get("pressure_hpa").add(Map.of("t", t, "v", s.getPressure_hpa()));
            series.get("gas_kohms").add(Map.of("t", t, "v", s.getGas_kohms()));
        }

        return ResponseEntity.ok(
                new TrendsResponse(300, from.toString(), to.toString(), series)
        );
    }
}
