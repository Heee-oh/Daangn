package com.daangn.market.repository.region_dong;

import com.daangn.market.domain.Location;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class RegionDongQueryDslRepositoryTest {


    @Autowired
    RegionDongQueryDslRepository repository;

    @Test
    void findMyRegionDong() {
        Integer regionId = repository.findRegionDongByCoordinate(new Location(126.9780d, 37.5665d));
        Assertions.assertThat(regionId).isNotNull();
        Assertions.assertThat(regionId).isEqualTo(20);
        log.info("");


    }
}