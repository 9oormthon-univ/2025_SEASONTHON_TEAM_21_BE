package com.goorm.sslim.income.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.goorm.sslim.income.entity.Income;

import java.util.*;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {
    /**
     * 특정 그룹명으로 데이터 조회
     * @param group 조회할 그룹명 (예: "1분위", "2분위")
     */
    Optional<Income> findByGroup(String group);

    @Query("SELECT i FROM Income i WHERE i.incomeId BETWEEN 1 AND 5 ORDER BY i.incomeId ASC")
    List<Income> findBoundariesForBrackets();

    @Query("SELECT i.boundary FROM Income i WHERE i.incomeId = :incomeId")
    Optional<Long> findBoundaryById(@Param("incomeId") int incomeId);

}
