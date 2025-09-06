package com.goorm.sslim.income.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.goorm.sslim.income.entity.Income;

import java.util.Optional;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {
    /**
     * 특정 그룹명으로 데이터 조회
     * @param group 조회할 그룹명 (예: "1분위", "2분위")
     */
    Optional<Income> findByGroup(String group);
}
