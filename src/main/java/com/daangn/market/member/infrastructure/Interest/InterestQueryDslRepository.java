package com.daangn.market.member.infrastructure.Interest;

import com.daangn.market.common.domain.id.MemberId;
import com.daangn.market.member.application.dto.InterestResponse;
import com.daangn.market.member.domain.QInterest;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class InterestQueryDslRepository implements InterestRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    private final QInterest qInterest = QInterest.interest;

    @Override
    public Slice<InterestResponse> findAllByMemberId(MemberId memberId, Pageable pageable) {
        int pageSize = pageable.getPageSize();

        queryFactory.select(Projections.constructor(InterestResponse.class,
                        qInterest.id,
                        qInterest.listingId
                )).from(qInterest)
                .where(qInterest.member.memberId.eq(memberId))
                // TODO 여기 수정해야함
                .fetchOne();

        return null;
    }
}
