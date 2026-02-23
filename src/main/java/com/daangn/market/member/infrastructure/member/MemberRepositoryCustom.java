package com.daangn.market.member.infrastructure.member;

import com.daangn.market.member.application.dto.MemberRegionResponse;
import com.daangn.market.member.application.dto.MemberResponse;

import java.util.List;

public interface MemberRepositoryCustom {
    MemberResponse findMember(Long memberId);
    List<MemberRegionResponse> findMemberRegion(Long memberId);
}
