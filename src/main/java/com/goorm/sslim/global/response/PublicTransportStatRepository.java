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
         weekly_usage_cnt, monthly_cost_won,
         main_mode_bus_pct, main_mode_metro_pct,
         card_usage_pct, info_service_usage_pct,
         access_time_min, transfer_service_usage_pct,
         transfer_count, transfer_move_time_min, transfer_wait_time_min)
        VALUES
        (:regionCode, :regionName,
         :weeklyUsageCnt, :monthlyCostWon,
         :mainModeBusPct, :mainModeMetroPct,
         :cardUsagePct, :infoServiceUsagePct,
         :accessTimeMin, :transferServiceUsagePct,
         :transferCount, :transferMoveTimeMin, :transferWaitTimeMin)
        ON DUPLICATE KEY UPDATE
          region_name                 = VALUES(region_name),
          weekly_usage_cnt            = VALUES(weekly_usage_cnt),
          monthly_cost_won            = VALUES(monthly_cost_won),
          main_mode_bus_pct           = VALUES(main_mode_bus_pct),
          main_mode_metro_pct         = VALUES(main_mode_metro_pct),
          card_usage_pct              = VALUES(card_usage_pct),
          info_service_usage_pct      = VALUES(info_service_usage_pct),
          access_time_min             = VALUES(access_time_min),
          transfer_service_usage_pct  = VALUES(transfer_service_usage_pct),
          transfer_count              = VALUES(transfer_count),
          transfer_move_time_min      = VALUES(transfer_move_time_min),
          transfer_wait_time_min      = VALUES(transfer_wait_time_min),
          fetched_at                  = CURRENT_TIMESTAMP
        """, nativeQuery = true)
    int upsert(
            @Param("regionCode") String regionCode,
            @Param("regionName") String regionName,
            @Param("weeklyUsageCnt") BigDecimal weeklyUsageCnt,
            @Param("monthlyCostWon") BigDecimal monthlyCostWon,
            @Param("mainModeBusPct") BigDecimal mainModeBusPct,
            @Param("mainModeMetroPct") BigDecimal mainModeMetroPct,
            @Param("cardUsagePct") BigDecimal cardUsagePct,
            @Param("infoServiceUsagePct") BigDecimal infoServiceUsagePct,
            @Param("accessTimeMin") BigDecimal accessTimeMin,
            @Param("transferServiceUsagePct") BigDecimal transferServiceUsagePct,
            @Param("transferCount") BigDecimal transferCount,
            @Param("transferMoveTimeMin") BigDecimal transferMoveTimeMin,
            @Param("transferWaitTimeMin") BigDecimal transferWaitTimeMin
    );
}