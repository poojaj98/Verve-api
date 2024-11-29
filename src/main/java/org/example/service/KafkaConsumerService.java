package org.example.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @KafkaListener(topics = "unique-requests-topic", groupId = "test-group")
    public void listen(String message) {
        System.out.println("Received message from Kafka: " + message);
        // You can add your processing logic here
    }
}
