package com.goorm.sslim.repository;

import com.goorm.sslim.entity.PublicTransportStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
public interface PublicTransportStatRepository extends JpaRepository<PublicTransportStat, Long> {

    @Query("""
        select p
        from PublicTransportStat p
        where p.monthlyCost is not null
        order by p.regionName asc
    """)
    List<PublicTransportStat> findAllWithMonthlyCostOrderByNameAsc();

    @Query("""
        SELECT p
        FROM PublicTransportStat p
        WHERE p.regionName = :regionName
    """)
    List<PublicTransportStat> findByRegionName(@Param("regionName") String regionName);
}