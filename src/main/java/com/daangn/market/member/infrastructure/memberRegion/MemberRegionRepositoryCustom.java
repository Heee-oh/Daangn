package com.daangn.market.member.infrastructure.memberRegion;

import com.daangn.market.member.application.dto.MemberRegionResponse;

import java.util.List;

public interface MemberRegionRepositoryCustom {
    List<MemberRegionResponse> findAllByMember(Long memberId);
}
