package com.daangn.market.repository.post;

import com.daangn.market.domain.*;
import com.daangn.market.dto.response.PostDetailDto;
import com.daangn.market.util.GeometryUtil;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final QPost qPost = QPost.post;
    private final QMember qMember = QMember.member;
    private final QRegionDong qRegionDong = QRegionDong.regionDong;
    private final QMemberSelectRegion qMemberSelectRegion = QMemberSelectRegion.memberSelectRegion;
    private final QPostImages qPostImages = QPostImages.postImages;

    // mybatis로 처리하자
    public Optional<PostDetailDto> findPostDetailById(long id, Location location) {
        Point point = GeometryUtil.createPoint(location.getLongitude(), location.getLatitude());


        return null;
    }




}
