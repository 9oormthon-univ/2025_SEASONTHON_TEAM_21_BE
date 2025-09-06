package com.goorm.sslim.income.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.goorm.sslim.income.entity.Income;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {

}
