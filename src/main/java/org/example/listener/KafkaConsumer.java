package org.example.listener;

import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;

@EnableKafka
public class KafkaConsumer {

    @KafkaListener(topics = "unique-request-count", groupId = "group_id")
    public void listen(String message) {
        System.out.println("Received from Kafka: " + message);
    }
}
