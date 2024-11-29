# Thought Process: High-Level Overview

## Introduction
In this document, I outline the approach I took to implement the solution for the Verve API. The solution involves building a REST service capable of processing high throughput requests, handling unique request IDs, and interacting with external endpoints while logging the counts of requests. Additionally, extensions involve working with Kafka and distributed systems to ensure scalability and robustness.

## Key Design Considerations

### 1. REST API Design
   - The core functionality revolves around a simple GET endpoint `/api/verve/accept`, which accepts an `id` query parameter and optionally an `endpoint` query parameter.
   - I ensured the API is stateless to be easily scalable under high load conditions.
   - The request ID is used for deduplication and tracking unique requests across different instances of the application.

### 2. Handling High Throughput
   - I used efficient data structures like `Set` to track unique request IDs within each minute, ensuring the service can handle high throughput efficiently.
   - The service is designed to process at least 10K requests per second by using optimal methods for storing and checking the uniqueness of request IDs.

### 3. Logging and Monitoring
   - To meet the requirement of logging unique requests per minute, I integrated a **Scheduled Task** (`@Scheduled`) that runs every minute to log the count of unique requests.
   - I used **standard logging mechanisms** (e.g., SLF4J, Logback) to track the count of unique requests in the logs.

### 4. Extension 1: External HTTP Calls
   - If an `endpoint` query parameter is provided, the application sends an HTTP request (GET/POST) to the specified endpoint with the unique request count as part of the payload.
   - I chose to use `RestTemplate` for simplicity in making HTTP requests to the external endpoint.

### 5. Extension 2: Load Balancer and Id Deduplication
   - To handle cases where multiple instances of the application are behind a load balancer, I implemented **distributed synchronization** using **Kafka** for tracking unique request counts.
   - Kafka ensures that even with load balancing across multiple application instances, the unique request IDs are not duplicated.

### 6. Extension 3: Kafka Integration
   - I integrated **Kafka** as a distributed streaming service to send unique request counts. The service pushes the count of unique requests to a Kafka topic every minute.
   - The **Kafka Producer** is used to send messages to a topic, which can then be consumed by any system that listens to that topic.
   - This provides a scalable way to collect metrics in real-time and allows integration with other systems that require the request data.

## Technologies and Tools
- **Spring Boot**: Used for building the RESTful API, with features such as scheduled tasks and integration with external services.
- **Kafka**: A distributed streaming platform to handle communication between services and track unique requests across distributed systems.
- **RestTemplate**: Used for making HTTP requests to external endpoints.
- **Logback/SLF4J**: For logging purposes.
- **GitHub**: For hosting the source code and making it accessible for others to view.

## Challenges and Considerations
- Ensuring **id deduplication** across multiple instances of the service was a challenge. I addressed it using Kafka to maintain a global view of unique IDs across all instances.
- Handling high throughput and ensuring the service can scale effectively was key. I optimized data storage and retrieval methods to handle 10K requests per second.
- Integration with external endpoints and the need to log unique requests required careful design to avoid unnecessary delays in processing.

## Conclusion
This approach ensures that the application meets the functional requirements of logging unique requests, making external HTTP calls, and handling high request throughput. The Kafka integration for distributed tracking and logging enhances scalability, and using Spring Boot's scheduling capabilities ensures that the application can efficiently manage periodic tasks.
