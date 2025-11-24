package com.daangn.market.service;

import com.daangn.market.domain.Location;
import com.daangn.market.mapper.RegionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

/**
 * 해당 서비스는 지역관련 비지니스 작업을 처리
 * ex) 동네 인증, 동네 설정 등
 */
@Service
@RequiredArgsConstructor
public class RegionService {
    private final RegionMapper regionMapper;

    public int findRegionIdByLocation(Location location) {
        return regionMapper.findRegionDongByCoordinate(location).orElseThrow(()-> new NoSuchElementException("동네 id가 없음"));

    }


}
