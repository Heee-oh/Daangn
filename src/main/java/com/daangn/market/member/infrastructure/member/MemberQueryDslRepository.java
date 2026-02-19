package com.daangn.market.member.infrastructure.member;

import com.daangn.market.common.domain.id.MemberId;
import com.daangn.market.member.application.dto.MemberRegionResponse;
import com.daangn.market.member.application.dto.MemberResponse;
import com.daangn.market.member.domain.QMember;
import com.daangn.market.member.domain.QMemberRegion;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberQueryDslRepository {
    private final JPAQueryFactory queryFactory;
    private final QMember qMember = QMember.member;
    private final QMemberRegion memberRegion = QMemberRegion.memberRegion;

    public MemberResponse findMember(MemberId memberId) {
        MemberResponse result = queryFactory
                .select(Projections.constructor(MemberResponse.class,
                        qMember.nickname,
                        qMember.profileImageUrl,
                        qMember.mannerTemp
                ))
                .from(qMember)
                .where(qMember.memberId.eq(memberId))
                .fetchOne();

        if (result == null) {
            throw new EntityNotFoundException("회원 정보를 찾을 수 없음");
        }

        return result;
    }

    public List<MemberRegionResponse> findMemberRegion(MemberId memberId) {
        List<MemberRegionResponse> result = queryFactory.select(Projections.constructor(MemberRegionResponse.class,
                        memberRegion.MemberRegionId,
                        memberRegion.regionId,
                        memberRegion.primary,
                        memberRegion.verifiedAt
                ))
                .from(memberRegion)
                .where(memberRegion.member.memberId.eq(memberId),
                        memberRegion.primary.isTrue())
                .fetch();

        if (result == null) {
            throw new RuntimeException("내 동네 정보가 없음");
        }

        return result;
    }

}
