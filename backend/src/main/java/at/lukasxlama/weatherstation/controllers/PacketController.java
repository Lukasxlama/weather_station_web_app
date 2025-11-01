package at.lukasxlama.weatherstation.controllers;

import at.lukasxlama.weatherstation.models.dao.ReceivedPacket;
import at.lukasxlama.weatherstation.models.dto.TrendsResponse;
import at.lukasxlama.weatherstation.repository.ReceivedPacketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.sql.DataSource;
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

    @Autowired
    private DataSource dataSource;

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

    // --------------------------------------------------------------------
    // POST /debug?sql=SELECT * FROM received_packet LIMIT 5
    // --------------------------------------------------------------------
    @PostMapping("/debug")
    public ResponseEntity<?> runDebugQuery(@RequestBody Map<String, String> body)
    {
        String sql = body.get("sql").trim().toLowerCase();
        log.warn("⚙️ Running public DEBUG SQL query: {}", sql);

        Statement stmt;
        try
        {
            stmt = CCJSqlParserUtil.parse(sql);
        }

        catch (JSQLParserException e)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid SQL syntax");
        }

        if (!(stmt instanceof Select))
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only SELECT statements are allowed");
        }

        if (sql.contains("union") || sql.contains(";") || sql.contains("--") || sql.contains("/*"))
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Potentially unsafe SQL detected");
        }

        if (!sql.contains("from received_packet"))
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only queries on 'received_packet' are allowed");
        }

        if(!sql.contains("limit"))
        {
            sql += " LIMIT 9999";
        }

        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement();
             var resultSet = statement.executeQuery(sql))
        {
            var meta = resultSet.getMetaData();
            int columnCount = meta.getColumnCount();

            var columns = new java.util.ArrayList<String>();
            for (int i = 1; i <= columnCount; i++)
            {
                columns.add(meta.getColumnLabel(i));
            }

            var rows = new java.util.ArrayList<java.util.List<Object>>();
            while (resultSet.next())
            {
                var row = new java.util.ArrayList<>();
                for (int i = 1; i <= columnCount; i++)
                {
                    row.add(resultSet.getObject(i));
                }
                rows.add(row);
            }

            var response = Map.of("columns", columns, "rows", rows);
            return rows.isEmpty()
                    ? ResponseEntity.noContent().build()
                    : ResponseEntity.ok(response);
        }

        catch (Exception e)
        {
            log.error("SQL debug query failed", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "SQL execution failed");
        }

    }
}
