package com.daangn.market.Listing.infrastructure;

import com.daangn.market.Listing.application.dto.ListingResponse;
import com.daangn.market.Listing.application.dto.QListingResponse;
import com.daangn.market.Listing.domain.QListing;
import com.daangn.market.Listing.domain.QListingImage;
import com.daangn.market.Listing.domain.Status;
import com.daangn.market.member.domain.QMember;
import com.daangn.market.member.domain.QMemberRegion;
import com.daangn.market.region.domain.QRegion;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;


@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional
public class ListingJpaRepositoryCustomImpl implements ListingJpaRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    private static final QListing listing = QListing.listing;
    private static final QRegion region = QRegion.region;
    private static final QListingImage image = QListingImage.listingImage;
    private static final QRegion base = new QRegion("base");


    /**
     * 내 행정동 7KM 범위 내 판매글 조회 (작성중 x, 슬라이스)
     * @param memberId 본인 id
     * @param regionId 해당 멤버의 행정동 id
     * @param lastListingId
     * @param size
     * @param pageable
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public Slice<ListingResponse> findListings(Long memberId, Integer regionId, Long lastListingId, int size, Pageable pageable) {
        // 반경 7~10km 주의의 행정동을 뽑고,
        // 해당 행정동의 게시글들을 조회

        // 행정동의 3KM 반경의 행정동 판별
        BooleanExpression isWithin7Km = Expressions.booleanTemplate(
                "function('ST_DWithin', {0}, {1}, {2}) = true",
                region.geom,
                base.geom,
                3000.0d
        );

        List<ListingResponse> fetch
                = queryFactory.select(new QListingResponse(
                        listing.id,
                        listing.sellerId,
                        listing.title,
                        listing.categoryId,
                        listing.price.priceAmount,
                        listing.price.isFree,
                        listing.status,
                        region.dongnm,
                        listing.hopeLocation.regionId,
                        listing.hopeLocation.lat,
                        listing.hopeLocation.lng,
                        listing.viewCount,
                        listing.viewCount,
                        image.imageUrl,
                        listing.updatedAt
                ))
                .from(listing)
                .leftJoin(image)
                .on(listing.id.eq(image.listing.id)
                        .and(image.sortOrder.isNull()
                                .or(image.sortOrder.eq(0)))
                )
                .join(region).on(region.id.eq(listing.regionId))
                .join(base).on(base.id.eq(regionId))
                .where(
                        isWithin7Km,
                        listing.id.lt(lastListingId),
                        listing.isHidden.isFalse(),
                        listing.status.ne(Status.DRAFT)
                )
                .orderBy(listing.id.desc())
                .limit(size + 1)
                .fetch();


        int pageSize = pageable.getPageSize();
        boolean hasNext = fetch.size() > pageSize;

        if (hasNext) {
            fetch.removeLast();
        }

        return new SliceImpl<>(fetch, pageable, hasNext);
    }



}
