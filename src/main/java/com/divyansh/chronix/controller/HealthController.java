package com.divyansh.chronix.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class HealthController {

    private final DataSource dataSource;

    public HealthController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/api/health")
    public Map<String, Object> health() {

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("status", "UP");
        response.put("service", "Chronix");

        try (Connection connection = dataSource.getConnection()) {

            if (connection.isValid(2)) {
                response.put("database", "UP");
            } else {
                response.put("database", "DOWN");
                response.put("status", "DEGRADED");
            }

        } catch (Exception e) {

            response.put("database", "DOWN");
            response.put("status", "DEGRADED");
        }

        return response;
    }
}