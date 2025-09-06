package com.goorm.sslim.income.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "INCOME_BOUNDARY")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Income {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "INCOME_ID")
    private Long incomeId;

	@Column(name = "INCOME_YEAR")
    private String year;       // 과세 연도

    @Column(name = "INCOME_GROUP")
    private String group;      // 분위 이름 (예: 1분위, 2분위…)

    @Column(name = "BOUNDARY")
    private Long boundary;     // 경계값
	
}
