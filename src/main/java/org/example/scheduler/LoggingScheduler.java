package org.example.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class LoggingScheduler {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private Set<Integer> uniqueIds = new HashSet<>();

    public void recordRequest(int id) {
        uniqueIds.add(id);
    }

    @Scheduled(cron = "0 * * * * ?")
    public void sendUniqueRequestCountToKafka() {
        int uniqueCount = uniqueIds.size();
        String message = "Unique ID Count: " + uniqueCount;

        // Send the count to Kafka topic "unique-request-count"
        kafkaTemplate.send("unique-request-count", message);

        System.out.println("Sent to Kafka: " + message);

        uniqueIds.clear();
    }
}
