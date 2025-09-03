package com.goorm.sslim.foodcost.repository;

import com.goorm.sslim.foodcost.entity.FoodCost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodCostRepository extends JpaRepository<FoodCost, Long> {

}
