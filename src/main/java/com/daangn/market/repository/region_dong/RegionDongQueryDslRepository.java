package com.daangn.market.repository.region_dong;

import com.daangn.market.domain.Location;
import com.daangn.market.domain.Member;
import com.daangn.market.domain.QMemberSelectRegion;
import com.daangn.market.domain.QRegionDong;
import com.daangn.market.util.GeometryUtil;
import com.querydsl.core.types.dsl.BooleanTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RegionDongQueryDslRepository {

    private final JPAQueryFactory factory;

    private final QMemberSelectRegion qMemberSelectRegion = QMemberSelectRegion.memberSelectRegion;
    private final QRegionDong qRegionDong = QRegionDong.regionDong;

    public List<Integer> findRegionIds(Member member) {
       // 사용자가 설정한 동네에서 어떠한 조건 혹은 특정 원안의 동네들의 id값을 구해야함


        return null;
    }

    // 현재 위치에 해당하는 동네 id 조회
    public Integer findRegionDongByCoordinate(Location location) {
        Point point = GeometryUtil.createPoint(location.getLongitude(), location.getLatitude());

        BooleanTemplate isContains = Expressions.booleanTemplate(
                "ST_Contains({0}, {1})",
                qRegionDong.geom,
                point
        );

        return factory
                .select(qRegionDong.id)
                .from(qRegionDong)
                .where(isContains)
                .fetchOne();
    }

}
