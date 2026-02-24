package com.daangn.market.region.infrastructure;

import com.daangn.market.region.domain.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface RegionJpaRepository extends JpaRepository<Region, Long>, RegionJpaRepositoryCustom {

    @Query(value =
            """
                SELECT 1
                FROM region r
                WHERE r.id = :regionId
                AND ST_Covers(r.geom, ST_SetSRID(ST_MakePoint(:lng, :lat), 4326))
                LIMIT 1
            """
            , nativeQuery = true)
    Integer covers(Integer regionId, double lat, double lng);

    default boolean validateCoordinateInRegion(Integer regionId, double lat, double lng) {
        return covers(regionId, lat, lng) != null;
    }
}
