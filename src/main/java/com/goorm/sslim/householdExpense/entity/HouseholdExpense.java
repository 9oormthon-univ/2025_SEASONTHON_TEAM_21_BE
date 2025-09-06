package com.goorm.sslim.householdExpense.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "household_expenses",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_age_group", columnNames = {"age_group"})
        })

@Getter
@Setter
public class HouseholdExpense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK

    /**
     * 연령 그룹 (YOUNG, MIDDLE, OLD)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "age_group", nullable = false, length = 10)
    private AgeGroup ageGroup;

    /**
     * Food (식료품비)
     */
    @Column(name = "food_cost")
    private Integer foodCost;

    /**
     * Rent (월세)
     */
    @Column(name = "rent_monthly")
    private Integer rentMonthly;

    /**
     * Housing management fee (주거 관리비)
     */
    @Column(name = "housing_management_fee")
    private Integer housingManagementFee;

    /**
     * Education (교육비)
     */
    @Column(name = "education_cost")
    private Integer educationCost;

    /**
     * Telecom (통신비)
     */
    @Column(name = "telecom_cost")
    private Integer telecomCost;

    /**
     * Transportation (교통비)
     */
    @Column(name = "transportation_cost")
    private Integer transportationCost;

    /**
     * Recreation & Culture (오락·문화비)
     */
    @Column(name = "recreation_culture_cost")
    private Integer recreationCultureCost;
}