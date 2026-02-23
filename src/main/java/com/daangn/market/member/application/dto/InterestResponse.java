package com.daangn.market.member.application.dto;

import com.querydsl.core.annotations.QueryProjection;

public record InterestResponse(
        Long id,
        Long listingId

) {
    @QueryProjection
    public InterestResponse {
    }
}
