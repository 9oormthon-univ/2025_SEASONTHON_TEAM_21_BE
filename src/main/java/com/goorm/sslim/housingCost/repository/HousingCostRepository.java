package com.goorm.sslim.housingCost.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.goorm.sslim.housingCost.entity.HousingCost;

public interface HousingCostRepository extends JpaRepository<HousingCost, Long> {

}
