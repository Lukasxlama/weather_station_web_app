package at.lukasxlama.weatherstation.services;

import at.lukasxlama.weatherstation.models.dao.ReceivedPacket;
import at.lukasxlama.weatherstation.repository.ReceivedPacketRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MqttService implements MqttCallback
{
    private final ReceivedPacketRepository packetRepository;
    private final ObjectMapper objectMapper;

    @Value("${mqtt.broker-url}")
    private String mqttBrokerUrl;

    @Value("${mqtt.topic}")
    private String mqttTopic;

    @Value("${mqtt.username:}")
    private String mqttUsername;

    @Value("${mqtt.password:}")
    private String mqttPassword;

    private MqttClient mqttClient;

    @EventListener(ApplicationReadyEvent.class)
    public void connectMqtt()
    {
        log.info("Connecting to MQTT broker at {}", mqttBrokerUrl);

        try
        {
            mqttClient = new MqttClient(mqttBrokerUrl, MqttClient.generateClientId());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(5);

            if (!mqttUsername.isBlank())
            {
                options.setUserName(mqttUsername);
                options.setPassword(mqttPassword.toCharArray());
                log.debug("Using MQTT authentication with user '{}'", mqttUsername);
            }

            mqttClient.setCallback(this);
            mqttClient.connect(options);
            mqttClient.subscribe(mqttTopic);

            log.info("Connected & subscribed to topic '{}'", mqttTopic);
        }

        catch (MqttException e)
        {
            log.error("Failed to initialize MQTT connection", e);
        }
    }

    @PreDestroy
    public void cleanup()
    {
        if (mqttClient != null && mqttClient.isConnected())
        {
            try
            {
                mqttClient.disconnect();
                mqttClient.close();
                log.info("Disconnected from MQTT broker");
            }

            catch (MqttException e)
            {
                log.warn("Error while closing MQTT client", e);
            }
        }
    }

    @Override
    public void connectionLost(Throwable cause)
    {
        log.warn("MQTT connection lost: {}", cause.getMessage());
    }

    @Override
    public void messageArrived(String topic, MqttMessage message)
    {
        try
        {
            String payload = new String(message.getPayload());
            log.debug("Received message: {}", payload);

            ReceivedPacket packet = objectMapper.readValue(payload, ReceivedPacket.class);
            packetRepository.save(packet);

            log.info("Saved new packet at {}", packet.getTimestamp());
        }

        catch (Exception e)
        {
            log.error("Failed to process MQTT message", e);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token)
    {
        // not used for subscriptions
    }
}