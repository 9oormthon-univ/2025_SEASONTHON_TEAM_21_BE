package com.goorm.sslim.income.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.goorm.sslim.income.entity.Income;
import com.goorm.sslim.income.repository.IncomeRepository;
import com.goorm.sslim.income.service.IncomeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class IncomeController {

	private final IncomeService incomeService;
    private final IncomeRepository incomeRepository;
	
    @GetMapping("/api/income/fetch")
    public List<Income> fetchAndSaveIncomeBoundaries() {
        incomeService.fetchAndSaveIncomeBoundaries();
        return incomeRepository.findAll();
    }

    @GetMapping("/api/income/all")
    public List<Income> getAllIncomeBoundaries() {
        return incomeRepository.findAll();
    }
    
}
