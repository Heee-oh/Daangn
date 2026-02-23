package com.daangn.market.member.infrastructure.memberRegion;

import com.daangn.market.member.application.dto.MemberRegionResponse;
import com.daangn.market.member.application.dto.QMemberRegionResponse;
import com.daangn.market.member.domain.QMemberRegion;
import com.daangn.market.region.domain.QRegion;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberRegionRepositoryCustomImpl implements MemberRegionRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QMemberRegion memberRegion = QMemberRegion.memberRegion;
    private final QRegion region = QRegion.region;

    @Override
    public List<MemberRegionResponse> findAllByMember(Long memberId) {

        return queryFactory
                .select(new QMemberRegionResponse(
                        memberRegion.id,
                        memberRegion.regionId,
                        memberRegion.verifiedAt,
                        memberRegion.primary,
                        region.dongnm
                ))
                .from(memberRegion)
                .join(region).on(memberRegion.regionId.eq(region.id))
                .where(memberRegion.member.id.eq(memberId))
                .fetch();
    }
}
