package com.daangn.market.mapper;

import com.daangn.market.domain.Location;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface RegionMapper {

    Optional<Integer> findRegionDongByCoordinate(@Param("location") Location location);

}
