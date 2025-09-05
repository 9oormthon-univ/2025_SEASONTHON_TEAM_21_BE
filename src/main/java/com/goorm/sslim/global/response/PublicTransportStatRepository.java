package com.goorm.sslim.global.response;

import com.goorm.sslim.entity.PublicTransportStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

public interface PublicTransportStatRepository extends JpaRepository<PublicTransportStat, Long> {

    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO public_transport_stats
        (region_code, region_name,
         weekly_usage_cnt, monthly_cost,
         bus_pct, metro_pct,
         card_usage_pct, info_service_usage_pct,
         access_time, transfer_service_usage_pct,
         transfer_count, transfer_move_time_min, transfer_wait_time_min)
        VALUES
        (:regionCode, :regionName,
         :weeklyUsageCnt, :monthlyCost,
         :busPct, :metroPct,
         :cardUsagePct, :infoServiceUsagePct,
         :accessTime, :transferServiceUsagePct,
         :transferCount, :transferMoveTime, :transferWaitTime)
        ON DUPLICATE KEY UPDATE
          region_name                 = VALUES(region_name),
          weekly_usage_cnt            = VALUES(weekly_usage_cnt),
          monthly_cost                = VALUES(monthly_cost),
          bus_pct                     = VALUES(bus_pct),
          metro_pct                   = VALUES(metro_pct),
          card_usage_pct              = VALUES(card_usage_pct),
          info_service_usage_pct      = VALUES(info_service_usage_pct),
          access_time                 = VALUES(access_time),
          transfer_service_usage_pct  = VALUES(transfer_service_usage_pct),
          transfer_count              = VALUES(transfer_count),
          transfer_move_time_min      = VALUES(transfer_move_time_min),
          transfer_wait_time_min      = VALUES(transfer_wait_time_min),
          fetched_at                  = CURRENT_TIMESTAMP
        """, nativeQuery = true)
    int upsert(
            @Param("regionCode") String regionCode,
            @Param("regionName") String regionName,
            @Param("weeklyUsageCnt") BigDecimal weeklyUsageCnt,   // 엔티티 BigDecimal
            @Param("monthlyCost") Integer monthlyCost,            // 엔티티 Integer
            @Param("busPct") BigDecimal busPct,                   // 엔티티 BigDecimal
            @Param("metroPct") BigDecimal metroPct,               // 엔티티 BigDecimal
            @Param("cardUsagePct") BigDecimal cardUsagePct,
            @Param("infoServiceUsagePct") BigDecimal infoServiceUsagePct,
            @Param("accessTime") BigDecimal accessTime,
            @Param("transferServiceUsagePct") BigDecimal transferServiceUsagePct,
            @Param("transferCount") BigDecimal transferCount,     // 엔티티 BigDecimal
            @Param("transferMoveTime") BigDecimal transferMoveTime,
            @Param("transferWaitTime") BigDecimal transferWaitTime
    );
}