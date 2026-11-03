package com.college.studentmanagement.controller;

import com.college.studentmanagement.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    private final StudentRepository repository;

    public HealthController(StudentRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/api/health")
    public ResponseEntity<Map<String, Object>> health() {
        try {
            long count = repository.count(); // also verifies the database connection
            return ResponseEntity.ok(Map.of(
                    "application", "UP",
                    "database", "CONNECTED",
                    "totalStudents", count
            ));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                    "application", "UP",
                    "database", "DISCONNECTED",
                    "message", "Check database settings and MySQL service."
            ));
        }
    }
}
