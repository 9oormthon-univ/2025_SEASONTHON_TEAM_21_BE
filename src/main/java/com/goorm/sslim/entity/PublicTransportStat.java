package com.goorm.sslim.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "public_transport_stats",
        uniqueConstraints = @UniqueConstraint(name = "uk_region_code", columnNames = "region_code"))
@Getter
@Setter
public class PublicTransportStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="region_code", nullable=false, length=16)
    private String regionCode;

    @Column(name="region_name", nullable=false, length=20)
    private String regionName;

    // 주간 대중교통 이용 횟수 (기존 precision=5, scale=2 유지: 스키마와 불일치 방지)
    @Column(name="weekly_usage_cnt", precision = 5, scale = 2)
    private BigDecimal weeklyUsageCnt;

    // 한달 평균 대중 교통 비용
    @Column(name="monthly_cost")
    private Integer monthlyCost;
}