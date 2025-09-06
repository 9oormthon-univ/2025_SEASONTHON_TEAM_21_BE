package com.goorm.sslim.repository;

import com.goorm.sslim.entity.AgeGroup;
import com.goorm.sslim.entity.HouseholdExpense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HouseholdExpenseRepository extends JpaRepository<HouseholdExpense, Long> {
    Optional<HouseholdExpense> findByAgeGroup(AgeGroup ageGroup);
}
