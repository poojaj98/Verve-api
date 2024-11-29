package org.example.controller;

import org.example.scheduler.LoggingScheduler;
import org.example.service.UniqueRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/verve")
public class VerveController {
    private final UniqueRequestService uniqueRequestService;

    private final LoggingScheduler loggingScheduler;

    public VerveController(UniqueRequestService uniqueRequestService) {
        this.uniqueRequestService = uniqueRequestService;
        this.loggingScheduler = new LoggingScheduler();
    }

    @GetMapping("/accept")
    public ResponseEntity<String> acceptRequest(@RequestParam int id, @RequestParam(required = false) String endpoint) {
        try {
            uniqueRequestService.recordRequest(id);
            loggingScheduler.recordRequest(id);
            if (endpoint != null) {
                uniqueRequestService.callExternalEndpoint(endpoint);
            }
            return ResponseEntity.ok("ok");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failed");
        }
    }

    @PostMapping("/accept")
    public ResponseEntity<String> acceptPost(@RequestParam int id, @RequestParam(required = false) String endpoint) {
        try {
            uniqueRequestService.recordRequest(id);
            if (endpoint != null) {
                uniqueRequestService.callExternalEndpoint(endpoint);
            }
            return ResponseEntity.ok("ok");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failed");
        }
    }
}
