package com.goorm.sslim.controller;

import com.goorm.sslim.service.PublicTransportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public-transport")
@RequiredArgsConstructor
public class PublicTransportController {
    private final PublicTransportService service;

    @PostMapping("/ingest")
    public ResponseEntity<String> ingest() {
        service.ingestPivot();
        return ResponseEntity.ok("Ingest completed");
    }
}
