package com.daangn.market.member.application;

import java.math.BigDecimal;

public interface MemberRegionCommandService {

    void addMemberRegion(Integer regionId, Long memberId);

    void verifyMemberRegion(Long memberRegionId, Long memberId, BigDecimal lat, BigDecimal lng);

    void verifyMemberRegionByRegionId(Integer regionId, Long memberId, BigDecimal lat, BigDecimal lng);
}
