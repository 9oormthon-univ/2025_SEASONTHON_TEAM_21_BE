package com.goorm.sslim.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "public_transport_stats",
        uniqueConstraints = @UniqueConstraint(name = "uk_region_code", columnNames = "region_code"))
@Getter
@Setter
public class PublicTransportStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 구분 아이디

    @Column(name="region_code", nullable=false, length=16)
    private String regionCode; // 지역 영문

    @Column(name="region_name", nullable=false, length=20)
    private String regionName; // 지역 한글

    @Column(name="weekly_usage_cnt", precision = 5, scale = 2)
    private BigDecimal weeklyUsageCnt; // 주간 대중교통 이용 횟수

    @Column(name="monthly_cost")
    private Integer monthlyCost; // 한달 평균 대중 교통 비용

    @Column(name="bus_pct", precision = 5, scale = 2)
    private BigDecimal BusPct; // 버스 이용률

    @Column(name="metro_pct", precision = 5, scale = 2)
    private BigDecimal MetroPct; // 지하철 이용률

    @Column(name="card_usage_pct", precision = 5, scale = 2)
    private BigDecimal cardUsagePct; // 교통카드 사용률

    @Column(name="info_service_usage_pct", precision = 5, scale = 2)
    private BigDecimal infoServiceUsagePct; // 정보제공서비스 이용률

    @Column(name="access_time", precision = 5, scale = 2)
    private BigDecimal accessTime; // 접근 소요 시간

    @Column(name="transfer_service_usage_pct", precision = 5, scale = 2)
    private BigDecimal transferServiceUsagePct; // 환승 이용률

    @Column(name="transfer_count", precision = 5, scale = 2)
    private BigDecimal transferCount; // 환승 횟수

    @Column(name="transfer_move_time_min", precision = 5, scale = 2)
    private BigDecimal transferMoveTime; // 환승 이동 시간

    @Column(name="transfer_wait_time_min", precision = 5, scale = 2)
    private BigDecimal transferWaitTime; // 환승 대기 시간

    @Column(name="fetched_at", insertable=false, updatable=false,
            columnDefinition="timestamp default current_timestamp on update current_timestamp")
    private Timestamp fetchedAt;
}