package org.example.service;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;

@Service
public class UniqueRequestService {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    private final Set<Integer> uniqueRequests = ConcurrentHashMap.newKeySet();
    private final Logger logger = LoggerFactory.getLogger(UniqueRequestService.class);
    private static final String TOPIC_NAME = "unique-requests-topic";

    public void recordRequest(int id) {
        uniqueRequests.add(id);
    }
    @Scheduled(fixedRate = 60000)
    public void logUniqueRequestCount() {
        int count = uniqueRequests.size();
        logger.info("Unique requests in the last minute: {}", count);
        // Send the unique request count to Kafka
        String message = "{ \"uniqueRequestCount\": " + count + " }";
        kafkaTemplate.send(TOPIC_NAME, message);

        // Clear the unique requests after sending the count
        uniqueRequests.clear();
    }
    public void callExternalEndpoint(String endpoint) {
        int uniqueCount = uniqueRequests.size(); // Get the current count
        RestTemplate restTemplate = new RestTemplate();

        // Create the JSON payload
        Map<String, Object> payload = new HashMap<>();
        payload.put("uniqueRequestCount", uniqueCount);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(endpoint, requestEntity, String.class);
            logger.info("Called endpoint: {}, Payload: {}, HTTP Status: {}",
                    endpoint, payload, response.getStatusCode());
        } catch (Exception e) {
            logger.error("Error calling endpoint: {}, Error: {}", endpoint, e.getMessage(), e);
        }
    }
}
