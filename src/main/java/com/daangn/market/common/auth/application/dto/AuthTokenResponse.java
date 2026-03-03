package com.daangn.market.common.auth.application.dto;

public record AuthTokenResponse(
        Long memberId,
        String accessToken,
        long expiresIn
) {
}

