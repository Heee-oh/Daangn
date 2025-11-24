package com.daangn.market.mapper;

import com.daangn.market.domain.Location;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class RegionMapperTest {

    @Autowired
    RegionMapper mapper;



    @Test
    @DisplayName("현재 내 위치의 행정동 id 값 찾기")
    void findCurrentPosRegionDong() {
        // 판교역 주소
        Integer regionId = mapper.findRegionDongByCoordinate(new Location(127.111231d, 37.394787d)).get();

        Assertions.assertThat(regionId).isNotNull();
        Assertions.assertThat(regionId).isEqualTo(1227);

    }

    @Test
    @DisplayName("현재 잘못된 주소 전달시 null")
    void find_fail_invalidLocation() {
        Assertions.assertThat(
                        mapper.findRegionDongByCoordinate(
                                new Location(127.111231d, 40d)))
                .isEmpty();

    }

}