package com.daangn.market.member.infrastructure.member;

import com.daangn.market.member.application.dto.MemberRegionResponse;
import com.daangn.market.member.application.dto.MemberResponse;
import com.daangn.market.member.application.dto.QMemberRegionResponse;
import com.daangn.market.member.application.dto.QMemberResponse;
import com.daangn.market.member.domain.QMember;
import com.daangn.market.member.domain.QMemberRegion;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final QMember qMember = QMember.member;
    private final QMemberRegion memberRegion = QMemberRegion.memberRegion;

    @Override
    public MemberResponse findMember(Long memberId) {
        MemberResponse result = queryFactory
                .select(new QMemberResponse(
                        qMember.nickname,
                        qMember.profileImageUrl,
                        qMember.mannerTemp
                ))
                .from(qMember)
                .where(qMember.id.eq(memberId))
                .fetchOne();

        if (result == null) {
            throw new EntityNotFoundException("Member not found");
        }

        return result;
    }
    @Override
    public List<MemberRegionResponse> findMemberRegion(Long memberId) {
        return queryFactory.select(new QMemberRegionResponse(
                        memberRegion.member.id,
                        memberRegion.regionId,
                        memberRegion.verifiedAt,
                        memberRegion.primary,
                        memberRegion.member.nickname
                ))
                .from(memberRegion)
                .where(memberRegion.member.id.eq(memberId),
                        memberRegion.primary.isTrue())
                .fetch();
    }
}
