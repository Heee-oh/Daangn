package com.daangn.market.common.auth.application.dto;

public record AuthTokenResponse(
        String memberId,
        String accessToken,
        long expiresIn
) {
}

