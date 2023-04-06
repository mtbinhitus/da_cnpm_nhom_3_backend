package com.example.examservice.controller;

import com.example.examservice.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/health-check")
public class HealthCheckController {
    @GetMapping()
    public ResponseEntity<?> getAPIHealthCheck(){
        return ResponseUtils.success("API Version: 1.0.0");
    }
}
