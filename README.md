# Weather Station Web App (Angular + Spring Boot)

Full-stack web interface for the IoT weather station ecosystem.  
This application provides a **Spring Boot backend** for data persistence and REST APIs, and an **Angular frontend** for visualization, trend analysis, and developer debugging — forming the final component in the data chain from **ESP32 → LoRa → MQTT → Web**.

---

## Features

- **Spring Boot REST API**
  - `/api/latest` → latest received sensor data packet
  - `/api/trends` → time-based historical data queries
  - `/api/debug` → local SQL viewer for diagnostics

- **Angular Frontend**
  - Real-time sensor data visualization (temperature, humidity, pressure, gas resistance)
  - Interactive charts with zoom & pan (Chart.js + Zoom Plugin)
  - Developer “Debug Console” for SQL queries
  - Aurora-style responsive dark theme

- **SQLite Database** – simple, local data storage managed by Spring Boot
- **Dockerized Architecture** – build and run both backend & frontend with one command
- **Tight MQTT Integration** – receives data published by the middleware service

---

## Requirements

- **Docker** and **Docker Compose**
- A running MQTT-to-database pipeline from the middleware project  
  (the backend consumes data that has been published to the MQTT broker)

### Expected MQTT Data Structure

The backend expects weather data in the following topic layout:

```
weather_station/json →
{
  "timestamp": "2025-11-01T12:34:56.789Z",
  "rssi_dbm": -95.0,
  "snr_db": 7.25,
  "error": false,
  "error_type": null,
  "sensor_data":
  {
    "temperature_c": 18.3,
    "humidity_pct": 52.4,
    "pressure_hpa": 983.1,
    "gas_kohms": 250.7
  }
}
```

---

## Installation & Usage

1. Clone this repository:
   ```
   git clone https://github.com/Lukasxlama/weather_station_web_app.git
   cd weather_station_web_app
   ```

2. Build and start the containers:
   ```
   docker compose up --build    # Optional detached with `-d` 
   ```

3. Access the services:
   - **Web Interface:** http://localhost:8080  
   - **API (via nginx Reverse Proxy):** http://localhost:8080/api

The backend automatically creates a local SQLite database (`weather.db`) inside the container.

---

## API Overview

| Endpoint | Method | Description |
|-----------|---------|-------------|
| `/api/latest` | GET | Retrieve the most recent weather packet |
| `/api/trends?from=...&to=...` | GET | Retrieve historical measurements for a given time range |
| `/api/debug` | POST | Execute safe, read-only SQL queries for diagnostics |

---

## Environment Configuration (.env)

The backend and middleware can be configured via environment variables.  
An example of the .env file can be found in the .env.example file.

---

## License

MIT License — free to use for personal and commercial projects.

---

## Related Projects

- [weather_station_esp32](https://github.com/Lukasxlama/weather_station_esp32)  
  ESP32 sensor node (BME680 + OLED + optional LoRa transmission)

- [weather_station_middleware](https://github.com/Lukasxlama/weather_station_middleware)  
  LoRa → MQTT bridge for Raspberry Pi, handling data ingestion and broker publishing

- **weather_station_web_app** (this project)  
  Web-based visualization and API access layer for all weather station data
