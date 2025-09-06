package com.goorm.sslim.controller;

import com.goorm.sslim.dto.HouseholdExpenseResponse;
import com.goorm.sslim.entity.AgeGroup;
import com.goorm.sslim.entity.HouseholdExpense;
import com.goorm.sslim.repository.HouseholdExpenseRepository;
import com.goorm.sslim.service.HouseholdExpenseIngestService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/household-expenses")
@RequiredArgsConstructor
public class HouseholdExpenseController {

    private final HouseholdExpenseIngestService ingestService;
    private final HouseholdExpenseRepository repo;

    // 1) 최신 데이터 적재 트리거
    @PostMapping("/ingest")
    public String ingestData() {
        ingestService.ingestLatest();
        return "Household expense data ingested successfully.";
    }

    @GetMapping
    public List<HouseholdExpenseResponse> listAll() {
        return repo.findAll().stream().map(HouseholdExpenseResponse::from).toList();
    }

    @GetMapping("/{ageGroup}")
    public HouseholdExpenseResponse getOne(@PathVariable AgeGroup ageGroup) {
        return repo.findByAgeGroup(ageGroup)
                .map(HouseholdExpenseResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}