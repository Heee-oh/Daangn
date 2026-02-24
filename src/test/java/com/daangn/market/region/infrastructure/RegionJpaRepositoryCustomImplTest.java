package com.daangn.market.region.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
class RegionJpaRepositoryCustomImplTest {

    @Autowired
    RegionJpaRepository regionJpaRepository;

    // 126.9780, 37.5665 id 20 을 기준으로 테스트

    @Test
    @DisplayName("서울시청 좌표가 regionId 20에 포함되면 true를 반환한다")
    void validateCoordinateInRegionReturnsTrueWhenPointIsCoveredByRegion() {
        // given
        double lat = 37.5665;
        double lng = 126.9780;
        Integer regionId = 20;

        // when
        boolean result = regionJpaRepository.validateCoordinateInRegion(regionId, lat, lng);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("멀리 떨어진 좌표가 regionId 20에 포함되지 않으면 false를 반환한다")
    void validateCoordinateInRegionReturnsFalseWhenPointIsOutsideRegion() {
        // given
        double lat = 35.1796;
        double lng = 129.0756; // 부산
        Integer regionId = 20;

        // when
        boolean result = regionJpaRepository.validateCoordinateInRegion(regionId, lat, lng);

        // then
        assertThat(result).isFalse();
    }
}