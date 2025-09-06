package com.goorm.sslim.householdExpense.repository;

import com.goorm.sslim.householdExpense.entity.AgeGroup;
import com.goorm.sslim.householdExpense.entity.HouseholdExpense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HouseholdExpenseRepository extends JpaRepository<HouseholdExpense, Long> {
    Optional<HouseholdExpense> findByAgeGroup(AgeGroup ageGroup);
}
