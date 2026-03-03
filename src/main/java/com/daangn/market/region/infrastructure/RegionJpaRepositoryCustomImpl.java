package com.daangn.market.region.infrastructure;

import com.daangn.market.common.util.GeometryUtil;
import com.daangn.market.region.domain.QRegion;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Repository
@Transactional
@RequiredArgsConstructor
public class RegionJpaRepositoryCustomImpl implements RegionJpaRepositoryCustom {

    private final JPAQueryFactory factory;

    private static final QRegion region = QRegion.region;
    @Override
    public boolean validateCoordinateInRegionQD(Integer regionId, BigDecimal lat, BigDecimal lng) {

        Point point = GeometryUtil.createPoint(lng, lat);

        Integer id = factory
                .select(region.id)
                .from(region)
                .where( region.id.eq(regionId),
                        Expressions.booleanTemplate(
                                "function('ST_Covers', {0}, {1}) = true",
                                region.geom,
                                point
                        )
                )
                .fetchFirst();


        return id != null;
    }
}
