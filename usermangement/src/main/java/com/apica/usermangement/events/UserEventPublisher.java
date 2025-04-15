package com.apica.usermangement.events;

import com.apica.usermangement.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class UserEventPublisher {
    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;
    @Value("${kafka.topic}") private String topic;
    private ObjectMapper mapper = new ObjectMapper();

    public void publishUserEvent(String action, User user) {
        try {
            Map<String, Object> event = Map.of(
                    "action", action,
                    "userId", user.getId(),
                    "username", user.getUsername(),
                    "role", user.getRoles(),
                    "timestamp", Instant.now().toString()
            );
            kafkaTemplate.send(topic, mapper.writeValueAsString(event));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
