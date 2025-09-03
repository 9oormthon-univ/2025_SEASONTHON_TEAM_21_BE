package com.goorm.sslim.housingCost.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.goorm.sslim.housingCost.entity.HousingCost;

@Repository
public interface HousingCostRepository extends JpaRepository<HousingCost, Long> {

}
