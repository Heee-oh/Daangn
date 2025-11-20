package com.daangn.market.repository.region_dong;

import com.daangn.market.domain.RegionDong;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionDongRepository extends JpaRepository<RegionDong, Integer> {

    boolean existsRegionDongById(int id);

}
