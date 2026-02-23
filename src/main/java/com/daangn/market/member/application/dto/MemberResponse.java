package com.daangn.market.member.application.dto;

import com.querydsl.core.annotations.QueryProjection;

public record MemberResponse(String nickname, String ProfileImage, int mannerTemp) {
    @QueryProjection
    public MemberResponse {
    }
}
