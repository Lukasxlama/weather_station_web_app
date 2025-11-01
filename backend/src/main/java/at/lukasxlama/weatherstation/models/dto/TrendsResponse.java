package at.lukasxlama.weatherstation.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class TrendsResponse
{
    private int bucket_seconds;
    private String from;
    private String to;
    private Map<String, List<Map<String, Object>>> series;
}