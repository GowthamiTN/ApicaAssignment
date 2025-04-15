package com.apica.journal_service.kafka;

import com.apica.journal_service.entity.JournalEvent;
import com.apica.journal_service.service.JournalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Component
public class UserEventConsumer {
    private static final Logger logger = LoggerFactory.getLogger(UserEventConsumer.class);
    @Autowired
    JournalService service;
    private ObjectMapper mapper = new ObjectMapper();

    @KafkaListener(topics = "${kafka.topic}", groupId = "journal-group")
    public void consume(String message) {
        try {

            Map<String, Object> eventMap = mapper.readValue(message, Map.class);
            JournalEvent event = new JournalEvent();
            event.setAction((String) eventMap.get("action"));
            event.setUserId(Long.valueOf(eventMap.get("userId").toString()));
            event.setUsername((String) eventMap.get("username"));
            List<?> roles = (List<?>) eventMap.get("role"); // changed from "roles" to "role"
            if (roles != null && !roles.isEmpty()) {
                Object firstRole = roles.get(0);
                if (firstRole instanceof Map<?, ?> roleMap && roleMap.get("name") != null) {
                    event.setRole(roleMap.get("name").toString());
                } else {
                    event.setRole("UNKNOWN");
                }
            }
            event.setTimestamp((String) eventMap.get("timestamp"));
            logger.info("Saving event for user: {}", eventMap.get("userId"));
            service.saveEvent(event);
        } catch (Exception e) {
            logger.error("Failed to process the event due to an error: {}", e.getMessage(), e);
        }

    }
}
