package com.goorm.sslim.region.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.goorm.sslim.region.entity.Region;

public interface RegionRepository extends JpaRepository<Region, Long> {

	Optional<Region> findByLawdCd(String lawdCd);
	
}
