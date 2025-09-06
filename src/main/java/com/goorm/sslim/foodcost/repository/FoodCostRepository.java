package com.goorm.sslim.foodcost.repository;

import com.goorm.sslim.foodcost.entity.FoodCost;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface FoodCostRepository extends JpaRepository<FoodCost, String> {

    List<FoodCost> findAllByIdIn(Collection<String> ids);
}
