package com.daangn.market.repository.post;

import com.daangn.market.domain.*;
import com.daangn.market.dto.response.PostDetailDto;
import com.daangn.market.dto.response.PostListDto;
import com.daangn.market.dto.response.QPostListDto;
import com.daangn.market.util.GeometryUtil;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostQueryDslRepository {

    private final JPAQueryFactory factory;
    private final QPost qPost = QPost.post;
    private final QMember qMember = QMember.member;
    private final QRegionDong qRegionDong = QRegionDong.regionDong;
    private final QMemberSelectRegion qMemberSelectRegion = QMemberSelectRegion.memberSelectRegion;
    private final QPostImages qPostImages = QPostImages.postImages;

    // post Detail 조회
    public Optional<PostDetailDto> findPostDetailById(long postId, Location location) {
        Point point = GeometryUtil.createPoint(location.getLongitude(), location.getLatitude());
        NumberTemplate<Double> distance = Expressions.numberTemplate(
                Double.class,
                "ST_Distance_Sphere({0}, {1})",
                qPost.location,
                point
        );

        NumberTemplate<Double> locationLat = Expressions.numberTemplate(
                Double.class, "ST_Y({0})", qPost.location
        );

        NumberTemplate<Double> locationLon = Expressions.numberTemplate(
                Double.class, "ST_X({0})", qPost.location
        );

        return Optional.ofNullable(factory
                .select(Projections.constructor(
                        PostDetailDto.class,
                        qPost.id,
                        qPost.memberId,
                        qMember.profileUrl,
                        qRegionDong.dongName,
                        qMember.mannerScore,
                        qPost.title,
                        qPost.price,
                        qPost.category,
                        qPost.lastModifiedAt,
                        qPost.body,
                        qPost.likeCnt,
                        qPost.view,
                        qPost.chatCnt,
                        qPost.state,
                        distance,
                        locationLat,
                        locationLon
                ))
                .from(qPost)
                .join(qMember).on(qPost.memberId.eq(qMember.id))
                .join(qRegionDong).on(qPost.regionId.eq(qRegionDong.id))
                .leftJoin(qPostImages).on(qPost.id.eq(qPostImages.postId))
                .where(qPost.id.eq(postId))
                .fetchOne());
    }

    // post List 조회
    public Slice<PostListDto> findPostsByDongIds(List<Integer> dongIds, long lastPostId, LocalDateTime lastPostDate, Pageable pageable) {
        // 유저가 선택한 동네, 범위 크기 검색 (먼저 동네들을 검색해서 id 값을 추출)

        // postImage는 sortorder 0번째를 선택
        NumberTemplate<Double> locationLat = Expressions.numberTemplate(
                Double.class, "ST_Y({0})", qPost.location
        );

        NumberTemplate<Double> locationLon = Expressions.numberTemplate(
                Double.class, "ST_X({0})", qPost.location
        );

        List<PostListDto> content = factory
                .select(new QPostListDto(
                        qPost.id,
                        qPost.regionId,
                        qPostImages.imgSrc,
                        qPost.title,
                        qPost.lastModifiedAt,
                        qPost.price,
                        qPost.likeCnt,
                        qPost.chatCnt,
                        qPost.state,
                        qRegionDong.dongName,
                        locationLon,
                        locationLat
                ))
                .from(qPost)
                .join(qPostImages).on(qPost.id.eq(qPostImages.postId))
                .join(qRegionDong).on(qPost.regionId.eq(qRegionDong.id))
                .where(qRegionDong.id.in(dongIds),
                        isThumbnail(),
                        cursorIdAndDate(lastPostId, lastPostDate)
                )
                .orderBy(qPost.lastModifiedAt.desc(), qPost.id.desc())
                .limit(pageable.getPageSize() + 1) // 다음 페이지 확인용 1개 추가로 더 조회
                .fetch();


        // 다음 페이지 여부 확인
        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }


        return new SliceImpl<>(content, pageable, hasNext);
    }

    private BooleanExpression isThumbnail() {
        return qPostImages.sortOrder.eq(0);
    }

    // 복합 커서 , 시간 기준, id 기준
    private BooleanExpression cursorIdAndDate(Long lastPostId, LocalDateTime lastPostDate) {
        if (lastPostId == null || lastPostDate == null) {
            return null; // 이로써 첫 페이지 선택됨
        }

        return qPost.lastModifiedAt.lt(lastPostDate)
                .or(qPost.lastModifiedAt.eq(lastPostDate)
                        .and(qPost.id.lt(lastPostId))
                );
    }


}
