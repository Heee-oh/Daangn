package com.daangn.market.member.application.dto;

import com.daangn.market.common.domain.id.RegionId;

import java.time.Instant;

public record MemberRegionResponse(
        Long MemberRegionId,
        RegionId regionId,
        Instant verifiedAt,
        boolean isPrimary,
        String dongnm
) {
}
