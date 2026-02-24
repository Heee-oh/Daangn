package com.daangn.market.region.infrastructure;

public interface RegionJpaRepositoryCustom {

    /**
     * 현재 위치와 인증하려는 동네 id 값
     *
     * @param regionId
     * @param lat
     * @param lng
     * @return
     */
    boolean validateCoordinateInRegionQD(Integer regionId, double lat, double lng);
}
