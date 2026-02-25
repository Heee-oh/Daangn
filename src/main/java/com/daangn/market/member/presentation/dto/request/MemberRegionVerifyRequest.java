package com.daangn.market.member.presentation.dto.request;

import jakarta.validation.constraints.NotNull;

public record MemberRegionVerifyRequest(
        @NotNull Double lat,
        @NotNull Double lng
) {
}
