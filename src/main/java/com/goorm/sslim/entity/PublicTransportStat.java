package com.goorm.sslim.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "public_transport_stats",
        uniqueConstraints = @UniqueConstraint(columnNames = {"metric_code", "region_name"}))
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

    @Column(name="weekly_usage_cnt")
    private BigDecimal weeklyUsageCnt;

    @Column(name="monthly_cost_won")
    private BigDecimal monthlyCostWon;

    @Column(name="main_mode_bus_pct")
    private BigDecimal mainModeBusPct;

    @Column(name="main_mode_metro_pct")
    private BigDecimal mainModeMetroPct;

    @Column(name="card_usage_pct")
    private BigDecimal cardUsagePct;

    @Column(name="info_service_usage_pct")
    private BigDecimal infoServiceUsagePct;

    @Column(name="access_time_min")
    private BigDecimal accessTimeMin;

    @Column(name="transfer_service_usage_pct")
    private BigDecimal transferServiceUsagePct;

    @Column(name="transfer_count")
    private BigDecimal transferCount;

    @Column(name="transfer_move_time_min")
    private BigDecimal transferMoveTimeMin;

    @Column(name="transfer_wait_time_min")
    private BigDecimal transferWaitTimeMin;

    @Column(name="fetched_at", insertable=false, updatable=false,
            columnDefinition="timestamp default current_timestamp on update current_timestamp")
    private Timestamp fetchedAt;
}