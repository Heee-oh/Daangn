package com.daangn.market.member.application.dto;

import com.querydsl.core.annotations.QueryProjection;

import java.time.Instant;

public record MemberRegionResponse(
        Long memberId,
        Integer regionId,
        Instant verifiedAt,
        boolean isPrimary,
        String dongnm
) {
    @QueryProjection
    public MemberRegionResponse {
    }
}
